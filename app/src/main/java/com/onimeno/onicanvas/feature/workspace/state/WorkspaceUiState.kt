package com.onimeno.onicanvas.feature.workspace.state

import com.onimeno.onicanvas.feature.controls.state.CreativeControlsConfig

enum class ControlModule(val displayName: String, val description: String) {
    MACRO_PAD("Macro Pad", "Custom macro action keys and modifier overrides"),
    GESTURE_PAD("Gesture Pad", "Custom gestural bindings and trackpad behavior"),
    RADIAL_MENU("Radial Menu", "Quick-access overlay radial menu for brush control"),
    SHORTCUT_GRID("Shortcut Grid", "Grid of keyboard shortcuts and utility triggers"),
    BRUSH_CONTROLS("Brush Controls", "Dedicated sliders for brush size, opacity, and flow")
}

data class WorkspaceItem(
    val id: String,
    val name: String,
    val description: String,
    val targetApp: String,
    val buttonCount: Int,
    val iconName: String,
    val isFavorite: Boolean = false,
    val lastUsed: String,
    val enabledModules: List<ControlModule> = ControlModule.values().toList(),
    val gridSize: Int = 3,
    val macroPages: List<MacroPage> = emptyList(),
    val creativeControlsConfig: CreativeControlsConfig = CreativeControlsConfig()
)

sealed interface WorkspaceUiState {
    object Loading : WorkspaceUiState
    data class Success(
        val workspaces: List<WorkspaceItem>,
        val searchQuery: String = "",
        val showFavoritesOnly: Boolean = false
    ) : WorkspaceUiState
    data class Error(val message: String) : WorkspaceUiState
}
