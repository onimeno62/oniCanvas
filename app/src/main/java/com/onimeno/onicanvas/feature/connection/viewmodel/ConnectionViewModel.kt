package com.onimeno.onicanvas.feature.connection.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onimeno.onicanvas.feature.connection.data.ConnectionRepository
import com.onimeno.onicanvas.feature.connection.state.ConnectionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ConnectionViewModel(
    private val repository: ConnectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ConnectionUiState>(ConnectionUiState.Loading)
    val uiState: StateFlow<ConnectionUiState> = _uiState.asStateFlow()

    init {
        observeRepository()
    }

    private fun observeRepository() {
        viewModelScope.launch {
            repository.state
                .catch { exception ->
                    _uiState.value = ConnectionUiState.Error(exception.message ?: "Connection state unavailable")
                }
                .collect { snapshot ->
                    _uiState.value = ConnectionUiState.Success(
                        status = snapshot.status,
                        hostIp = snapshot.hostIp,
                        transportType = snapshot.transportType,
                        activeHostName = snapshot.activeHostName,
                        pairedHosts = snapshot.pairedHosts,
                        discoveredHosts = snapshot.discoveredHosts,
                        connectionLogs = snapshot.connectionLogs
                    )
                }
        }
    }

    fun disconnect() {
        runConnectionOperation { repository.disconnect() }
    }

    fun connectToHost(hostId: String) {
        runConnectionOperation { repository.connectToHost(hostId) }
    }

    fun scanNetwork() {
        runConnectionOperation { repository.scanNetwork() }
    }

    fun clearLogs() {
        repository.clearLogs()
    }

    private fun runConnectionOperation(operation: suspend () -> Unit) {
        viewModelScope.launch {
            _uiState.value = ConnectionUiState.Loading
            operation()
        }
    }
}
