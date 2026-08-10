package com.onimeno.onicanvas.feature.connection.data

import kotlinx.serialization.json.JsonObject
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
        send("shortcut", buildJsonObject { put("keys", kotlinx.serialization.json.JsonArray(keys.map(::kotlinx.serialization.json.JsonPrimitive))) })
}
