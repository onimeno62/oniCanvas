package com.onimeno.onicanvas.feature.color.data

import com.onimeno.onicanvas.feature.connection.data.OniCanvasCommandService
import com.onimeno.onicanvas.feature.connection.data.OniCanvasMessage
import com.onimeno.onicanvas.feature.connection.data.OniCanvasProtocol
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorCommandsProtocolTest {

    @Test
    fun `setColorHex command produces expected frame`() = runBlocking {
        var sentMessage: OniCanvasMessage? = null
        val service = OniCanvasCommandService { msg ->
            sentMessage = msg
            true
        }

        val success = service.setColorHex("#FF5500")
        assertTrue(success)
        assertNotNull(sentMessage)

        val encoded = OniCanvasProtocol.encode(sentMessage!!)
        val decoded = OniCanvasProtocol.decode(encoded)
        assertNotNull(decoded)
        assertEquals(OniCanvasMessage.Type.COMMAND, decoded!!.type)
        assertEquals("color_hex", decoded.payload["action"]?.jsonPrimitive?.content)
        assertEquals("#FF5500", decoded.payload["hex"]?.jsonPrimitive?.content)
    }

    @Test
    fun `setColorRgb command produces expected frame`() = runBlocking {
        var sentMessage: OniCanvasMessage? = null
        val service = OniCanvasCommandService { msg ->
            sentMessage = msg
            true
        }

        val success = service.setColorRgb(255, 128, 64)
        assertTrue(success)
        assertNotNull(sentMessage)

        val encoded = OniCanvasProtocol.encode(sentMessage!!)
        val decoded = OniCanvasProtocol.decode(encoded)
        assertNotNull(decoded)
        assertEquals(OniCanvasMessage.Type.COMMAND, decoded!!.type)
        assertEquals("color_rgb", decoded.payload["action"]?.jsonPrimitive?.content)
        assertEquals("255", decoded.payload["r"]?.jsonPrimitive?.content)
        assertEquals("128", decoded.payload["g"]?.jsonPrimitive?.content)
        assertEquals("64", decoded.payload["b"]?.jsonPrimitive?.content)
    }

    @Test
    fun `setColorHsv command produces expected frame`() = runBlocking {
        var sentMessage: OniCanvasMessage? = null
        val service = OniCanvasCommandService { msg ->
            sentMessage = msg
            true
        }

        val success = service.setColorHsv(240.5f, 80.0f, 95.0f)
        assertTrue(success)
        assertNotNull(sentMessage)

        val encoded = OniCanvasProtocol.encode(sentMessage!!)
        val decoded = OniCanvasProtocol.decode(encoded)
        assertNotNull(decoded)
        assertEquals(OniCanvasMessage.Type.COMMAND, decoded!!.type)
        assertEquals("color_hsv", decoded.payload["action"]?.jsonPrimitive?.content)
    }

    @Test
    fun `sendColorPalette command produces expected frame`() = runBlocking {
        var sentMessage: OniCanvasMessage? = null
        val service = OniCanvasCommandService { msg ->
            sentMessage = msg
            true
        }

        val success = service.sendColorPalette("Concept Art", listOf("#FF0000", "#00FF00", "#0000FF"))
        assertTrue(success)
        assertNotNull(sentMessage)

        val encoded = OniCanvasProtocol.encode(sentMessage!!)
        val decoded = OniCanvasProtocol.decode(encoded)
        assertNotNull(decoded)
        assertEquals(OniCanvasMessage.Type.COMMAND, decoded!!.type)
        assertEquals("color_palette", decoded.payload["action"]?.jsonPrimitive?.content)
        assertEquals("Concept Art", decoded.payload["name"]?.jsonPrimitive?.content)
        assertEquals(3, decoded.payload["colors"]?.jsonArray?.size)
    }
}
