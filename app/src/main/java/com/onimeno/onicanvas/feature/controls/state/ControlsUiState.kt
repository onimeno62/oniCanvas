package com.onimeno.onicanvas.feature.controls.state

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
        val modules: List<ControlModule>,
        val currentProfileName: String
    ) : ControlsUiState
    data class Error(val message: String) : ControlsUiState
}
