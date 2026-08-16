package com.onimeno.onicanvas.feature.color.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class ColorConversionTest {

    @Test
    fun `rgbToHex converts accurately`() {
        assertEquals("#FF0000", ColorConversion.rgbToHex(255, 0, 0))
        assertEquals("#00FF00", ColorConversion.rgbToHex(0, 255, 0))
        assertEquals("#0000FF", ColorConversion.rgbToHex(0, 0, 255))
        assertEquals("#FFFFFF", ColorConversion.rgbToHex(255, 255, 255))
        assertEquals("#000000", ColorConversion.rgbToHex(0, 0, 0))
        assertEquals("#80CBC4", ColorConversion.rgbToHex(128, 203, 196))
    }

    @Test
    fun `rgbToHex clamps out-of-range values`() {
        assertEquals("#FF0000", ColorConversion.rgbToHex(300, -10, -50))
        assertEquals("#00FFFF", ColorConversion.rgbToHex(-1, 500, 300))
    }

    @Test
    fun `hexToRgb parses standard 6-digit hex`() {
        val red = ColorConversion.hexToRgb("#FF0000")
        assertNotNull(red)
        assertEquals(255, red!!.r)
        assertEquals(0, red.g)
        assertEquals(0, red.b)

        val green = ColorConversion.hexToRgb("00FF00") // without hash
        assertNotNull(green)
        assertEquals(0, green!!.r)
        assertEquals(255, green.g)
        assertEquals(0, green.b)

        val blue = ColorConversion.hexToRgb("#0000ff") // lowercase
        assertNotNull(blue)
        assertEquals(0, blue!!.r)
        assertEquals(0, blue.g)
        assertEquals(255, blue.b)
    }

    @Test
    fun `hexToRgb parses 3-digit shorthand`() {
        val white = ColorConversion.hexToRgb("#FFF")
        assertNotNull(white)
        assertEquals(255, white!!.r)
        assertEquals(255, white.g)
        assertEquals(255, white.b)

        val red = ColorConversion.hexToRgb("F00")
        assertNotNull(red)
        assertEquals(255, red!!.r)
        assertEquals(0, red.g)
        assertEquals(0, red.b)
    }

    @Test
    fun `hexToRgb returns null on invalid hex`() {
        assertNull(ColorConversion.hexToRgb("GGFFFF"))
        assertNull(ColorConversion.hexToRgb("#12345"))
        assertNull(ColorConversion.hexToRgb(""))
        assertNull(ColorConversion.hexToRgb("invalid"))
    }

    @Test
    fun `rgbToHsv converts primary colors accurately`() {
        // Red: 0°, 100%, 100%
        val hsvRed = ColorConversion.rgbToHsv(255, 0, 0)
        assertEquals(0f, hsvRed.hue, 0.1f)
        assertEquals(100f, hsvRed.saturation, 0.1f)
        assertEquals(100f, hsvRed.value, 0.1f)

        // Green: 120°, 100%, 100%
        val hsvGreen = ColorConversion.rgbToHsv(0, 255, 0)
        assertEquals(120f, hsvGreen.hue, 0.1f)
        assertEquals(100f, hsvGreen.saturation, 0.1f)
        assertEquals(100f, hsvGreen.value, 0.1f)

        // Blue: 240°, 100%, 100%
        val hsvBlue = ColorConversion.rgbToHsv(0, 0, 255)
        assertEquals(240f, hsvBlue.hue, 0.1f)
        assertEquals(100f, hsvBlue.saturation, 0.1f)
        assertEquals(100f, hsvBlue.value, 0.1f)

        // Yellow: 60°, 100%, 100%
        val hsvYellow = ColorConversion.rgbToHsv(255, 255, 0)
        assertEquals(60f, hsvYellow.hue, 0.1f)
        assertEquals(100f, hsvYellow.saturation, 0.1f)
        assertEquals(100f, hsvYellow.value, 0.1f)

        // Cyan: 180°, 100%, 100%
        val hsvCyan = ColorConversion.rgbToHsv(0, 255, 255)
        assertEquals(180f, hsvCyan.hue, 0.1f)
        assertEquals(100f, hsvCyan.saturation, 0.1f)
        assertEquals(100f, hsvCyan.value, 0.1f)

        // Magenta: 300°, 100%, 100%
        val hsvMagenta = ColorConversion.rgbToHsv(255, 0, 255)
        assertEquals(300f, hsvMagenta.hue, 0.1f)
        assertEquals(100f, hsvMagenta.saturation, 0.1f)
        assertEquals(100f, hsvMagenta.value, 0.1f)
    }

    @Test
    fun `rgbToHsv handles grayscale black and white`() {
        // Black: value 0%
        val black = ColorConversion.rgbToHsv(0, 0, 0)
        assertEquals(0f, black.saturation, 0.1f)
        assertEquals(0f, black.value, 0.1f)

        // White: sat 0%, value 100%
        val white = ColorConversion.rgbToHsv(255, 255, 255)
        assertEquals(0f, white.saturation, 0.1f)
        assertEquals(100f, white.value, 0.1f)

        // Mid-gray
        val gray = ColorConversion.rgbToHsv(128, 128, 128)
        assertEquals(0f, gray.saturation, 0.1f)
        assertTrue(abs(gray.value - 50.19f) < 1f)
    }

    @Test
    fun `hsvToRgb converts accurately`() {
        val red = ColorConversion.hsvToRgb(0f, 100f, 100f)
        assertEquals(255, red.r)
        assertEquals(0, red.g)
        assertEquals(0, red.b)

        val green = ColorConversion.hsvToRgb(120f, 100f, 100f)
        assertEquals(0, green.r)
        assertEquals(255, green.g)
        assertEquals(0, green.b)

        val blue = ColorConversion.hsvToRgb(240f, 100f, 100f)
        assertEquals(0, blue.r)
        assertEquals(0, blue.g)
        assertEquals(255, blue.b)

        val white = ColorConversion.hsvToRgb(0f, 0f, 100f)
        assertEquals(255, white.r)
        assertEquals(255, white.g)
        assertEquals(255, white.b)

        val black = ColorConversion.hsvToRgb(0f, 0f, 0f)
        assertEquals(0, black.r)
        assertEquals(0, black.g)
        assertEquals(0, black.b)
    }

    @Test
    fun `RGB - HSV - RGB round trip preserves values`() {
        val testColors = listOf(
            RgbColor(255, 0, 0),
            RgbColor(0, 255, 0),
            RgbColor(0, 0, 255),
            RgbColor(255, 255, 255),
            RgbColor(0, 0, 0),
            RgbColor(128, 64, 32),
            RgbColor(200, 150, 50),
            RgbColor(30, 90, 180),
            RgbColor(220, 80, 140)
        )

        for (rgb in testColors) {
            val hsv = ColorConversion.rgbToHsv(rgb.r, rgb.g, rgb.b)
            val convertedBack = ColorConversion.hsvToRgb(hsv.hue, hsv.saturation, hsv.value)

            // Tolerance within 1 unit due to integer rounding
            assertTrue("Red channel drift: ${rgb.r} vs ${convertedBack.r}", abs(rgb.r - convertedBack.r) <= 1)
            assertTrue("Green channel drift: ${rgb.g} vs ${convertedBack.g}", abs(rgb.g - convertedBack.g) <= 1)
            assertTrue("Blue channel drift: ${rgb.b} vs ${convertedBack.b}", abs(rgb.b - convertedBack.b) <= 1)
        }
    }

    @Test
    fun `HEX - RGB - HEX round trip preserves values`() {
        val hexList = listOf("#FF0000", "#00FF00", "#0000FF", "#FFFFFF", "#000000", "#80CBC4", "#FFA726", "#AB47BC")
        for (hex in hexList) {
            val rgb = ColorConversion.hexToRgb(hex)
            assertNotNull(rgb)
            val hexBack = ColorConversion.rgbToHex(rgb!!.r, rgb.g, rgb.b)
            assertEquals(hex, hexBack)
        }
    }

    @Test
    fun `fromHex with invalid input falls back gracefully`() {
        val fallback = ColorModel.fromRgb(10, 20, 30)
        val result = ColorConversion.fromHex("invalid_hex", fallback)
        assertEquals(fallback, result)
    }

    @Test
    fun `normalizeHex formats correctly`() {
        assertEquals("#AABBCC", ColorConversion.normalizeHex("aabbcc"))
        assertEquals("#AABBCC", ColorConversion.normalizeHex("#aabbcc"))
        assertEquals("#FFFFFF", ColorConversion.normalizeHex("#FFF"))
        assertEquals("#FF0000", ColorConversion.normalizeHex("f00"))
        assertNull(ColorConversion.normalizeHex("invalid"))
    }
}
