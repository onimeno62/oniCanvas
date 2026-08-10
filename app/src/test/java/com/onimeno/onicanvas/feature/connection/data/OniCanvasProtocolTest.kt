package com.onimeno.onicanvas.feature.connection.data

import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OniCanvasProtocolTest {
    @Test
    fun encodeProducesVersionedJsonEnvelope() {
        val frame = OniCanvasProtocol.encode(
            OniCanvasMessage.command("zoom", buildJsonObject { put("amount", 1.25) })
        )

        assertTrue(frame.contains("\"version\":1"))
        assertTrue(frame.contains("\"type\":\"command\""))
        assertTrue(frame.contains("\"action\":\"zoom\""))
        assertTrue(frame.contains("\"amount\":1.25"))
    }

    @Test
    fun decodeRoundTripsJsonMessage() {
        val original = OniCanvasMessage.command(
            "zoom",
            buildJsonObject { put("amount", 1.25) }
        )

        val decoded = OniCanvasProtocol.decode(OniCanvasProtocol.encode(original))

        assertEquals(original, decoded)
    }

    @Test
    fun decodeHeartbeatAck() {
        val message = OniCanvasMessage.heartbeatAck("heartbeat-1")
        val decoded = OniCanvasProtocol.decode(OniCanvasProtocol.encode(message))

        assertNotNull(decoded)
        assertEquals(OniCanvasMessage.Type.HEARTBEAT_ACK, decoded?.type)
        assertEquals("heartbeat-1", decoded?.id)
    }

    @Test
    fun decodeLegacyCommandForMigration() {
        val decoded = OniCanvasProtocol.decode("CMD|undo")

        assertEquals(OniCanvasMessage.Type.COMMAND, decoded?.type)
        assertEquals("undo", decoded?.payload?.get("action")?.toString()?.trim('"'))
    }

    @Test
    fun decodeRejectsMalformedJson() {
        assertNull(OniCanvasProtocol.decode("{not-json"))
        assertNull(OniCanvasProtocol.decode("BAD|message"))
        assertNull(OniCanvasProtocol.decode("CMD|"))
    }
}
