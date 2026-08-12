package com.onimeno.onicanvas.feature.connection.data

/**
 * Defines the deterministic backoff used when an established companion connection is lost.
 *
 * The first attempt is immediate; subsequent attempts wait progressively longer while keeping
 * the total retry count bounded. Keeping this policy separate from the repository makes the
 * reconnect contract easy to verify without sockets or coroutine timing in unit tests.
 */
object ConnectionReconnectPolicy {
    const val MAX_ATTEMPTS = 5

    private val delaysMs = longArrayOf(
        0L,
        2_000L,
        5_000L,
        10_000L,
        10_000L
    )

    fun delayBeforeAttempt(attempt: Int): Long {
        require(attempt in 1..MAX_ATTEMPTS) { "attempt must be between 1 and $MAX_ATTEMPTS" }
        return delaysMs[attempt - 1]
    }
}
