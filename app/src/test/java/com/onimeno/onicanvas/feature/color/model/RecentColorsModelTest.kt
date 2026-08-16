package com.onimeno.onicanvas.feature.color.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RecentColorsModelTest {

    @Test
    fun `add adds color to front and preserves newest-first ordering`() {
        val recent = RecentColors(
            colors = listOf("#111111", "#222222"),
            maxCapacity = 5
        )

        val updated = recent.add("#333333")
        assertEquals(listOf("#333333", "#111111", "#222222"), updated.colors)
    }

    @Test
    fun `add existing color moves it to front without duplicate`() {
        val recent = RecentColors(
            colors = listOf("#111111", "#222222", "#333333"),
            maxCapacity = 5
        )

        val updated = recent.add("#222222")
        assertEquals(listOf("#222222", "#111111", "#333333"), updated.colors)
        assertEquals(3, updated.colors.size)
    }

    @Test
    fun `add enforces max capacity`() {
        val recent = RecentColors(
            colors = listOf("#010101", "#020202", "#030303"),
            maxCapacity = 3
        )

        val updated = recent.add("#040404")
        assertEquals(listOf("#040404", "#010101", "#020202"), updated.colors)
        assertEquals(3, updated.colors.size)
        assertFalse(updated.colors.contains("#030303"))
    }

    @Test
    fun `clear empties list`() {
        val recent = RecentColors(colors = listOf("#111111", "#222222"))
        val cleared = recent.clear()
        assertTrue(cleared.colors.isEmpty())
    }
}
