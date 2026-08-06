package com.onimeno.onicanvas.feature.connection.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onimeno.onicanvas.core.designsystem.components.OniStatus
import com.onimeno.onicanvas.feature.connection.state.ConnectionHost
import com.onimeno.onicanvas.feature.connection.state.ConnectionLog
import com.onimeno.onicanvas.feature.connection.state.ConnectionUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConnectionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ConnectionUiState>(ConnectionUiState.Loading)
    val uiState: StateFlow<ConnectionUiState> = _uiState.asStateFlow()

    private val pairedHostsList = mutableListOf(
        ConnectionHost("studio_pc", "Studio-PC", "192.168.1.142", "10 mins ago", isAvailable = true),
        ConnectionHost("office_laptop", "Office-MacBook", "192.168.1.201", "Yesterday", isAvailable = false)
    )

    private val discoveredHostsList = mutableListOf(
        ConnectionHost("studio_pc", "Studio-PC", "192.168.1.142", "Available", isAvailable = true),
        ConnectionHost("guest_desktop", "Guest-Desktop", "192.168.1.105", "Available", isAvailable = true),
        ConnectionHost("home_media_server", "Home-Media-Server", "192.168.1.18", "Available", isAvailable = true)
    )

    private val logsList = mutableListOf<ConnectionLog>()

    init {
        initDefaultLogs()
        refreshConnectionState(OniStatus.SUCCESS, "Studio-PC", "192.168.1.142", "Wi-Fi (5 GHz)")
    }

    private fun initDefaultLogs() {
        addLogItem("Searching local network interfaces...", "INFO")
        addLogItem("Found gateway 192.168.1.1. Resolving hosts...", "INFO")
        addLogItem("Connection handshaking initiated with Studio-PC...", "INFO")
        addLogItem("WebSocket handshaking successful on port 8085", "SUCCESS")
        addLogItem("Synchronized layout config [Illustration Master]", "SUCCESS")
        addLogItem("Established low-latency TCP channel (Ping: 8ms)", "SUCCESS")
    }

    private fun addLogItem(message: String, level: String) {
        val sdf = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
        val timestamp = sdf.format(Date())
        logsList.add(0, ConnectionLog(timestamp, message, level))
    }

    private fun refreshConnectionState(
        status: OniStatus,
        activeName: String?,
        ip: String,
        transport: String
    ) {
        _uiState.value = ConnectionUiState.Success(
            status = status,
            hostIp = ip,
            transportType = transport,
            activeHostName = activeName,
            pairedHosts = pairedHostsList.toList(),
            discoveredHosts = discoveredHostsList.toList(),
            connectionLogs = logsList.toList()
        )
    }

    fun disconnect() {
        viewModelScope.launch {
            _uiState.value = ConnectionUiState.Loading
            delay(200)
            addLogItem("Disconnect signal sent by client", "WARNING")
            addLogItem("TCP stream closed successfully", "INFO")
            addLogItem("Disconnected from host", "ERROR")
            refreshConnectionState(OniStatus.OFFLINE, null, "—", "—")
        }
    }

    fun connectToHost(hostId: String) {
        viewModelScope.launch {
            _uiState.value = ConnectionUiState.Loading
            delay(500)
            val host = discoveredHostsList.firstOrNull { it.id == hostId } ?: pairedHostsList.firstOrNull { it.id == hostId }
            if (host != null) {
                addLogItem("Initiating connection to ${host.name} (${host.ipAddress})...", "INFO")
                addLogItem("Shaking hands via companion websocket...", "INFO")
                addLogItem("Connected to host machine: ${host.name}", "SUCCESS")
                addLogItem("Synchronized macros & configurations", "SUCCESS")
                refreshConnectionState(OniStatus.SUCCESS, host.name, host.ipAddress, "Wi-Fi (5 GHz)")
            } else {
                addLogItem("Error: Host ID not found", "ERROR")
                refreshConnectionState(OniStatus.OFFLINE, null, "—", "—")
            }
        }
    }

    fun scanNetwork() {
        viewModelScope.launch {
            addLogItem("Scanning local network subnets...", "INFO")
            delay(300)
            addLogItem("Discovered 3 available nodes", "INFO")
            refreshConnectionState(
                status = getCurrentStatus(),
                activeName = getActiveName(),
                ip = getActiveIp(),
                transport = getActiveTransport()
            )
        }
    }

    fun clearLogs() {
        logsList.clear()
        addLogItem("Logs cleared", "INFO")
        refreshConnectionState(
            status = getCurrentStatus(),
            activeName = getActiveName(),
            ip = getActiveIp(),
            transport = getActiveTransport()
        )
    }

    private fun getCurrentStatus(): OniStatus {
        return (uiState.value as? ConnectionUiState.Success)?.status ?: OniStatus.OFFLINE
    }

    private fun getActiveName(): String? {
        return (uiState.value as? ConnectionUiState.Success)?.activeHostName
    }

    private fun getActiveIp(): String {
        return (uiState.value as? ConnectionUiState.Success)?.hostIp ?: "—"
    }

    private fun getActiveTransport(): String {
        return (uiState.value as? ConnectionUiState.Success)?.transportType ?: "—"
    }
}
