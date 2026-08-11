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

    suspend fun zoom(amount: Double): Boolean =
        send("zoom", buildJsonObject { put("amount", amount) })

    suspend fun shortcut(keys: List<String>): Boolean =
        send("shortcut", buildJsonObject { put("keys", JsonArray(keys.map { JsonPrimitive(it) })) })

    suspend fun brush(): Boolean = send("brush")

    suspend fun eraser(): Boolean = send("eraser")

    suspend fun fill(): Boolean = send("fill")

    suspend fun selection(): Boolean = send("selection")

    suspend fun transform(): Boolean = send("transform")

    suspend fun copy(): Boolean = send("copy")

    suspend fun paste(): Boolean = send("paste")
}
