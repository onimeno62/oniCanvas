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
        val repository = RecordingConnectionRepository()
        val service = OniCanvasCommandService(repository)

        assertTrue(service.undo())
        assertEquals("CMD|undo", repository.lastMessage)
    }

    private class RecordingConnectionRepository : ConnectionRepository() {
        var lastMessage: String? = null

        override suspend fun sendMessage(message: String): Boolean {
            lastMessage = message
            return true
        }
    }
}
