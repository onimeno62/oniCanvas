package com.onimeno.onicanvas.feature.dashboard.state

import com.onimeno.onicanvas.core.designsystem.components.OniStatus

sealed interface DashboardUiState {
    object Loading : DashboardUiState
    
    data class Success(
        val deviceName: String,
        val connectionType: String,
        val connectionStatus: OniStatus,
        val activeSoftware: String,
        val activeWorkspace: String,
        val batteryLevel: Int,
        val latencyMs: Int,
        val recentWorkspaces: List<WorkspaceSummary>,
        val quickActions: List<QuickActionItem>
    ) : DashboardUiState

    data class Error(val message: String) : DashboardUiState
}

data class WorkspaceSummary(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val buttonCount: Int
)

data class QuickActionItem(
    val title: String,
    val description: String,
    val iconName: String,
    val actionType: QuickActionType
)

enum class QuickActionType {
    CONNECT, MACRO_PAD, GESTURE_PAD, RADIAL_MENU, TOUCHPAD
}
