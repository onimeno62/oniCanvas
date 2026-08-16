package com.onimeno.onicanvas.feature.color.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PaletteModelTest {

    @Test
    fun `addColor prevents duplicates and normalizes hex`() {
        val palette = ColorPalette(
            id = "test_p1",
            name = "Test Palette",
            colors = listOf("#FF0000", "#00FF00")
        )

        val updated = palette.addColor("#0000FF")
        assertEquals(3, updated.colors.size)
        assertEquals("#0000FF", updated.colors.last())

        // Adding duplicate (even with lowercase / without hash) should not create duplicate
        val dup1 = updated.addColor("#ff0000")
        assertEquals(3, dup1.colors.size)

        val dup2 = updated.addColor("00FF00")
        assertEquals(3, dup2.colors.size)
    }

    @Test
    fun `removeColor removes target color`() {
        val palette = ColorPalette(
            id = "test_p1",
            name = "Test Palette",
            colors = listOf("#FF0000", "#00FF00", "#0000FF")
        )

        val removed = palette.removeColor("#00FF00")
        assertEquals(2, removed.colors.size)
        assertFalse(removed.colors.contains("#00FF00"))
        assertTrue(removed.colors.contains("#FF0000"))
        assertTrue(removed.colors.contains("#0000FF"))
    }

    @Test
    fun `reorderColor changes order accurately`() {
        val palette = ColorPalette(
            id = "test_p1",
            name = "Test Palette",
            colors = listOf("#111111", "#222222", "#333333")
        )

        val reordered = palette.reorderColor(0, 2)
        assertEquals(listOf("#222222", "#333333", "#111111"), reordered.colors)
    }

    @Test
    fun `rename changes palette name`() {
        val palette = ColorPalette(
            id = "test_p1",
            name = "Old Name"
        )
        val renamed = palette.rename("New Art Palette")
        assertEquals("New Art Palette", renamed.name)

        val blankRename = renamed.rename("   ")
        assertEquals("New Art Palette", blankRename.name)
    }

    @Test
    fun `defaultPalettes provides expected preset sets`() {
        val presets = ColorPalette.defaultPalettes()
        assertTrue(presets.size >= 4)
        assertTrue(presets.any { it.name.contains("Digital Art") })
        assertTrue(presets.any { it.name.contains("Skin Tones") })
        assertTrue(presets.all { it.isDefault })
        assertTrue(presets.all { it.colors.isNotEmpty() })
    }
}
