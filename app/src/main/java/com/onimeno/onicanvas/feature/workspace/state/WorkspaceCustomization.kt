package com.onimeno.onicanvas.feature.workspace.state

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.ContentCut
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.Crop
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FormatColorFill
import androidx.compose.material.icons.rounded.Gamepad
import androidx.compose.material.icons.rounded.Gesture
import androidx.compose.material.icons.rounded.GridOn
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Redo
import androidx.compose.material.icons.rounded.RotateRight
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.material.icons.rounded.SmartButton
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Transform
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
data class WorkspaceCustomization(
    val themeKey: String = WorkspaceTheme.DEFAULT.key,
    val accentColorHex: String? = null,
    val cornerRadiusDp: Int? = null,
    val enableHaptics: Boolean = true,
    val enableAnimations: Boolean = true
)

enum class WorkspaceTheme(
    val key: String,
    val displayName: String,
    val accentHex: String = "#80CBC4",
    val backgroundHex: String = "#121212",
    val surfaceHex: String = "#1E1E1E"
) {
    DEFAULT("default", "Default", "#80CBC4", "#121212", "#1E1E1E"),
    MIDNIGHT("midnight", "Midnight", "#82B1FF", "#0A0E1A", "#131B2E"),
    AMBER("amber", "Amber", "#FFD54F", "#1A150A", "#2E2413"),
    VIOLET("violet", "Violet", "#CE93D8", "#150A1A", "#26132E"),
    MINT("mint", "Mint", "#A7F3D0", "#081C15", "#1B4332"),
    OLED("oled", "OLED Dark", "#EEEEEE", "#000000", "#111111"),
    OCEAN("ocean", "Ocean Blue", "#4DD0E1", "#051622", "#0E2F44"),
    SUNSET("sunset", "Sunset Coral", "#FF8A65", "#1A0C08", "#2E1710");

    companion object {
        fun fromKey(key: String): WorkspaceTheme =
            entries.firstOrNull { it.key == key } ?: DEFAULT
    }
}

object WorkspaceColorPalette {
    val presetColors: List<String> = listOf(
        "#80CBC4", // Teal / Default
        "#82B1FF", // Electric Blue
        "#CE93D8", // Soft Purple
        "#FFD54F", // Warm Amber
        "#A7F3D0", // Soft Mint
        "#FF8A65", // Warm Coral
        "#EF5350", // Soft Crimson
        "#EC407A", // Vibrant Pink
        "#AB47BC", // Deep Violet
        "#42A5F5", // Sky Blue
        "#26A69A", // Persian Green
        "#66BB6A", // Apple Green
        "#FFA726", // Bright Orange
        "#8D6E63", // Warm Earth
        "#78909C", // Cool Slate
        "#3B82F6", // Blue
        "#10B981", // Emerald
        "#EF4444"  // Red
    )

    val themeAccentColors: List<String> get() = WorkspaceTheme.values().map { it.accentHex }

    fun parseColor(hex: String?, fallback: Color = Color.Unspecified): Color {
        if (hex.isNullOrBlank()) return fallback
        return runCatching {
            val cleanHex = if (hex.startsWith("#")) hex else "#$hex"
            Color(android.graphics.Color.parseColor(cleanHex))
        }.getOrDefault(fallback)
    }
}

object WorkspaceIconLibrary {
    val names: List<String> = listOf(
        "category", "brush", "palette", "gesture", "grid", "keyboard",
        "layers", "folder", "star", "bolt", "gamepad", "tune",
        "undo", "redo", "save", "eraser", "fill", "select",
        "transform", "copy", "paste", "zoom_in", "rotate_right",
        "visibility", "lock", "content_cut", "crop", "navigation",
        "ruler", "eyedropper", "hand", "shortcut"
    )

    val availableIcons: List<String> get() = names

    fun getIcon(name: String): ImageVector {
        return when (name.lowercase()) {
            "undo" -> Icons.Rounded.Undo
            "redo" -> Icons.Rounded.Redo
            "save" -> Icons.Rounded.Save
            "brush" -> Icons.Rounded.Brush
            "eraser" -> Icons.Rounded.CleaningServices
            "fill" -> Icons.Rounded.FormatColorFill
            "select" -> Icons.Rounded.SelectAll
            "transform" -> Icons.Rounded.Transform
            "copy" -> Icons.Rounded.ContentCopy
            "paste" -> Icons.Rounded.ContentPaste
            "category" -> Icons.Rounded.Category
            "palette" -> Icons.Rounded.Palette
            "gesture" -> Icons.Rounded.Gesture
            "grid" -> Icons.Rounded.GridOn
            "keyboard" -> Icons.Rounded.Keyboard
            "layers" -> Icons.Rounded.Layers
            "folder" -> Icons.Rounded.Folder
            "star" -> Icons.Rounded.Star
            "bolt" -> Icons.Rounded.Bolt
            "gamepad" -> Icons.Rounded.Gamepad
            "tune" -> Icons.Rounded.Tune
            "zoom_in" -> Icons.Rounded.ZoomIn
            "rotate_right" -> Icons.Rounded.RotateRight
            "visibility" -> Icons.Rounded.Visibility
            "lock" -> Icons.Rounded.Lock
            "content_cut" -> Icons.Rounded.ContentCut
            "crop" -> Icons.Rounded.Crop
            "navigation" -> Icons.Rounded.Navigation
            else -> Icons.Rounded.SmartButton
        }
    }
}

object WorkspaceActionLibrary {
    val actions: List<MacroAction> = listOf(
        MacroAction.Undo, MacroAction.Redo, MacroAction.Save,
        MacroAction.Brush, MacroAction.Eraser, MacroAction.Fill,
        MacroAction.Selection, MacroAction.Transform, MacroAction.Copy,
        MacroAction.Paste
    )
}

