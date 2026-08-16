package com.onimeno.onicanvas.feature.color.model

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Serializable
data class HsvColor(
    val hue: Float, // 0.0f .. 360.0f
    val saturation: Float, // 0.0f .. 100.0f
    val value: Float // 0.0f .. 100.0f
) {
    val satFraction: Float get() = (saturation / 100f).coerceIn(0f, 1f)
    val valFraction: Float get() = (value / 100f).coerceIn(0f, 1f)
    val hueNormalized: Float get() = ((hue % 360f) + 360f) % 360f
}

@Serializable
data class RgbColor(
    val r: Int, // 0 .. 255
    val g: Int, // 0 .. 255
    val b: Int  // 0 .. 255
) {
    val rClamped: Int get() = r.coerceIn(0, 255)
    val gClamped: Int get() = g.coerceIn(0, 255)
    val bClamped: Int get() = b.coerceIn(0, 255)
}

@Serializable
data class ColorModel(
    val r: Int,
    val g: Int,
    val b: Int,
    val hex: String,
    val hsv: HsvColor
) {
    val composeColor: Color get() = Color(r, g, b)

    companion object {
        fun fromRgb(r: Int, g: Int, b: Int): ColorModel = ColorConversion.fromRgb(r, g, b)
        fun fromHex(hex: String, fallback: ColorModel = DEFAULT): ColorModel = ColorConversion.fromHex(hex, fallback)
        fun fromHsv(h: Float, s: Float, v: Float): ColorModel = ColorConversion.fromHsv(h, s, v)

        val BLACK = ColorConversion.fromRgb(0, 0, 0)
        val WHITE = ColorConversion.fromRgb(255, 255, 255)
        val RED = ColorConversion.fromRgb(255, 0, 0)
        val GREEN = ColorConversion.fromRgb(0, 255, 0)
        val BLUE = ColorConversion.fromRgb(0, 0, 255)
        val DEFAULT = ColorConversion.fromRgb(128, 203, 196)
    }
}

object ColorConversion {

    fun normalizeHex(hexInput: String): String? {
        val trimmed = hexInput.trim().removePrefix("#").uppercase()
        return when (trimmed.length) {
            3 -> {
                if (trimmed.all { it in "0123456789ABCDEF" }) {
                    "#${trimmed[0]}${trimmed[0]}${trimmed[1]}${trimmed[1]}${trimmed[2]}${trimmed[2]}"
                } else null
            }
            6 -> {
                if (trimmed.all { it in "0123456789ABCDEF" }) {
                    "#$trimmed"
                } else null
            }
            8 -> {
                // If 8-digit ARGB / RGBA hex, take RGB portion
                if (trimmed.all { it in "0123456789ABCDEF" }) {
                    "#${trimmed.takeLast(6)}"
                } else null
            }
            else -> null
        }
    }

    fun isValidHex(hexInput: String): Boolean = normalizeHex(hexInput) != null

    fun rgbToHex(r: Int, g: Int, b: Int): String {
        val rc = r.coerceIn(0, 255)
        val gc = g.coerceIn(0, 255)
        val bc = b.coerceIn(0, 255)
        return String.format("#%02X%02X%02X", rc, gc, bc)
    }

    fun hexToRgb(hexInput: String): RgbColor? {
        val normalized = normalizeHex(hexInput) ?: return null
        val clean = normalized.removePrefix("#")
        return runCatching {
            val r = clean.substring(0, 2).toInt(16)
            val g = clean.substring(2, 4).toInt(16)
            val b = clean.substring(4, 6).toInt(16)
            RgbColor(r, g, b)
        }.getOrNull()
    }

    fun rgbToHsv(r: Int, g: Int, b: Int): HsvColor {
        val rf = r.coerceIn(0, 255) / 255f
        val gf = g.coerceIn(0, 255) / 255f
        val bf = b.coerceIn(0, 255) / 255f

        val max = max(rf, max(gf, bf))
        val min = min(rf, min(gf, bf))
        val delta = max - min

        var hue = 0f
        if (delta > 0.00001f) {
            hue = when (max) {
                rf -> 60f * (((gf - bf) / delta) % 6f)
                gf -> 60f * (((bf - rf) / delta) + 2f)
                else -> 60f * (((rf - gf) / delta) + 4f)
            }
            if (hue < 0f) hue += 360f
        }

        val saturation = if (max > 0.00001f) (delta / max) * 100f else 0f
        val value = max * 100f

        return HsvColor(
            hue = ((hue % 360f) + 360f) % 360f,
            saturation = saturation.coerceIn(0f, 100f),
            value = value.coerceIn(0f, 100f)
        )
    }

    fun hsvToRgb(hue: Float, saturation: Float, value: Float): RgbColor {
        val h = (((hue % 360f) + 360f) % 360f)
        val s = (saturation / 100f).coerceIn(0f, 1f)
        val v = (value / 100f).coerceIn(0f, 1f)

        val c = v * s
        val x = c * (1f - abs((h / 60f) % 2f - 1f))
        val m = v - c

        val (rPrime, gPrime, bPrime) = when {
            h < 60f -> Triple(c, x, 0f)
            h < 120f -> Triple(x, c, 0f)
            h < 180f -> Triple(0f, c, x)
            h < 240f -> Triple(0f, x, c)
            h < 300f -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }

        val r = ((rPrime + m) * 255f).roundToInt().coerceIn(0, 255)
        val g = ((gPrime + m) * 255f).roundToInt().coerceIn(0, 255)
        val b = ((bPrime + m) * 255f).roundToInt().coerceIn(0, 255)

        return RgbColor(r, g, b)
    }

    fun hexToHsv(hexInput: String): HsvColor? {
        val rgb = hexToRgb(hexInput) ?: return null
        return rgbToHsv(rgb.r, rgb.g, rgb.b)
    }

    fun fromRgb(r: Int, g: Int, b: Int): ColorModel {
        val rc = r.coerceIn(0, 255)
        val gc = g.coerceIn(0, 255)
        val bc = b.coerceIn(0, 255)
        val hex = rgbToHex(rc, gc, bc)
        val hsv = rgbToHsv(rc, gc, bc)
        return ColorModel(rc, gc, bc, hex, hsv)
    }

    fun fromHsv(hue: Float, saturation: Float, value: Float): ColorModel {
        val rgb = hsvToRgb(hue, saturation, value)
        val hex = rgbToHex(rgb.r, rgb.g, rgb.b)
        val hsvClamped = HsvColor(
            hue = ((hue % 360f) + 360f) % 360f,
            saturation = saturation.coerceIn(0f, 100f),
            value = value.coerceIn(0f, 100f)
        )
        return ColorModel(rgb.r, rgb.g, rgb.b, hex, hsvClamped)
    }

    fun fromHex(hexInput: String, fallback: ColorModel = ColorModel.DEFAULT): ColorModel {
        val normalized = normalizeHex(hexInput) ?: return fallback
        val rgb = hexToRgb(normalized) ?: return fallback
        val hsv = rgbToHsv(rgb.r, rgb.g, rgb.b)
        return ColorModel(rgb.r, rgb.g, rgb.b, normalized, hsv)
    }

    fun fromComposeColor(color: Color): ColorModel {
        val r = (color.red * 255f).roundToInt().coerceIn(0, 255)
        val g = (color.green * 255f).roundToInt().coerceIn(0, 255)
        val b = (color.blue * 255f).roundToInt().coerceIn(0, 255)
        return fromRgb(r, g, b)
    }
}
