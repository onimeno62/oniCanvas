package com.onimeno.onicanvas.feature.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onimeno.onicanvas.feature.settings.state.SettingsData
import com.onimeno.onicanvas.feature.settings.state.SettingsUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private var currentSettings = SettingsData(
        darkTheme = true,
        dynamicColor = true,
        tabletMode = false,
        autoConnect = true,
        animationsEnabled = true,
        language = "English",
        lastBackupDate = "Today, 12:45 PM"
    )

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Loading
            delay(200)
            _uiState.value = SettingsUiState.Success(currentSettings)
        }
    }

    fun toggleDarkTheme() {
        currentSettings = currentSettings.copy(darkTheme = !currentSettings.darkTheme)
        updateState()
    }

    fun toggleDynamicColor() {
        currentSettings = currentSettings.copy(dynamicColor = !currentSettings.dynamicColor)
        updateState()
    }

    fun toggleTabletMode() {
        currentSettings = currentSettings.copy(tabletMode = !currentSettings.tabletMode)
        updateState()
    }

    fun toggleAutoConnect() {
        currentSettings = currentSettings.copy(autoConnect = !currentSettings.autoConnect)
        updateState()
    }

    fun toggleAnimations() {
        currentSettings = currentSettings.copy(animationsEnabled = !currentSettings.animationsEnabled)
        updateState()
    }

    fun changeLanguage(newLang: String) {
        currentSettings = currentSettings.copy(language = newLang)
        updateState()
    }

    fun performBackup() {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
            val formattedDate = sdf.format(Date())
            currentSettings = currentSettings.copy(lastBackupDate = formattedDate)
            updateState()
        }
    }

    private fun updateState() {
        _uiState.value = SettingsUiState.Success(currentSettings)
    }
}
