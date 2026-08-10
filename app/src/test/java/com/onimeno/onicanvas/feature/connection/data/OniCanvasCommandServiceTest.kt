package com.onimeno.onicanvas.feature.connection.data

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OniCanvasCommandServiceTest {
    @Test
    fun commandFramesUseTheOniCanvasProtocol() {
        assertEquals("CMD|undo", OniCanvasProtocol.encode(OniCanvasMessage.Command("undo")))
        assertEquals("CMD|zoom|1.25", OniCanvasProtocol.encode(OniCanvasMessage.Command("zoom", "1.25")))
    }

    @Test
    fun commandServiceSendsEncodedCommand() = runTest {
        var lastFrame: String? = null
        val service = OniCanvasCommandService { frame ->
            lastFrame = frame
            true
        }

        assertTrue(service.undo())
        assertEquals("CMD|undo", lastFrame)
    }
}
