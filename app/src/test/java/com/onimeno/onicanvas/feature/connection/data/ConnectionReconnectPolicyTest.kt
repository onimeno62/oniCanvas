package com.onimeno.onicanvas.feature.connection.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ConnectionReconnectPolicyTest {

    @Test
    fun `retry schedule is bounded and deterministic`() {
        assertEquals(5, ConnectionReconnectPolicy.MAX_ATTEMPTS)
        assertEquals(0L, ConnectionReconnectPolicy.delayBeforeAttempt(1))
        assertEquals(2_000L, ConnectionReconnectPolicy.delayBeforeAttempt(2))
        assertEquals(5_000L, ConnectionReconnectPolicy.delayBeforeAttempt(3))
        assertEquals(10_000L, ConnectionReconnectPolicy.delayBeforeAttempt(4))
        assertEquals(10_000L, ConnectionReconnectPolicy.delayBeforeAttempt(5))
    }

    @Test
    fun `attempt outside retry window is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            ConnectionReconnectPolicy.delayBeforeAttempt(0)
        }
        assertThrows(IllegalArgumentException::class.java) {
            ConnectionReconnectPolicy.delayBeforeAttempt(6)
        }
    }
}
