package com.onimeno.onicanvas.feature.connection.state

import com.onimeno.onicanvas.core.designsystem.components.OniStatus

data class ConnectionHost(
    val id: String,
    val name: String,
    val ipAddress: String,
    val lastConnected: String,
    val isAvailable: Boolean = false
)

data class ConnectionLog(
    val timestamp: String,
    val message: String,
    val level: String // INFO, SUCCESS, WARNING, ERROR
)

sealed interface ConnectionUiState {
    object Loading : ConnectionUiState
    data class Success(
        val status: OniStatus,
        val hostIp: String,
        val transportType: String,
        val activeHostName: String?,
        val pairedHosts: List<ConnectionHost>,
        val discoveredHosts: List<ConnectionHost>,
        val connectionLogs: List<ConnectionLog>
    ) : ConnectionUiState
    data class Error(val message: String) : ConnectionUiState
}
