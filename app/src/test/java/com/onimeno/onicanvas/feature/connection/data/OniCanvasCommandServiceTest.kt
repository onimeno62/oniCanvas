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
}
