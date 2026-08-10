package com.onimeno.onicanvas.feature.connection.data

/**
 * First version of the oniCanvas companion message format.
 *
 * Messages are intentionally small and text based so the protocol can evolve
 * without changing the TCP transport layer.
 */
sealed interface OniCanvasMessage {
    data class Command(
        val name: String,
        val value: String? = null
    ) : OniCanvasMessage

    data class Event(
        val name: String,
        val value: String? = null
    ) : OniCanvasMessage
}

object OniCanvasProtocol {
    fun encode(message: OniCanvasMessage): String = when (message) {
        is OniCanvasMessage.Command -> encodeFrame("CMD", message.name, message.value)
        is OniCanvasMessage.Event -> encodeFrame("EVT", message.name, message.value)
    }

    fun decode(frame: String): OniCanvasMessage? {
        val parts = frame.split('|', limit = 3)
        if (parts.size < 2) return null

        val type = parts[0]
        val name = parts[1]
        if (name.isBlank()) return null

        val value = parts.getOrNull(2)?.takeIf { it.isNotEmpty() }
        return when (type) {
            "CMD" -> OniCanvasMessage.Command(name, value)
            "EVT" -> OniCanvasMessage.Event(name, value)
            else -> null
        }
    }

    private fun encodeFrame(type: String, name: String, value: String?): String {
        require(name.isNotBlank()) { "Message name must not be blank" }
        require(!name.contains('|')) { "Message name must not contain '|'" }
        require(value?.contains('|') != true) { "Message value must not contain '|'" }

        return buildString {
            append(type)
            append('|')
            append(name)
            if (value != null) {
                append('|')
                append(value)
            }
        }
    }
}
