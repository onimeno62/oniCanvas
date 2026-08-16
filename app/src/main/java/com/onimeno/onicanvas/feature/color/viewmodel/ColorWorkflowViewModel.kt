package com.onimeno.onicanvas.feature.color.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onimeno.onicanvas.core.designsystem.components.OniStatus
import com.onimeno.onicanvas.feature.color.data.ColorWorkflowRepository
import com.onimeno.onicanvas.feature.color.model.ColorConversion
import com.onimeno.onicanvas.feature.color.model.ColorModel
import com.onimeno.onicanvas.feature.color.model.ColorPalette
import com.onimeno.onicanvas.feature.color.state.ColorPickerMode
import com.onimeno.onicanvas.feature.color.state.ColorWorkflowUiState
import com.onimeno.onicanvas.feature.color.state.PaletteDialogMode
import com.onimeno.onicanvas.feature.connection.data.ConnectionRepository
import com.onimeno.onicanvas.feature.connection.data.ConnectionSnapshot
import com.onimeno.onicanvas.feature.connection.state.ConnectionPhase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ColorWorkflowViewModel(
    private val colorWorkflowRepository: ColorWorkflowRepository,
    private val connectionRepository: ConnectionRepository
) : ViewModel() {

    private val _selectedColor = MutableStateFlow(ColorModel.DEFAULT)
    private val _previousColor = MutableStateFlow(ColorModel.DEFAULT)
    private val _hexInputText = MutableStateFlow(ColorModel.DEFAULT.hex)
    private val _isHexInputValid = MutableStateFlow(true)
    private val _activeMode = MutableStateFlow(ColorPickerMode.WHEEL)
    private val _selectedPaletteId = MutableStateFlow<String?>(null)
    private val _dialogMode = MutableStateFlow(PaletteDialogMode.NONE)
    private val _dialogInputText = MutableStateFlow("")
    private val _targetPaletteId = MutableStateFlow<String?>(null)
    private val _statusMessage = MutableStateFlow<String?>(null)
    private val _lastDispatchedHex = MutableStateFlow<String?>(null)

    private var dispatchJob: Job? = null
    private var lastDispatchTime = 0L
    private val dispatchThrottleMs = 30L

    val uiState: StateFlow<ColorWorkflowUiState> = combine(
        _selectedColor,
        _previousColor,
        _hexInputText,
        _isHexInputValid,
        _activeMode,
        _selectedPaletteId,
        _dialogMode,
        _dialogInputText,
        _statusMessage,
        _lastDispatchedHex,
        colorWorkflowRepository.palettes,
        colorWorkflowRepository.recentColors,
        connectionRepository.state
    ) { params: Array<Any?> ->
        val selectedColor = params[0] as ColorModel
        val previousColor = params[1] as ColorModel
        val hexInputText = params[2] as String
        val isHexInputValid = params[3] as Boolean
        val activeMode = params[4] as ColorPickerMode
        val selectedPaletteId = params[5] as String?
        val dialogMode = params[6] as PaletteDialogMode
        val dialogInputText = params[7] as String
        val statusMessage = params[8] as String?
        val lastDispatchedHex = params[9] as String?
        @Suppress("UNCHECKED_CAST")
        val palettes = params[10] as List<ColorPalette>
        @Suppress("UNCHECKED_CAST")
        val recentColors = params[11] as List<String>
        val connectionSnapshot = params[12] as ConnectionSnapshot

        val isConnected = connectionSnapshot.phase == ConnectionPhase.CONNECTED || connectionSnapshot.status == OniStatus.SUCCESS
        val hostName = connectionSnapshot.activeHostName

        val resolvedPaletteId = selectedPaletteId ?: palettes.firstOrNull()?.id

        ColorWorkflowUiState(
            selectedColor = selectedColor,
            previousColor = previousColor,
            hexInputText = hexInputText,
            isHexInputValid = isHexInputValid,
            activeMode = activeMode,
            palettes = palettes,
            selectedPaletteId = resolvedPaletteId,
            recentColors = recentColors,
            isConnected = isConnected,
            hostName = hostName,
            lastDispatchedHex = lastDispatchedHex,
            statusMessage = statusMessage,
            dialogMode = dialogMode,
            dialogInputText = dialogInputText
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ColorWorkflowUiState()
    )

    fun selectColor(color: ColorModel, recordRecent: Boolean = false, syncHexInput: Boolean = true) {
        if (_selectedColor.value != color) {
            _previousColor.value = _selectedColor.value
            _selectedColor.value = color
            if (syncHexInput) {
                _hexInputText.value = color.hex
                _isHexInputValid.value = true
            }
            dispatchColorThrottled(color.hex)
        }
        if (recordRecent) {
            commitColor(color)
        }
    }

    fun updateHsv(hue: Float, saturation: Float, value: Float, commitToRecent: Boolean = false) {
        val color = ColorConversion.fromHsv(hue, saturation, value)
        _selectedColor.value = color
        _hexInputText.value = color.hex
        _isHexInputValid.value = true
        dispatchColorThrottled(color.hex)
        if (commitToRecent) {
            commitColor(color)
        }
    }

    fun updateHue(hue: Float) {
        val currentHsv = _selectedColor.value.hsv
        updateHsv(hue, currentHsv.saturation, currentHsv.value)
    }

    fun updateSaturation(saturation: Float) {
        val currentHsv = _selectedColor.value.hsv
        updateHsv(currentHsv.hue, saturation, currentHsv.value)
    }

    fun updateValue(value: Float) {
        val currentHsv = _selectedColor.value.hsv
        updateHsv(currentHsv.hue, currentHsv.saturation, value)
    }

    fun updateRgb(r: Int, g: Int, b: Int, commitToRecent: Boolean = false) {
        val color = ColorConversion.fromRgb(r, g, b)
        _selectedColor.value = color
        _hexInputText.value = color.hex
        _isHexInputValid.value = true
        dispatchColorThrottled(color.hex)
        if (commitToRecent) {
            commitColor(color)
        }
    }

    fun updateRed(red: Int) {
        val current = _selectedColor.value
        updateRgb(red, current.g, current.b)
    }

    fun updateGreen(green: Int) {
        val current = _selectedColor.value
        updateRgb(current.r, green, current.b)
    }

    fun updateBlue(blue: Int) {
        val current = _selectedColor.value
        updateRgb(current.r, current.g, blue)
    }

    fun updateHexInput(hexInput: String) {
        _hexInputText.value = hexInput
        val normalized = ColorConversion.normalizeHex(hexInput)
        if (normalized != null) {
            _isHexInputValid.value = true
            val color = ColorConversion.fromHex(normalized)
            if (_selectedColor.value != color) {
                _previousColor.value = _selectedColor.value
                _selectedColor.value = color
                dispatchColorThrottled(color.hex)
            }
        } else {
            _isHexInputValid.value = false
        }
    }

    fun applyHexInput() {
        val normalized = ColorConversion.normalizeHex(_hexInputText.value)
        if (normalized != null) {
            val color = ColorConversion.fromHex(normalized)
            selectColor(color, recordRecent = true)
        } else {
            // Revert to valid selected color
            _hexInputText.value = _selectedColor.value.hex
            _isHexInputValid.value = true
        }
    }

    fun swapWithPreviousColor() {
        val current = _selectedColor.value
        val prev = _previousColor.value
        _selectedColor.value = prev
        _previousColor.value = current
        _hexInputText.value = prev.hex
        _isHexInputValid.value = true
        commitColor(prev)
    }

    fun commitColor(color: ColorModel = _selectedColor.value) {
        viewModelScope.launch {
            colorWorkflowRepository.recordRecentColor(color.hex)
            dispatchColorImmediately(color.hex)
        }
    }

    fun setActiveMode(mode: ColorPickerMode) {
        _activeMode.value = mode
    }

    fun selectPalette(paletteId: String) {
        _selectedPaletteId.value = paletteId
    }

    fun openCreatePaletteDialog() {
        _dialogInputText.value = ""
        _dialogMode.value = PaletteDialogMode.CREATE
    }

    fun openRenamePaletteDialog(paletteId: String) {
        val palette = uiState.value.palettes.firstOrNull { it.id == paletteId }
        _targetPaletteId.value = paletteId
        _dialogInputText.value = palette?.name ?: ""
        _dialogMode.value = PaletteDialogMode.RENAME
    }

    fun openDeletePaletteDialog(paletteId: String) {
        _targetPaletteId.value = paletteId
        _dialogMode.value = PaletteDialogMode.DELETE_CONFIRM
    }

    fun openClearRecentDialog() {
        _dialogMode.value = PaletteDialogMode.CLEAR_RECENT_CONFIRM
    }

    fun updateDialogInput(text: String) {
        _dialogInputText.value = text
    }

    fun dismissDialog() {
        _dialogMode.value = PaletteDialogMode.NONE
        _dialogInputText.value = ""
        _targetPaletteId.value = null
    }

    fun confirmDialog() {
        val mode = _dialogMode.value
        val input = _dialogInputText.value.trim()
        val targetId = _targetPaletteId.value

        viewModelScope.launch {
            when (mode) {
                PaletteDialogMode.CREATE -> {
                    if (input.isNotBlank()) {
                        val newPalette = colorWorkflowRepository.createPalette(input, listOf(_selectedColor.value.hex))
                        _selectedPaletteId.value = newPalette.id
                        showStatus("Created palette '${newPalette.name}'")
                    }
                }
                PaletteDialogMode.RENAME -> {
                    if (targetId != null && input.isNotBlank()) {
                        val existing = uiState.value.palettes.firstOrNull { it.id == targetId }
                        if (existing != null) {
                            colorWorkflowRepository.savePalette(existing.rename(input))
                            showStatus("Renamed palette to '$input'")
                        }
                    }
                }
                PaletteDialogMode.DELETE_CONFIRM -> {
                    if (targetId != null) {
                        colorWorkflowRepository.deletePalette(targetId)
                        val remaining = uiState.value.palettes.filterNot { it.id == targetId }
                        _selectedPaletteId.value = remaining.firstOrNull()?.id
                        showStatus("Deleted palette")
                    }
                }
                PaletteDialogMode.CLEAR_RECENT_CONFIRM -> {
                    colorWorkflowRepository.clearRecentColors()
                    showStatus("Cleared recent colors")
                }
                PaletteDialogMode.NONE -> Unit
            }
            dismissDialog()
        }
    }

    fun addColorToCurrentPalette(hex: String = _selectedColor.value.hex) {
        val currentPalette = uiState.value.selectedPalette ?: return
        viewModelScope.launch {
            colorWorkflowRepository.addColorToPalette(currentPalette.id, hex)
            showStatus("Added $hex to '${currentPalette.name}'")
        }
    }

    fun removeColorFromPalette(paletteId: String, hex: String) {
        viewModelScope.launch {
            colorWorkflowRepository.removeColorFromPalette(paletteId, hex)
        }
    }

    fun reorderColorInPalette(paletteId: String, fromIndex: Int, toIndex: Int) {
        val palette = uiState.value.palettes.firstOrNull { it.id == paletteId } ?: return
        val updated = palette.reorderColor(fromIndex, toIndex)
        viewModelScope.launch {
            colorWorkflowRepository.savePalette(updated)
        }
    }

    fun removeRecentColor(hex: String) {
        viewModelScope.launch {
            colorWorkflowRepository.removeRecentColor(hex)
        }
    }

    fun clearRecentColors() {
        viewModelScope.launch {
            colorWorkflowRepository.clearRecentColors()
            showStatus("Recent colors cleared")
        }
    }

    fun sendCurrentPaletteToCompanion() {
        val palette = uiState.value.selectedPalette ?: return
        viewModelScope.launch {
            val snapshot = connectionRepository.state.value
            val isConnected = snapshot.phase == ConnectionPhase.CONNECTED || snapshot.status == OniStatus.SUCCESS
            if (isConnected) {
                connectionRepository.commandService.sendColorPalette(palette.name, palette.colors)
                showStatus("Sent '${palette.name}' to companion")
            } else {
                showStatus("Companion disconnected — cannot send palette")
            }
        }
    }

    private fun dispatchColorThrottled(hex: String) {
        val now = System.currentTimeMillis()
        if (now - lastDispatchTime >= dispatchThrottleMs) {
            lastDispatchTime = now
            dispatchColorInternal(hex)
        } else {
            dispatchJob?.cancel()
            dispatchJob = viewModelScope.launch {
                delay(dispatchThrottleMs - (now - lastDispatchTime))
                lastDispatchTime = System.currentTimeMillis()
                dispatchColorInternal(hex)
            }
        }
    }

    private fun dispatchColorImmediately(hex: String) {
        dispatchJob?.cancel()
        lastDispatchTime = System.currentTimeMillis()
        dispatchColorInternal(hex)
    }

    private fun dispatchColorInternal(hex: String) {
        val snapshot = connectionRepository.state.value
        val isConnected = snapshot.phase == ConnectionPhase.CONNECTED || snapshot.status == OniStatus.SUCCESS
        if (isConnected) {
            viewModelScope.launch {
                connectionRepository.commandService.setColorHex(hex)
                _lastDispatchedHex.value = hex
            }
        }
    }

    private fun showStatus(message: String) {
        _statusMessage.value = message
        viewModelScope.launch {
            delay(3000)
            if (_statusMessage.value == message) {
                _statusMessage.value = null
            }
        }
    }
}
