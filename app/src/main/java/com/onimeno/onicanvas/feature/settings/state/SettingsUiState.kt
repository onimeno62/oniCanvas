package com.onimeno.onicanvas.feature.settings.state

data class SettingsData(
    val darkTheme: Boolean,
    val dynamicColor: Boolean,
    val tabletMode: Boolean,
    val autoConnect: Boolean,
    val animationsEnabled: Boolean,
    val language: String,
    val lastBackupDate: String
)

sealed interface SettingsUiState {
    object Loading : SettingsUiState
    data class Success(val settings: SettingsData) : SettingsUiState
    data class Error(val message: String) : SettingsUiState
}
