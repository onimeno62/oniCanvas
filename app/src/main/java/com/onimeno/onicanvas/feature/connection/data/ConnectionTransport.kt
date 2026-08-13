package com.onimeno.onicanvas.feature.connection.data

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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

/** TCP transport for newline-delimited oniCanvas JSON frames. */
class ConnectionTransport {
    private val scope = CoroutineScope(kotlinx.coroutines.SupervisorJob() + Dispatchers.IO)
    private val writeLock = Any()
    private var socket: Socket? = null
    private var writer: BufferedWriter? = null
    private var readerJob: Job? = null

    private val _incomingMessages = MutableSharedFlow<OniCanvasMessage>(extraBufferCapacity = 64)
    val incomingMessages: SharedFlow<OniCanvasMessage> = _incomingMessages.asSharedFlow()

    private val _disconnected = MutableSharedFlow<Unit>(extraBufferCapacity = 4)
    val disconnected: SharedFlow<Unit> = _disconnected.asSharedFlow()

    fun attach(newSocket: Socket) {
        closeSocketOnly(emitDisconnected = false)
        socket = newSocket
        writer = BufferedWriter(OutputStreamWriter(newSocket.getOutputStream(), Charsets.UTF_8))
        val reader = BufferedReader(InputStreamReader(newSocket.getInputStream(), Charsets.UTF_8))

        readerJob = scope.launch {
            var peerClosed = false
            try {
                while (!newSocket.isClosed) {
                    val line = reader.readLine()
                    if (line == null) {
                        peerClosed = true
                        break
                    }
                    OniCanvasProtocol.decode(line)?.let { _incomingMessages.emit(it) }
                }
            } catch (_: CancellationException) {
                // Local close/cancellation is intentional.
            } catch (_: Exception) {
                peerClosed = true
            } finally {
                runCatching { reader.close() }
                if (peerClosed) _disconnected.emit(Unit)
            }
        }
    }

    suspend fun send(message: OniCanvasMessage): Boolean {
        return sendEncoded(OniCanvasProtocol.encode(message))
    }

    suspend fun sendFrame(frame: String): Boolean {
        return sendEncoded(frame)
    }

    fun isConnected(): Boolean = socket?.let { it.isConnected && !it.isClosed } == true

    fun close() = closeSocketOnly(emitDisconnected = false)

    fun shutdown() {
        closeSocketOnly(emitDisconnected = false)
        scope.cancel()
    }

    private suspend fun sendEncoded(frame: String): Boolean {
        val currentWriter: BufferedWriter
        val currentSocket: Socket
        synchronized(writeLock) {
            currentWriter = writer ?: return false
            currentSocket = socket ?: return false
            if (currentSocket.isClosed || !currentSocket.isConnected) return false
        }

        return synchronized(writeLock) {
            if (writer !== currentWriter || socket !== currentSocket || currentSocket.isClosed) {
                false
            } else {
                runCatching {
                    currentWriter.write(frame)
                    currentWriter.newLine()
                    currentWriter.flush()
                }.isSuccess
            }
        }
    }

    private fun closeSocketOnly(emitDisconnected: Boolean) {
        synchronized(writeLock) {
            readerJob?.cancel()
            readerJob = null
            runCatching { writer?.close() }
            writer = null
            runCatching { socket?.close() }
            socket = null
        }
        if (emitDisconnected) _disconnected.tryEmit(Unit)
    }
}
