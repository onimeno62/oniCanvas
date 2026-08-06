package com.onimeno.onicanvas.feature.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onimeno.onicanvas.core.designsystem.components.OniStatus
import com.onimeno.onicanvas.feature.dashboard.data.DashboardRepository
import com.onimeno.onicanvas.feature.dashboard.data.FakeDashboardRepository
import com.onimeno.onicanvas.feature.dashboard.state.DashboardUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: DashboardRepository = FakeDashboardRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            // Simulate brief loading to show polished skeleton layout transition
            delay(300)
            repository.getDashboardData()
                .catch { exception ->
                    _uiState.value = DashboardUiState.Error(exception.message ?: "Failed to load dashboard data")
                }
                .collect { data ->
                    _uiState.value = DashboardUiState.Success(
                        deviceName = data.deviceName,
                        connectionType = data.connectionType,
                        connectionStatus = data.connectionStatus,
                        activeSoftware = data.activeSoftware,
                        activeWorkspace = data.activeWorkspace,
                        batteryLevel = data.batteryLevel,
                        latencyMs = data.latencyMs,
                        recentWorkspaces = data.recentWorkspaces,
                        quickActions = data.quickActions
                    )
                }
        }
    }
}

