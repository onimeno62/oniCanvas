package com.onimeno.onicanvas.feature.connection.data

import java.util.UUID
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.json.put

/** Versioned, newline-delimited JSON protocol shared with the Windows companion. */
data class OniCanvasMessage(
    val version: Int = CURRENT_VERSION,
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val type: Type,
    val payload: JsonObject = buildJsonObject {}
) {
    enum class Type {
        COMMAND,
        RESPONSE,
        EVENT,
        HEARTBEAT,
        HEARTBEAT_ACK,
        DISCOVERY_REQUEST,
        DISCOVERY_RESPONSE,
        ERROR
    }

    companion object {
        fun command(action: String, payload: JsonObject = buildJsonObject {}): OniCanvasMessage =
            OniCanvasMessage(type = Type.COMMAND, payload = payload.withAction(action))

        fun event(name: String, payload: JsonObject = buildJsonObject {}): OniCanvasMessage =
            OniCanvasMessage(type = Type.EVENT, payload = payload.withName(name))

        fun heartbeat(): OniCanvasMessage = OniCanvasMessage(type = Type.HEARTBEAT)

        fun heartbeatAck(id: String): OniCanvasMessage = OniCanvasMessage(
            id = id,
            type = Type.HEARTBEAT_ACK
        )

        fun discoveryRequest(): OniCanvasMessage = OniCanvasMessage(type = Type.DISCOVERY_REQUEST)
    }
}

object OniCanvasProtocol {
    private val json = Json { ignoreUnknownKeys = true }

    fun encode(message: OniCanvasMessage): String = buildJsonObject {
        put("version", message.version)
        put("id", message.id)
        put("timestamp", message.timestamp)
        put("type", message.type.name.lowercase())
        put("payload", message.payload)
    }.toString()

    fun decode(frame: String): OniCanvasMessage? {
        val trimmed = frame.trim()
        if (trimmed.isEmpty()) return null

        // Accept the short Phase 2 frame format while peers migrate to JSON.
        if (!trimmed.startsWith("{")) return decodeLegacyFrame(trimmed)

        return runCatching {
            val root = json.parseToJsonElement(trimmed).jsonObject
            val version = root["version"]?.jsonPrimitive?.content?.toIntOrNull() ?: return null
            val id = root["id"]?.jsonPrimitive?.content ?: return null
            val timestamp = root["timestamp"]?.jsonPrimitive?.long ?: return null
            val typeValue = root["type"]?.jsonPrimitive?.content ?: return null
            val type = OniCanvasMessage.Type.entries.firstOrNull {
                it.name.equals(typeValue, ignoreCase = true)
            } ?: return null
            val payload = root["payload"] as? JsonObject ?: buildJsonObject {}
            OniCanvasMessage(version, id, timestamp, type, payload)
        }.getOrNull()
    }

    private fun decodeLegacyFrame(frame: String): OniCanvasMessage? {
        val parts = frame.split('|', limit = 3)
        if (parts.size < 2 || parts[1].isBlank()) return null
        val value = parts.getOrNull(2)?.takeIf(String::isNotEmpty)
        return when (parts[0]) {
            "CMD" -> OniCanvasMessage.command(parts[1], value?.let { buildJsonObject { put("value", it) } } ?: buildJsonObject {})
            "EVT" -> OniCanvasMessage.event(parts[1], value?.let { buildJsonObject { put("value", it) } } ?: buildJsonObject {})
            else -> null
        }
    }

    private fun JsonObject.withAction(action: String): JsonObject = buildJsonObject {
        put("action", action)
        for ((key, value) in this@withAction) put(key, value)
    }

    private fun JsonObject.withName(name: String): JsonObject = buildJsonObject {
        put("event", name)
        for ((key, value) in this@withName) put(key, value)
    }

    private const val CURRENT_VERSION = 1
}
