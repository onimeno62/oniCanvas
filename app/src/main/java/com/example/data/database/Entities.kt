package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workspaces")
data class Workspace(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val isDefault: Boolean = false
)

@Entity(tableName = "macro_buttons")
data class MacroButton(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val workspaceId: String,
    val label: String,
    val iconName: String, // Name of Material Icon or custom SVG
    val colorHex: String, // HEX value representing background or glow tint
    val actionShortcut: String, // E.g., "Ctrl+Z", "Ctrl+S", "B"
    val page: Int = 0,
    val row: Int,
    val column: Int
)

@Entity(tableName = "recent_colors")
data class RecentColor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hex: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "brush_favorites")
data class BrushFavorite(
    @PrimaryKey val id: String,
    val workspaceId: String,
    val name: String,
    val category: String,
    val shortcut: String
)
