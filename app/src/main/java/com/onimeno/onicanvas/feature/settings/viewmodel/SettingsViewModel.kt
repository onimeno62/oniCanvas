package com.onimeno.onicanvas.feature.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onimeno.onicanvas.feature.settings.data.SettingsRepository
import com.onimeno.onicanvas.feature.settings.state.SettingsData
import com.onimeno.onicanvas.feature.settings.state.SettingsUiState
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private var lastSettings = SettingsData(
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
            repository.settingsFlow
                .catch { exception ->
                    _uiState.value = SettingsUiState.Error(exception.message ?: "Unknown error")
                }
                .collect { settings ->
                    lastSettings = settings
                    _uiState.value = SettingsUiState.Success(settings)
                }
        }
    }

    fun toggleDarkTheme() {
        viewModelScope.launch {
            repository.updateDarkTheme(!lastSettings.darkTheme)
        }
    }

    fun toggleDynamicColor() {
        viewModelScope.launch {
            repository.updateDynamicColor(!lastSettings.dynamicColor)
        }
    }

    fun toggleTabletMode() {
        viewModelScope.launch {
            repository.updateTabletMode(!lastSettings.tabletMode)
        }
    }

    fun toggleAutoConnect() {
        viewModelScope.launch {
            repository.updateAutoConnect(!lastSettings.autoConnect)
        }
    }

    fun toggleAnimations() {
        viewModelScope.launch {
            repository.updateAnimationsEnabled(!lastSettings.animationsEnabled)
        }
    }

    fun changeLanguage(newLang: String) {
        viewModelScope.launch {
            repository.updateLanguage(newLang)
        }
    }

    fun performBackup() {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
            val formattedDate = sdf.format(Date())
            repository.updateLastBackupDate(formattedDate)
        }
    }
}
