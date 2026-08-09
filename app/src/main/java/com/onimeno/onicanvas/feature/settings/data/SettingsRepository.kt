package com.onimeno.onicanvas.feature.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.onimeno.onicanvas.feature.settings.state.SettingsData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    private object PreferencesKeys {
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val TABLET_MODE = booleanPreferencesKey("tablet_mode")
        val AUTO_CONNECT = booleanPreferencesKey("auto_connect")
        val ANIMATIONS_ENABLED = booleanPreferencesKey("animations_enabled")
        val LANGUAGE = stringPreferencesKey("language")
        val LAST_BACKUP_DATE = stringPreferencesKey("last_backup_date")
    }

    val settingsFlow: Flow<SettingsData> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            SettingsData(
                darkTheme = preferences[PreferencesKeys.DARK_THEME] ?: true,
                dynamicColor = preferences[PreferencesKeys.DYNAMIC_COLOR] ?: true,
                tabletMode = preferences[PreferencesKeys.TABLET_MODE] ?: false,
                autoConnect = preferences[PreferencesKeys.AUTO_CONNECT] ?: true,
                animationsEnabled = preferences[PreferencesKeys.ANIMATIONS_ENABLED] ?: true,
                language = preferences[PreferencesKeys.LANGUAGE] ?: "English",
                lastBackupDate = preferences[PreferencesKeys.LAST_BACKUP_DATE] ?: "Today, 12:45 PM"
            )
        }

    suspend fun updateDarkTheme(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_THEME] = enabled
        }
    }

    suspend fun updateDynamicColor(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DYNAMIC_COLOR] = enabled
        }
    }

    suspend fun updateTabletMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TABLET_MODE] = enabled
        }
    }

    suspend fun updateAutoConnect(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_CONNECT] = enabled
        }
    }

    suspend fun updateAnimationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ANIMATIONS_ENABLED] = enabled
        }
    }

    suspend fun updateLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = language
        }
    }

    suspend fun updateLastBackupDate(date: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_BACKUP_DATE] = date
        }
    }
}
