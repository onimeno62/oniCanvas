package com.example.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "oni_settings")

class SettingsStore(private val context: Context) {

    companion object {
        val ACTIVE_WORKSPACE = stringPreferencesKey("active_workspace")
        val COMPANION_IP = stringPreferencesKey("companion_ip")
        val COMPANION_PORT = intPreferencesKey("companion_port")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val VOICE_ENABLED = booleanPreferencesKey("voice_enabled")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val activeWorkspaceFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[ACTIVE_WORKSPACE] ?: "illustration"
    }

    suspend fun saveActiveWorkspace(workspaceId: String) {
        context.dataStore.edit { preferences ->
            preferences[ACTIVE_WORKSPACE] = workspaceId
        }
    }

    val ipAddressFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[COMPANION_IP] ?: "192.168.1.100"
    }

    suspend fun saveIpAddress(ip: String) {
        context.dataStore.edit { preferences ->
            preferences[COMPANION_IP] = ip
        }
    }

    val portFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[COMPANION_PORT] ?: 8000
    }

    suspend fun savePort(port: Int) {
        context.dataStore.edit { preferences ->
            preferences[COMPANION_PORT] = port
        }
    }

    val hapticsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[HAPTICS_ENABLED] ?: true
    }

    suspend fun saveHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[HAPTICS_ENABLED] = enabled
        }
    }

    val voiceEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[VOICE_ENABLED] ?: false
    }

    suspend fun saveVoiceEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[VOICE_ENABLED] = enabled
        }
    }

    val themeModeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_MODE] ?: "dark"
    }

    suspend fun saveThemeMode(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = theme
        }
    }
}
