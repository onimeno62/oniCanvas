package com.onimeno.onicanvas.feature.color.model

import kotlinx.serialization.Serializable

@Serializable
data class RecentColors(
    val colors: List<String> = emptyList(),
    val maxCapacity: Int = DEFAULT_MAX_CAPACITY
) {
    fun add(hexInput: String): RecentColors {
        val normalized = ColorConversion.normalizeHex(hexInput) ?: return this
        val filtered = colors.filterNot { it.equals(normalized, ignoreCase = true) }
        val updated = listOf(normalized) + filtered
        return copy(colors = updated.take(maxCapacity))
    }

    fun remove(hexInput: String): RecentColors {
        val normalized = ColorConversion.normalizeHex(hexInput) ?: hexInput.uppercase()
        return copy(colors = colors.filterNot { it.equals(normalized, ignoreCase = true) })
    }

    fun clear(): RecentColors = copy(colors = emptyList())

    companion object {
        const val DEFAULT_MAX_CAPACITY = 24

        val INITIAL_DEFAULTS = listOf(
            "#80CBC4", "#82B1FF", "#CE93D8", "#FFD54F",
            "#A7F3D0", "#FF8A65", "#EF5350", "#EC407A",
            "#AB47BC", "#42A5F5", "#26A69A", "#66BB6A"
        )
    }
}
