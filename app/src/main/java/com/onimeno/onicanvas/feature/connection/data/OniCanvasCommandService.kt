package com.onimeno.onicanvas.feature.connection.data

/**
 * Application-facing API for sending oniCanvas commands.
 * Keeps command construction out of UI code and the raw TCP transport.
 */
class OniCanvasCommandService(
    private val sendFrame: suspend (String) -> Boolean
) {
    suspend fun send(command: String, value: String? = null): Boolean {
        return sendFrame(
            OniCanvasProtocol.encode(OniCanvasMessage.Command(command, value))
        )
    }

    suspend fun undo(): Boolean = send("undo")

    suspend fun redo(): Boolean = send("redo")

    suspend fun save(): Boolean = send("save")

    suspend fun zoom(value: String): Boolean = send("zoom", value)
}
