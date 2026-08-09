package com.onimeno.onicanvas.feature.connection.data

import com.onimeno.onicanvas.core.designsystem.components.OniStatus
import com.onimeno.onicanvas.feature.connection.state.ConnectionHost
import com.onimeno.onicanvas.feature.connection.state.ConnectionLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ConnectionSnapshot(
    val status: OniStatus,
    val hostIp: String,
    val transportType: String,
    val activeHostName: String?,
    val pairedHosts: List<ConnectionHost>,
    val discoveredHosts: List<ConnectionHost>,
    val connectionLogs: List<ConnectionLog>
)

class ConnectionRepository(
    private val connectionProbe: ConnectionProbe = ConnectionProbe()
) {
    private val pairedHosts = listOf(
        ConnectionHost("studio_pc", "Studio-PC", "192.168.1.142", "10 mins ago", isAvailable = true),
        ConnectionHost("office_laptop", "Office-MacBook", "192.168.1.201", "Yesterday", isAvailable = false)
    )

    private val discoveredHosts = listOf(
        ConnectionHost("studio_pc", "Studio-PC", "192.168.1.142", "Available", isAvailable = true),
        ConnectionHost("guest_desktop", "Guest-Desktop", "192.168.1.105", "Available", isAvailable = true),
        ConnectionHost("home_media_server", "Home-Media-Server", "192.168.1.18", "Available", isAvailable = true)
    )

    private val logs = mutableListOf<ConnectionLog>()
    private var activeSocket: Socket? = null

    private val _state = MutableStateFlow(
        ConnectionSnapshot(
            status = OniStatus.OFFLINE,
            hostIp = "—",
            transportType = "—",
            activeHostName = null,
            pairedHosts = pairedHosts,
            discoveredHosts = discoveredHosts,
            connectionLogs = emptyList()
        )
    )
    val state: StateFlow<ConnectionSnapshot> = _state.asStateFlow()

    init {
        addLog("Ready to connect to companion", "INFO")
        publish()
    }

    suspend fun disconnect() {
        withContext(Dispatchers.IO) {
            activeSocket?.close()
            activeSocket = null
        }
        addLog("Disconnected from host", "INFO")
        publish(status = OniStatus.OFFLINE, activeHostName = null, hostIp = "—", transportType = "—")
    }

    suspend fun connectToHost(hostId: String) {
        val host = discoveredHosts.firstOrNull { it.id == hostId }
            ?: pairedHosts.firstOrNull { it.id == hostId }

        if (host == null) {
            addLog("Error: Host ID not found", "ERROR")
            publish(status = OniStatus.OFFLINE, activeHostName = null, hostIp = "—", transportType = "—")
            return
        }

        addLog("Connecting to ${host.name} (${host.ipAddress}) on TCP port 8085...", "INFO")

        when (val result = connectionProbe.connect(host.ipAddress)) {
            is ConnectionProbe.ProbeResult.Success -> {
                activeSocket = result.socket
                addLog("TCP connection established with ${host.name}", "SUCCESS")
                addLog("Connection latency: ${result.latencyMs}ms", "SUCCESS")
                publish(
                    status = OniStatus.SUCCESS,
                    activeHostName = host.name,
                    hostIp = host.ipAddress,
                    transportType = "TCP"
                )
            }

            is ConnectionProbe.ProbeResult.Failure -> {
                activeSocket = null
                addLog("Connection failed: ${result.reason}", "ERROR")
                publish(status = OniStatus.OFFLINE, activeHostName = null, hostIp = "—", transportType = "—")
            }
        }
    }

    suspend fun scanNetwork() {
        addLog("Network discovery is not implemented yet", "INFO")
        publish()
    }

    fun clearLogs() {
        logs.clear()
        addLog("Logs cleared", "INFO")
        publish()
    }

    private fun addLog(message: String, level: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        logs.add(0, ConnectionLog(timestamp, message, level))
    }

    private fun publish(
        status: OniStatus = _state.value.status,
        activeHostName: String? = _state.value.activeHostName,
        hostIp: String = _state.value.hostIp,
        transportType: String = _state.value.transportType
    ) {
        _state.value = ConnectionSnapshot(
            status = status,
            hostIp = hostIp,
            transportType = transportType,
            activeHostName = activeHostName,
            pairedHosts = pairedHosts,
            discoveredHosts = discoveredHosts,
            connectionLogs = logs.toList()
        )
    }
}
