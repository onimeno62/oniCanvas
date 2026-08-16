package com.onimeno.onicanvas.feature.color.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "color_palettes")
data class ColorPaletteEntity(
    @PrimaryKey val id: String,
    val name: String,
    val colorsJson: String, // JSON array of hex strings
    val isDefault: Boolean,
    val sortOrder: Int,
    val createdAt: Long
)

@Entity(tableName = "recent_colors")
data class RecentColorEntity(
    @PrimaryKey val hex: String,
    val lastUsedTimestamp: Long
)
