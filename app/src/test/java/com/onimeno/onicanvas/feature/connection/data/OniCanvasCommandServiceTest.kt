package com.onimeno.onicanvas.feature.connection.data

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OniCanvasCommandServiceTest {
    @Test
    fun commandServiceSendsTypedCommand() = runBlocking {
        var lastMessage: OniCanvasMessage? = null
        val service = OniCanvasCommandService { message ->
            lastMessage = message
            true
        }

        assertTrue(service.undo())
        assertEquals(OniCanvasMessage.Type.COMMAND, lastMessage?.type)
        assertEquals("undo", lastMessage?.payload?.get("action")?.jsonPrimitive?.content)
    }

    @Test
    fun zoomUsesNumericAmountPayload() = runBlocking {
        var lastMessage: OniCanvasMessage? = null
        val service = OniCanvasCommandService { message ->
            lastMessage = message
            true
        }

        assertTrue(service.zoom(1.25))
        assertEquals("zoom", lastMessage?.payload?.get("action")?.jsonPrimitive?.content)
        assertEquals("1.25", lastMessage?.payload?.get("amount")?.jsonPrimitive?.content)
    }

    @Test
    fun shortcutUsesArrayPayload() = runBlocking {
        var lastMessage: OniCanvasMessage? = null
        val service = OniCanvasCommandService { message ->
            lastMessage = message
            true
        }

        assertTrue(service.shortcut(listOf("CTRL", "Z")))
        assertEquals(
            listOf("CTRL", "Z"),
            lastMessage?.payload?.get("keys")?.jsonArray?.map { it.jsonPrimitive.content }
        )
    }

    @Test
    fun panSendsDeltaXAndDeltaYPayload() = runBlocking {
        var lastMessage: OniCanvasMessage? = null
        val service = OniCanvasCommandService { message ->
            lastMessage = message
            true
        }

        assertTrue(service.pan(12.5, -5.0))
        assertEquals("pan", lastMessage?.payload?.get("action")?.jsonPrimitive?.content)
        assertEquals("12.5", lastMessage?.payload?.get("deltaX")?.jsonPrimitive?.content)
        assertEquals("-5.0", lastMessage?.payload?.get("deltaY")?.jsonPrimitive?.content)
    }

    @Test
    fun rotateSendsAnglePayload() = runBlocking {
        var lastMessage: OniCanvasMessage? = null
        val service = OniCanvasCommandService { message ->
            lastMessage = message
            true
        }

        assertTrue(service.rotate(45.0))
        assertEquals("rotate", lastMessage?.payload?.get("action")?.jsonPrimitive?.content)
        assertEquals("45.0", lastMessage?.payload?.get("angle")?.jsonPrimitive?.content)
    }

    @Test
    fun canvasActionsSendTypedCommands() = runBlocking {
        var lastMessage: OniCanvasMessage? = null
        val service = OniCanvasCommandService { message ->
            lastMessage = message
            true
        }

        assertTrue(service.fitCanvas())
        assertEquals("fit_canvas", lastMessage?.payload?.get("action")?.jsonPrimitive?.content)

        assertTrue(service.resetView())
        assertEquals("reset_view", lastMessage?.payload?.get("action")?.jsonPrimitive?.content)

        assertTrue(service.flipHorizontal())
        assertEquals("flip_horizontal", lastMessage?.payload?.get("action")?.jsonPrimitive?.content)

        assertTrue(service.flipVertical())
        assertEquals("flip_vertical", lastMessage?.payload?.get("action")?.jsonPrimitive?.content)

        assertTrue(service.rotateLeft())
        assertEquals("rotate_left", lastMessage?.payload?.get("action")?.jsonPrimitive?.content)

        assertTrue(service.rotateRight())
        assertEquals("rotate_right", lastMessage?.payload?.get("action")?.jsonPrimitive?.content)

        assertTrue(service.resetRotation())
        assertEquals("reset_rotation", lastMessage?.payload?.get("action")?.jsonPrimitive?.content)
    }
}
