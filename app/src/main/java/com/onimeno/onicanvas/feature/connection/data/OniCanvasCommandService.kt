package com.onimeno.onicanvas.feature.connection.data

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/** Application-facing API for sending oniCanvas commands. */
class OniCanvasCommandService(
    private val sendMessage: suspend (OniCanvasMessage) -> Boolean
) {
    suspend fun send(command: String, payload: JsonObject = buildJsonObject {}): Boolean =
        sendMessage(OniCanvasMessage.command(command, payload))

    suspend fun undo(): Boolean = send("undo")
    suspend fun redo(): Boolean = send("redo")
    suspend fun save(): Boolean = send("save")
    suspend fun zoom(amount: Double): Boolean = send("zoom", buildJsonObject { put("amount", amount) })
    suspend fun shortcut(keys: List<String>): Boolean = send("shortcut", buildJsonObject { put("keys", JsonArray(keys.map { JsonPrimitive(it) })) })
    suspend fun brush(): Boolean = send("brush")
    suspend fun eraser(): Boolean = send("eraser")
    suspend fun fill(): Boolean = send("fill")
    suspend fun selection(): Boolean = send("selection")
    suspend fun transform(): Boolean = send("transform")
    suspend fun copy(): Boolean = send("copy")
    suspend fun paste(): Boolean = send("paste")
    suspend fun pan(deltaX: Double, deltaY: Double): Boolean = send("pan", buildJsonObject { put("deltaX", deltaX); put("deltaY", deltaY) })
    suspend fun rotate(angleDegrees: Double): Boolean = send("rotate", buildJsonObject { put("angle", angleDegrees) })
    suspend fun rotateLeft(): Boolean = send("rotate_left")
    suspend fun rotateRight(): Boolean = send("rotate_right")
    suspend fun resetRotation(): Boolean = send("reset_rotation")
    suspend fun resetView(): Boolean = send("reset_view")
    suspend fun fitCanvas(): Boolean = send("fit_canvas")
    suspend fun flipHorizontal(): Boolean = send("flip_horizontal")
    suspend fun flipVertical(): Boolean = send("flip_vertical")
    suspend fun zoomIn(): Boolean = send("zoom_in")
    suspend fun zoomOut(): Boolean = send("zoom_out")
    suspend fun resetZoom(): Boolean = send("reset_zoom")

    // Phase 6 productivity commands.
    suspend fun mouseMove(deltaX: Double, deltaY: Double): Boolean =
        send("mouse_move", buildJsonObject { put("x", deltaX); put("y", deltaY) })

    suspend fun mouseButton(button: String, pressed: Boolean): Boolean =
        send("mouse_button", buildJsonObject { put("button", button); put("pressed", pressed) })

    suspend fun scroll(deltaX: Double, deltaY: Double): Boolean =
        send("mouse_scroll", buildJsonObject { put("x", deltaX); put("y", deltaY) })

    suspend fun brushPreset(index: Int): Boolean =
        send("brush_preset", buildJsonObject { put("index", index) })
}
