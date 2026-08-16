package com.onimeno.onicanvas.feature.color.model

import kotlinx.serialization.Serializable

@Serializable
data class ColorPalette(
    val id: String,
    val name: String,
    val colors: List<String> = emptyList(),
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun addColor(hexInput: String): ColorPalette {
        val normalized = ColorConversion.normalizeHex(hexInput) ?: return this
        if (colors.contains(normalized)) {
            return this // Avoid duplicate
        }
        return copy(colors = colors + normalized)
    }

    fun removeColor(hexInput: String): ColorPalette {
        val normalized = ColorConversion.normalizeHex(hexInput) ?: hexInput.uppercase()
        return copy(colors = colors.filterNot { it.equals(normalized, ignoreCase = true) })
    }

    fun reorderColor(fromIndex: Int, toIndex: Int): ColorPalette {
        if (fromIndex !in colors.indices || toIndex !in colors.indices || fromIndex == toIndex) {
            return this
        }
        val mutable = colors.toMutableList()
        val item = mutable.removeAt(fromIndex)
        mutable.add(toIndex, item)
        return copy(colors = mutable)
    }

    fun rename(newName: String): ColorPalette {
        val trimmed = newName.trim()
        if (trimmed.isBlank()) return this
        return copy(name = trimmed)
    }

    companion object {
        fun defaultPalettes(): List<ColorPalette> = listOf(
            ColorPalette(
                id = "palette_essentials",
                name = "Digital Art Essentials",
                isDefault = true,
                colors = listOf(
                    "#1A1A1A", "#FFFFFF", "#E63946", "#F1FAEE",
                    "#A8DADC", "#457B9D", "#1D3557", "#F4A261",
                    "#E76F51", "#2A9D8F", "#E9C46A", "#264653"
                )
            ),
            ColorPalette(
                id = "palette_skintones",
                name = "Skin Tones",
                isDefault = true,
                colors = listOf(
                    "#FFF5EB", "#FFE0BD", "#FFD1AA", "#F1C27D",
                    "#E0AC69", "#C68642", "#8D5524", "#5C3818",
                    "#3A2312", "#2B170B"
                )
            ),
            ColorPalette(
                id = "palette_neon",
                name = "Vibrant Neon",
                isDefault = true,
                colors = listOf(
                    "#FF0055", "#FF5500", "#FFE600", "#00FF66",
                    "#00F0FF", "#7000FF", "#FF00D4", "#00E5FF",
                    "#76FF03", "#FF3D00"
                )
            ),
            ColorPalette(
                id = "palette_pastel",
                name = "Pastel Dreams",
                isDefault = true,
                colors = listOf(
                    "#FFB3BA", "#FFDFBA", "#FFFFBA", "#BAFFC9",
                    "#BAE1FF", "#D7BAFF", "#FFC6FF", "#BDB2FF",
                    "#A0C4FF", "#9BF6FF"
                )
            ),
            ColorPalette(
                id = "palette_manga",
                name = "Manga Inking & Grays",
                isDefault = true,
                colors = listOf(
                    "#000000", "#1E1E1E", "#383838", "#555555",
                    "#777777", "#9E9E9E", "#C4C4C4", "#E0E0E0",
                    "#F5F5F5", "#FFFFFF"
                )
            )
        )
    }
}
