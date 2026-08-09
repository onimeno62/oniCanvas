package com.onimeno.onicanvas.feature.connection.data

import java.net.InetSocketAddress
import java.net.Socket
import kotlin.math.max

/** Performs a small real network check against the companion TCP endpoint. */
class ConnectionProbe(
    private val port: Int = DEFAULT_PORT,
    private val timeoutMs: Int = DEFAULT_TIMEOUT_MS
) {
    suspend fun probe(host: String): ProbeResult {
        val startedAt = System.nanoTime()
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), timeoutMs)
            }
            val elapsedMs = max(1L, (System.nanoTime() - startedAt) / 1_000_000L).toInt()
            ProbeResult.Success(elapsedMs)
        } catch (exception: Exception) {
            ProbeResult.Failure(exception.message ?: "Unable to reach companion")
        }
    }

    sealed interface ProbeResult {
        data class Success(val latencyMs: Int) : ProbeResult
        data class Failure(val reason: String) : ProbeResult
    }

    private companion object {
        const val DEFAULT_PORT = 8085
        const val DEFAULT_TIMEOUT_MS = 1_500
    }
}
