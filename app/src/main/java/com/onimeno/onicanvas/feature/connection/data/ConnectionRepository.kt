package com.onimeno.onicanvas.feature.connection.data

import com.onimeno.onicanvas.core.designsystem.components.OniStatus
import com.onimeno.onicanvas.feature.connection.state.ConnectionHost
import com.onimeno.onicanvas.feature.connection.state.ConnectionLog
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

class ConnectionRepository {
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

    private val _state = MutableStateFlow(
        ConnectionSnapshot(
            status = OniStatus.SUCCESS,
            hostIp = "192.168.1.142",
            transportType = "Wi-Fi (5 GHz)",
            activeHostName = "Studio-PC",
            pairedHosts = pairedHosts,
            discoveredHosts = discoveredHosts,
            connectionLogs = emptyList()
        )
    )
    val state: StateFlow<ConnectionSnapshot> = _state.asStateFlow()

    init {
        addLog("Searching local network interfaces...", "INFO")
        addLog("Found gateway 192.168.1.1. Resolving hosts...", "INFO")
        addLog("Connection handshaking initiated with Studio-PC...", "INFO")
        addLog("WebSocket handshaking successful on port 8085", "SUCCESS")
        addLog("Synchronized layout config [Illustration Master]", "SUCCESS")
        addLog("Established low-latency TCP channel (Ping: 8ms)", "SUCCESS")
        publish()
    }

    suspend fun disconnect() {
        delay(200)
        addLog("Disconnect signal sent by client", "WARNING")
        addLog("TCP stream closed successfully", "INFO")
        addLog("Disconnected from host", "ERROR")
        publish(status = OniStatus.OFFLINE, activeHostName = null, hostIp = "—", transportType = "—")
    }

    suspend fun connectToHost(hostId: String) {
        delay(500)
        val host = discoveredHosts.firstOrNull { it.id == hostId }
            ?: pairedHosts.firstOrNull { it.id == hostId }
        if (host != null) {
            addLog("Initiating connection to ${host.name} (${host.ipAddress})...", "INFO")
            addLog("Shaking hands via companion websocket...", "INFO")
            addLog("Connected to host machine: ${host.name}", "SUCCESS")
            addLog("Synchronized macros & configurations", "SUCCESS")
            publish(
                status = OniStatus.SUCCESS,
                activeHostName = host.name,
                hostIp = host.ipAddress,
                transportType = "Wi-Fi (5 GHz)"
            )
        } else {
            addLog("Error: Host ID not found", "ERROR")
            publish(status = OniStatus.OFFLINE, activeHostName = null, hostIp = "—", transportType = "—")
        }
    }

    suspend fun scanNetwork() {
        addLog("Scanning local network subnets...", "INFO")
        delay(300)
        addLog("Discovered 3 available nodes", "INFO")
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
