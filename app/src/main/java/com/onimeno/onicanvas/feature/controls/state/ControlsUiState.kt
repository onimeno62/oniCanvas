package com.onimeno.onicanvas.feature.controls.state

import com.onimeno.onicanvas.feature.workspace.state.WorkspaceItem

data class ControlModule(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val activeState: String,
    val buttonCount: Int = 0,
    val lastSync: String
)

sealed interface ControlsUiState {
    object Loading : ControlsUiState
    data class Success(
        val activeWorkspace: WorkspaceItem,
        val availableWorkspaces: List<WorkspaceItem>,
        val activePageId: String,
        val isConnected: Boolean,
        val connectionType: String = "—",
        val activeHostName: String? = null
    ) : ControlsUiState
    data class Error(val message: String) : ControlsUiState
}
