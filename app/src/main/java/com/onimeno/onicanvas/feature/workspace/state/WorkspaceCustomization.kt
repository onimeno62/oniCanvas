package com.onimeno.onicanvas.feature.workspace.state

import kotlinx.serialization.Serializable

@Serializable
data class WorkspaceCustomization(
    val themeKey: String = WorkspaceTheme.DEFAULT.key
)

enum class WorkspaceTheme(val key: String, val displayName: String) {
    DEFAULT("default", "Default"),
    MIDNIGHT("midnight", "Midnight"),
    AMBER("amber", "Amber"),
    VIOLET("violet", "Violet"),
    MINT("mint", "Mint");

    companion object {
        fun fromKey(key: String): WorkspaceTheme =
            values().firstOrNull { it.key == key } ?: DEFAULT
    }
}

object WorkspaceIconLibrary {
    val names: List<String> = listOf(
        "category", "brush", "palette", "gesture", "grid", "keyboard",
        "layers", "folder", "star", "bolt", "gamepad", "tune"
    )
}

object WorkspaceActionLibrary {
    val actions: List<MacroAction> = listOf(
        MacroAction.Undo, MacroAction.Redo, MacroAction.Save,
        MacroAction.Brush, MacroAction.Eraser, MacroAction.Fill,
        MacroAction.Selection, MacroAction.Transform, MacroAction.Copy,
        MacroAction.Paste
    )
}
