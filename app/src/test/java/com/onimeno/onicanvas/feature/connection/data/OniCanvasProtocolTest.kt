package com.onimeno.onicanvas.feature.connection.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OniCanvasProtocolTest {
    @Test
    fun encodeCommandWithoutValue() {
        assertEquals("CMD|undo", OniCanvasProtocol.encode(OniCanvasMessage.Command("undo")))
    }

    @Test
    fun encodeCommandWithValue() {
        assertEquals("CMD|zoom|1.25", OniCanvasProtocol.encode(OniCanvasMessage.Command("zoom", "1.25")))
    }

    @Test
    fun decodeCommandWithValue() {
        assertEquals(OniCanvasMessage.Command("zoom", "1.25"), OniCanvasProtocol.decode("CMD|zoom|1.25"))
    }

    @Test
    fun decodeEventWithoutValue() {
        assertEquals(OniCanvasMessage.Event("connected"), OniCanvasProtocol.decode("EVT|connected"))
    }

    @Test
    fun decodeUnknownFrameReturnsNull() {
        assertNull(OniCanvasProtocol.decode("BAD|message"))
    }

    @Test
    fun decodeBlankNameReturnsNull() {
        assertNull(OniCanvasProtocol.decode("CMD|"))
    }

    @Test
    fun encodeRejectsBlankName() {
        try {
            OniCanvasProtocol.encode(OniCanvasMessage.Command(""))
            throw AssertionError("Expected IllegalArgumentException")
        } catch (_: IllegalArgumentException) {
            // Expected.
        }
    }
}
