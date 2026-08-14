package com.onimeno.onicanvas.feature.controls.state

import kotlinx.serialization.Serializable

@Serializable
enum class GestureType(val displayName: String) {
    ONE_FINGER_PAN("One-Finger Pan"),
    TWO_FINGER_PAN("Two-Finger Pan"),
    PINCH_ZOOM("Pinch Zoom"),
    ROTATE_CANVAS("Canvas Rotation"),
    TWO_FINGER_TAP_UNDO("Two-Finger Tap (Undo)"),
    THREE_FINGER_TAP_REDO("Three-Finger Tap (Redo)")
}

@Serializable
enum class GestureAction(val displayName: String) {
    PAN("Pan Canvas"),
    ZOOM("Zoom Canvas"),
    ROTATE("Rotate Canvas"),
    UNDO("Undo Action"),
    REDO("Redo Action"),
    NONE("Disabled")
}

@Serializable
data class GestureBinding(
    val gestureType: GestureType,
    val enabled: Boolean = true,
    val action: GestureAction,
    val sensitivity: Float = 1.0f,
    val isInverted: Boolean = false
)

@Serializable
data class CreativeControlsConfig(
    val gestureBindings: List<GestureBinding> = defaultGestureBindings(),
    val zoomSensitivity: Float = 1.0f,
    val panSensitivity: Float = 1.0f,
    val rotationSensitivity: Float = 1.0f,
    val invertPanX: Boolean = false,
    val invertPanY: Boolean = false,
    val invertZoom: Boolean = false,
    val invertRotation: Boolean = false,
    val hapticsEnabled: Boolean = true
) {
    fun normalized(): CreativeControlsConfig {
        val existing = gestureBindings.associateBy { it.gestureType }
        val defaults = defaultGestureBindings().associateBy { it.gestureType }
        val normalizedBindings = GestureType.entries.map { type ->
            existing[type] ?: defaults[type] ?: GestureBinding(type, action = GestureAction.NONE)
        }
        return copy(gestureBindings = normalizedBindings)
    }
}

fun defaultGestureBindings(): List<GestureBinding> = listOf(
    GestureBinding(GestureType.ONE_FINGER_PAN, enabled = true, action = GestureAction.PAN, sensitivity = 1.0f),
    GestureBinding(GestureType.TWO_FINGER_PAN, enabled = true, action = GestureAction.PAN, sensitivity = 1.0f),
    GestureBinding(GestureType.PINCH_ZOOM, enabled = true, action = GestureAction.ZOOM, sensitivity = 1.0f),
    GestureBinding(GestureType.ROTATE_CANVAS, enabled = true, action = GestureAction.ROTATE, sensitivity = 1.0f),
    GestureBinding(GestureType.TWO_FINGER_TAP_UNDO, enabled = true, action = GestureAction.UNDO, sensitivity = 1.0f),
    GestureBinding(GestureType.THREE_FINGER_TAP_REDO, enabled = true, action = GestureAction.REDO, sensitivity = 1.0f)
)
