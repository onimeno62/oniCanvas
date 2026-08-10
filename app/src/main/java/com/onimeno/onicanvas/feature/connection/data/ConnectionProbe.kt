package com.onimeno.onicanvas.feature.connection.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.math.max

/** Opens a real TCP connection to a companion endpoint and measures connect latency. */
class ConnectionProbe(
    private val defaultPort: Int = DEFAULT_PORT,
    private val timeoutMs: Int = DEFAULT_TIMEOUT_MS
) {
    suspend fun connect(host: String, port: Int = defaultPort): ProbeResult = withContext(Dispatchers.IO) {
        val startedAt = System.nanoTime()
        val socket = Socket()
        try {
            socket.tcpNoDelay = true
            socket.keepAlive = true
            socket.connect(InetSocketAddress(host, port), timeoutMs)
            val elapsedMs = max(1L, (System.nanoTime() - startedAt) / 1_000_000L).toInt()
            ProbeResult.Success(socket, elapsedMs)
        } catch (exception: Exception) {
            runCatching { socket.close() }
            ProbeResult.Failure(exception.message ?: "Unable to reach companion")
        }
    }

    sealed interface ProbeResult {
        data class Success(val socket: Socket, val latencyMs: Int) : ProbeResult
        data class Failure(val reason: String) : ProbeResult
    }

    companion object {
        const val DEFAULT_PORT = 8085
        const val DEFAULT_TIMEOUT_MS = 1_500
    }
}
