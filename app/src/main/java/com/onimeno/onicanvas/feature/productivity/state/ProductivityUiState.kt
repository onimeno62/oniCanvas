package com.onimeno.onicanvas.feature.productivity.state

import com.onimeno.onicanvas.core.designsystem.components.OniStatus

/** Immutable UI state for the productivity control surface. */
data class ProductivityUiState(
    val isConnected: Boolean = false,
    val connectionStatus: OniStatus = OniStatus.OFFLINE,
    val activeTool: ProductivityTool = ProductivityTool.RADIAL_MENU
)

enum class ProductivityTool {
    RADIAL_MENU,
    TOUCHPAD,
    LAYERS,
    BRUSHES
}

enum class ProductivityAction(val wireAction: String) {
    NEW_LAYER("layer_new"),
    DUPLICATE_LAYER("layer_duplicate"),
    MERGE_LAYER("layer_merge"),
    LOCK_LAYER("layer_lock"),
    TOGGLE_MASK("layer_mask"),
    OPACITY_DOWN("layer_opacity_down"),
    OPACITY_UP("layer_opacity_up"),
    BRUSH("brush"),
    ERASER("eraser"),
    FILL("fill"),
    SELECT("selection"),
    TRANSFORM("transform"),
    COPY("copy"),
    PASTE("paste"),
    SAVE("save"),
    UNDO("undo"),
    REDO("redo")
}
