package com.onimeno.onicanvas.feature.controls.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onimeno.onicanvas.feature.controls.state.ControlModule
import com.onimeno.onicanvas.feature.controls.state.ControlsUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ControlsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ControlsUiState>(ControlsUiState.Loading)
    val uiState: StateFlow<ControlsUiState> = _uiState.asStateFlow()

    private val allModules = mutableListOf(
        ControlModule(
            id = "macro_pad",
            title = "Macro Pad",
            description = "Customized layout for common paint macros (New Layer, Merge, Clear)",
            iconName = "apps",
            activeState = "Standard Mode",
            buttonCount = 12,
            lastSync = "Synced"
        ),
        ControlModule(
            id = "gesture_pad",
            title = "Gesture Pad",
            description = "Multi-touch canvas manipulation (Pinch to Zoom, Two-finger Rotate)",
            iconName = "gesture",
            activeState = "Precision Lock ON",
            buttonCount = 4,
            lastSync = "Synced"
        ),
        ControlModule(
            id = "radial_menu",
            title = "Radial Menu",
            description = "Thumb-friendly circular context wheel for fast tool switching",
            iconName = "track_changes",
            activeState = "8-sector Layout",
            buttonCount = 8,
            lastSync = "Modified"
        ),
        ControlModule(
            id = "shortcut_grid",
            title = "Shortcut Grid",
            description = "Traditional 4x4 keypad with visual icon labels for modifiers",
            iconName = "grid_view",
            activeState = "16 Buttons Map",
            buttonCount = 16,
            lastSync = "Synced"
        ),
        ControlModule(
            id = "brush_controls",
            title = "Brush Controls",
            description = "Horizontal precise slider controls for sizing, opacity, and hardness",
            iconName = "tune",
            activeState = "Advanced Sliders",
            buttonCount = 3,
            lastSync = "Synced"
        )
    )

    init {
        loadControls()
    }

    fun loadControls() {
        viewModelScope.launch {
            _uiState.value = ControlsUiState.Loading
            delay(300)
            _uiState.value = ControlsUiState.Success(
                modules = allModules.toList(),
                currentProfileName = "Clip Studio Paint (Default)"
            )
        }
    }

    fun toggleModuleState(id: String, newState: String) {
        val index = allModules.indexOfFirst { it.id == id }
        if (index != -1) {
            allModules[index] = allModules[index].copy(
                activeState = newState,
                lastSync = "Modified just now"
            )
            _uiState.value = ControlsUiState.Success(
                modules = allModules.toList(),
                currentProfileName = "Clip Studio Paint (Default)"
            )
        }
    }
}
