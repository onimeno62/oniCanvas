package com.onimeno.onicanvas.feature.workspace.state

import kotlinx.serialization.Serializable

@Serializable
sealed interface MacroAction {
    @Serializable
    object Undo : MacroAction
    
    @Serializable
    object Redo : MacroAction
    
    @Serializable
    object Save : MacroAction
    
    @Serializable
    object Brush : MacroAction
    
    @Serializable
    object Eraser : MacroAction
    
    @Serializable
    object Fill : MacroAction
    
    @Serializable
    object Selection : MacroAction
    
    @Serializable
    object Transform : MacroAction
    
    @Serializable
    object Copy : MacroAction
    
    @Serializable
    object Paste : MacroAction
    
    @Serializable
    data class CustomShortcut(
        val keys: List<String>,
        val modifiers: List<String> = emptyList()
    ) : MacroAction
}

@Serializable
data class MacroButton(
    val id: String,
    val position: Int,
    val label: String,
    val iconName: String,
    val action: MacroAction,
    val longPressAction: MacroAction? = null,
    val repeatEnabled: Boolean = false,
    val enabled: Boolean = true,
    val hidden: Boolean = false,
    val colorHex: String? = null
)

@Serializable
data class MacroPage(
    val id: String,
    val name: String,
    val orderIndex: Int,
    val buttons: List<MacroButton> = emptyList()
)
