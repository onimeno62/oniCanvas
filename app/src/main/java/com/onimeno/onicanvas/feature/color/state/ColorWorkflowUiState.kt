package com.onimeno.onicanvas.feature.color.state

import com.onimeno.onicanvas.feature.color.model.ColorModel
import com.onimeno.onicanvas.feature.color.model.ColorPalette

enum class ColorPickerMode {
    WHEEL,
    HSV,
    RGB,
    PALETTES
}

enum class PaletteDialogMode {
    NONE,
    CREATE,
    RENAME,
    DELETE_CONFIRM,
    CLEAR_RECENT_CONFIRM
}

data class ColorWorkflowUiState(
    val selectedColor: ColorModel = ColorModel.DEFAULT,
    val previousColor: ColorModel = ColorModel.DEFAULT,
    val hexInputText: String = ColorModel.DEFAULT.hex,
    val isHexInputValid: Boolean = true,
    val activeMode: ColorPickerMode = ColorPickerMode.WHEEL,
    val palettes: List<ColorPalette> = emptyList(),
    val selectedPaletteId: String? = null,
    val recentColors: List<String> = emptyList(),
    val isConnected: Boolean = false,
    val hostName: String? = null,
    val lastDispatchedHex: String? = null,
    val statusMessage: String? = null,
    val dialogMode: PaletteDialogMode = PaletteDialogMode.NONE,
    val dialogInputText: String = ""
) {
    val selectedPalette: ColorPalette?
        get() = palettes.firstOrNull { it.id == selectedPaletteId } ?: palettes.firstOrNull()
}
