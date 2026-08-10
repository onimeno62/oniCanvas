package com.onimeno.onicanvas.feature.connection.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

/** TCP transport for newline-delimited oniCanvas protocol frames. */
class ConnectionTransport {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var socket: Socket? = null
    private var writer: BufferedWriter? = null
    private var readerJob: Job? = null

    private val _incomingMessages = MutableSharedFlow<OniCanvasMessage>(extraBufferCapacity = 32)
    val incomingMessages: SharedFlow<OniCanvasMessage> = _incomingMessages.asSharedFlow()

    fun attach(socket: Socket) {
        closeSocketOnly()
        this.socket = socket
        writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream(), Charsets.UTF_8))

        val reader = BufferedReader(InputStreamReader(socket.getInputStream(), Charsets.UTF_8))
        readerJob = scope.launch {
            try {
                while (!socket.isClosed) {
                    val line = reader.readLine() ?: break
                    OniCanvasProtocol.decode(line)?.let { _incomingMessages.emit(it) }
                }
            } finally {
                reader.close()
            }
        }
    }

    suspend fun send(message: OniCanvasMessage): Boolean {
        val currentWriter = writer ?: return false
        if (socket?.isClosed != false) return false

        return try {
            currentWriter.write(OniCanvasProtocol.encode(message))
            currentWriter.newLine()
            currentWriter.flush()
            true
        } catch (_: Exception) {
            false
        }
    }

    fun close() = closeSocketOnly()

    fun shutdown() {
        closeSocketOnly()
        scope.cancel()
    }

    private fun closeSocketOnly() {
        readerJob?.cancel()
        readerJob = null
        writer?.close()
        writer = null
        socket?.close()
        socket = null
    }
}
