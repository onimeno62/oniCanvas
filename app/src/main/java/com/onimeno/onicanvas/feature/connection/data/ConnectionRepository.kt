package com.onimeno.onicanvas.feature.connection.data

import com.onimeno.onicanvas.core.designsystem.components.OniStatus
import com.onimeno.onicanvas.feature.connection.state.ConnectionHost
import com.onimeno.onicanvas.feature.connection.state.ConnectionLog
import com.onimeno.onicanvas.feature.connection.state.ConnectionPhase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap


data class ConnectionSnapshot(
    val status: OniStatus,
    val phase: ConnectionPhase,
    val hostIp: String,
    val hostPort: Int,
    val transportType: String,
    val activeHostName: String?,
    val latencyMs: Int?,
    val reconnectAttempt: Int,
    val pairedHosts: List<ConnectionHost>,
    val discoveredHosts: List<ConnectionHost>,
    val connectionLogs: List<ConnectionLog>
)

class ConnectionRepository(
    private val connectionProbe: ConnectionProbe = ConnectionProbe(),
    private val discovery: ConnectionDiscovery = ConnectionDiscovery()
) {
    private val pairedHosts = listOf(
        ConnectionHost("studio_pc", "Studio-PC", "192.168.1.142", "10 mins ago", isAvailable = true),
        ConnectionHost("office_laptop", "Office-MacBook", "192.168.1.201", "Yesterday", isAvailable = false)
    )

    private var discoveredHosts: List<ConnectionHost> = emptyList()
    private val logs = mutableListOf<ConnectionLog>()
    private val transport = ConnectionTransport()
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val connectionMutex = Mutex()
    private val heartbeatSentAt = ConcurrentHashMap<String, Long>()
    private var heartbeatJob: Job? = null
    private var reconnectJob: Job? = null
    private var reconnectGeneration = 0
    private var lastHost: ConnectionHost? = null
    private var missedHeartbeats = 0

    val incomingMessages: SharedFlow<OniCanvasMessage> = transport.incomingMessages
    val commandService = OniCanvasCommandService { frame -> sendMessage(frame) }

    private val _state = MutableStateFlow(
        ConnectionSnapshot(
            status = OniStatus.OFFLINE,
            phase = ConnectionPhase.DISCONNECTED,
            hostIp = "—",
            hostPort = ConnectionProbe.DEFAULT_PORT,
            transportType = "—",
            activeHostName = null,
            latencyMs = null,
            reconnectAttempt = 0,
            pairedHosts = pairedHosts,
            discoveredHosts = emptyList(),
            connectionLogs = emptyList()
        )
    )
    val state: StateFlow<ConnectionSnapshot> = _state.asStateFlow()

    init {
        addLog("Connection manager ready", "INFO")
        repositoryScope.launch {
            transport.incomingMessages
                .catch { exception ->
                    addLog("Receive error: ${exception.message ?: "unknown error"}", "ERROR")
                    publish()
                }
                .collect(::handleIncomingMessage)
        }
        repositoryScope.launch {
            transport.disconnected.collect { handleUnexpectedDisconnect() }
        }
        publish()
    }

    suspend fun disconnect() {
        reconnectJob?.cancel()
        reconnectJob = null
        reconnectGeneration++
        heartbeatJob?.cancel()
        heartbeatJob = null
        missedHeartbeats = 0
        transport.close()
        addLog("Disconnected by user", "INFO")
        publish(
            status = OniStatus.OFFLINE,
            phase = ConnectionPhase.DISCONNECTED,
            activeHostName = null,
            hostIp = "—",
            hostPort = ConnectionProbe.DEFAULT_PORT,
            transportType = "—",
            latencyMs = null,
            reconnectAttempt = 0
        )
    }

    suspend fun connectToHost(hostId: String) {
        reconnectJob?.cancel()
        reconnectJob = null
        val host = findHost(hostId)
        if (host == null) {
            addLog("Host ID not found: $hostId", "ERROR")
            publish(status = OniStatus.ERROR, phase = ConnectionPhase.ERROR)
            return
        }
        connect(host, reconnectAttempt = 0)
    }

    suspend fun scanNetwork() {
        publish(status = OniStatus.INFO, phase = ConnectionPhase.SEARCHING, reconnectAttempt = 0)
        addLog("Scanning local network for companion services...", "INFO")
        val results = runCatching { discovery.scan() }.getOrElse { exception ->
            addLog("Discovery failed: ${exception.message ?: "unknown error"}", "WARNING")
            emptyList()
        }
        discoveredHosts = mergeHosts(results)
        addLog("Discovery complete: ${discoveredHosts.size} companion(s) found", "SUCCESS")
        publish(phase = if (isConnected()) ConnectionPhase.CONNECTED else ConnectionPhase.DISCONNECTED)
    }

    suspend fun sendMessage(message: String): Boolean {
        val sent = transport.sendFrame(message)
        addLog(if (sent) "TX: $message" else "Send failed: $message", if (sent) "INFO" else "ERROR")
        if (!sent && isConnected()) handleUnexpectedDisconnect()
        else publish()
        return sent
    }

    suspend fun send(message: OniCanvasMessage): Boolean {
        val sent = transport.send(message)
        addLog(if (sent) "TX: ${message.type}" else "Send failed: ${message.type}", if (sent) "INFO" else "ERROR")
        if (!sent && isConnected()) handleUnexpectedDisconnect()
        else publish()
        return sent
    }

    fun clearLogs() {
        logs.clear()
        addLog("Logs cleared", "INFO")
        publish()
    }

    fun close() {
        reconnectJob?.cancel()
        heartbeatJob?.cancel()
        transport.shutdown()
        repositoryScope.cancel()
        heartbeatSentAt.clear()
        lastHost = null
    }

    private suspend fun connect(host: ConnectionHost, reconnectAttempt: Int) {
        connectionMutex.withLock {
            heartbeatJob?.cancel()
            missedHeartbeats = 0
            publish(
                status = OniStatus.INFO,
                phase = if (reconnectAttempt > 0) ConnectionPhase.RECONNECTING else ConnectionPhase.CONNECTING,
                activeHostName = host.name,
                hostIp = host.ipAddress,
                hostPort = host.port,
                transportType = "TCP",
                reconnectAttempt = reconnectAttempt
            )
            addLog(
                if (reconnectAttempt > 0) "Reconnect attempt $reconnectAttempt/5 to ${host.name}" else "Connecting to ${host.name} (${host.ipAddress}:${host.port})",
                "INFO"
            )

            when (val result = connectionProbe.connect(host.ipAddress, host.port)) {
                is ConnectionProbe.ProbeResult.Success -> {
                    lastHost = host
                    transport.attach(result.socket)
                    missedHeartbeats = 0
                    heartbeatSentAt.clear()
                    addLog("TCP connection established with ${host.name}", "SUCCESS")
                    addLog("Initial connection latency: ${result.latencyMs}ms", "SUCCESS")
                    publish(
                        status = OniStatus.SUCCESS,
                        phase = ConnectionPhase.CONNECTED,
                        activeHostName = host.name,
                        hostIp = host.ipAddress,
                        hostPort = host.port,
                        transportType = "TCP",
                        latencyMs = result.latencyMs,
                        reconnectAttempt = 0
                    )
                    startHeartbeat()
                }
                is ConnectionProbe.ProbeResult.Failure -> {
                    addLog("Connection failed: ${result.reason}", "ERROR")
                    publish(status = OniStatus.OFFLINE, phase = ConnectionPhase.DISCONNECTED, latencyMs = null, reconnectAttempt = reconnectAttempt)
                    if (reconnectAttempt > 0) transport.close()
                }
            }
        }
    }

    private fun startHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = repositoryScope.launch {
            while (transport.isConnected()) {
                delay(5_000)
                if (!transport.isConnected()) break

                val heartbeat = OniCanvasMessage.heartbeat()
                heartbeatSentAt[heartbeat.id] = System.currentTimeMillis()
                if (!transport.send(heartbeat)) {
                    missedHeartbeats++
                } else {
                    missedHeartbeats++
                }

                if (missedHeartbeats >= 3) {
                    addLog("Heartbeat timeout: connection considered lost", "ERROR")
                    handleUnexpectedDisconnect()
                    break
                }
                publish()
            }
        }
    }

    private fun handleIncomingMessage(message: OniCanvasMessage) {
        when (message.type) {
            OniCanvasMessage.Type.HEARTBEAT_ACK -> {
                val sentAt = heartbeatSentAt.remove(message.id)
                if (sentAt != null) {
                    missedHeartbeats = 0
                    val latency = (System.currentTimeMillis() - sentAt).toInt()
                    publish(latencyMs = latency)
                    addLog("Heartbeat acknowledged: ${latency}ms", "SUCCESS")
                }
            }
            OniCanvasMessage.Type.HEARTBEAT -> {
                repositoryScope.launch { send(OniCanvasMessage.heartbeatAck(message.id)) }
            }
            OniCanvasMessage.Type.EVENT,
            OniCanvasMessage.Type.RESPONSE,
            OniCanvasMessage.Type.ERROR,
            OniCanvasMessage.Type.DISCOVERY_RESPONSE,
            OniCanvasMessage.Type.COMMAND,
            OniCanvasMessage.Type.DISCOVERY_REQUEST -> {
                addLog("RX ${message.type.name}: ${message.payload}", if (message.type == OniCanvasMessage.Type.ERROR) "ERROR" else "INFO")
                publish()
            }
        }
    }

    private fun handleUnexpectedDisconnect() {
        if (lastHost == null || _state.value.phase == ConnectionPhase.DISCONNECTED) return
        heartbeatJob?.cancel()
        heartbeatJob = null
        transport.close()
        val host = lastHost ?: return
        reconnectJob?.cancel()
        val generation = ++reconnectGeneration
        reconnectJob = repositoryScope.launch {
            val delays = longArrayOf(0, 2_000, 5_000, 10_000, 10_000)
            for (attempt in 1..5) {
                if (generation != reconnectGeneration) return@launch
                delay(delays[attempt - 1])
                connect(host, attempt)
                if (isConnected()) {
                    addLog("Reconnected to ${host.name}", "SUCCESS")
                    return@launch
                }
            }
            if (generation == reconnectGeneration) {
                addLog("Automatic reconnect exhausted after 5 attempts", "ERROR")
                publish(status = OniStatus.ERROR, phase = ConnectionPhase.ERROR, reconnectAttempt = 5, latencyMs = null)
            }
        }
    }

    private fun findHost(hostId: String): ConnectionHost? =
        (discoveredHosts + pairedHosts).firstOrNull { it.id == hostId || "${it.ipAddress}:${it.port}" == hostId }

    private fun mergeHosts(results: List<ConnectionHost>): List<ConnectionHost> {
        val merged = linkedMapOf<String, ConnectionHost>()
        pairedHosts.forEach { merged[it.id] = it.copy(isAvailable = false) }
        results.forEach { merged[it.id] = it }
        return merged.values.toList()
    }

    private fun isConnected(): Boolean = transport.isConnected()

    private fun addLog(message: String, level: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        logs.add(0, ConnectionLog(timestamp, message, level))
        if (logs.size > MAX_LOG_ENTRIES) logs.removeLast()
    }

    private fun publish(
        status: OniStatus = _state.value.status,
        phase: ConnectionPhase = _state.value.phase,
        activeHostName: String? = _state.value.activeHostName,
        hostIp: String = _state.value.hostIp,
        hostPort: Int = _state.value.hostPort,
        transportType: String = _state.value.transportType,
        latencyMs: Int? = _state.value.latencyMs,
        reconnectAttempt: Int = _state.value.reconnectAttempt
    ) {
        _state.value = ConnectionSnapshot(
            status = status,
            phase = phase,
            hostIp = hostIp,
            hostPort = hostPort,
            transportType = transportType,
            activeHostName = activeHostName,
            latencyMs = latencyMs,
            reconnectAttempt = reconnectAttempt,
            pairedHosts = pairedHosts,
            discoveredHosts = discoveredHosts,
            connectionLogs = logs.toList()
        )
    }

    private companion object {
        const val MAX_LOG_ENTRIES = 250
    }
}
