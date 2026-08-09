package com.onimeno.onicanvas.feature.connection.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.math.max

/** Opens a real TCP connection to the companion endpoint. */
class ConnectionProbe(
    private val port: Int = DEFAULT_PORT,
    private val timeoutMs: Int = DEFAULT_TIMEOUT_MS
) {
    suspend fun connect(host: String): ProbeResult = withContext(Dispatchers.IO) {
        val startedAt = System.nanoTime()
        val socket = Socket()
        try {
            socket.connect(InetSocketAddress(host, port), timeoutMs)
            val elapsedMs = max(1L, (System.nanoTime() - startedAt) / 1_000_000L).toInt()
            ProbeResult.Success(socket, elapsedMs)
        } catch (exception: Exception) {
            socket.close()
            ProbeResult.Failure(exception.message ?: "Unable to reach companion")
        }
    }

    sealed interface ProbeResult {
        data class Success(val socket: Socket, val latencyMs: Int) : ProbeResult
        data class Failure(val reason: String) : ProbeResult
    }

    private companion object {
        const val DEFAULT_PORT = 8085
        const val DEFAULT_TIMEOUT_MS = 1_500
    }
}
