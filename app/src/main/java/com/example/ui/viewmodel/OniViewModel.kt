package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.Workspace
import com.example.data.database.MacroButton
import com.example.data.database.RecentColor
import com.example.data.database.BrushFavorite
import com.example.data.datastore.SettingsStore
import com.example.data.repository.OniRepository
import com.example.service.ConnectionService
import com.example.service.ConnectionStatus
import com.example.service.ProtocolLog
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OniViewModel(
    private val repository: OniRepository,
    private val settingsStore: SettingsStore,
    private val connectionService: ConnectionService
) : ViewModel() {

    // Connection Info
    val status: StateFlow<ConnectionStatus> = connectionService.status
    val latency: StateFlow<Int> = connectionService.latency
    val cpuUsage: StateFlow<Int> = connectionService.cpuUsage
    val ramUsage: StateFlow<Int> = connectionService.ramUsage
    val batteryLevel: StateFlow<Int> = connectionService.batteryLevel
    val protocolLogs: StateFlow<List<ProtocolLog>> = connectionService.logs

    // Settings Store Streams
    val ipAddress: StateFlow<String> = settingsStore.ipAddressFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "192.168.1.100"
    )

    val port: StateFlow<Int> = settingsStore.portFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 8000
    )

    val hapticsEnabled: StateFlow<Boolean> = settingsStore.hapticsEnabledFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val voiceEnabled: StateFlow<Boolean> = settingsStore.voiceEnabledFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val themeMode: StateFlow<String> = settingsStore.themeModeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "dark"
    )

    // Room Database Streams
    val workspaces: StateFlow<List<Workspace>> = repository.getWorkspaces().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val activeWorkspaceId: StateFlow<String> = settingsStore.activeWorkspaceFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "illustration"
    )

    val activeWorkspace: StateFlow<Workspace?> = combine(workspaces, activeWorkspaceId) { list, activeId ->
        list.find { it.id == activeId } ?: list.firstOrNull()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val buttons: StateFlow<List<MacroButton>> = activeWorkspaceId.flatMapLatest { id ->
        repository.getButtons(id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val favoriteBrushes: StateFlow<List<BrushFavorite>> = activeWorkspaceId.flatMapLatest { id ->
        repository.getFavorites(id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentColors: StateFlow<List<RecentColor>> = repository.getRecentColors().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Trigger Connection Action
    fun toggleConnection() {
        viewModelScope.launch {
            if (status.value == ConnectionStatus.Connected) {
                connectionService.disconnect()
            } else {
                connectionService.connect(ipAddress.value, port.value)
            }
        }
    }

    // Trigger Shortcut Execution
    fun triggerShortcut(actionLabel: String, actionShortcut: String) {
        viewModelScope.launch {
            connectionService.sendCommand("shortcut", mapOf("keys" to listOf(actionShortcut)))
        }
    }

    // Trigger Canvas Actions
    fun triggerCanvasAction(action: String, payload: Map<String, Any>? = null) {
        viewModelScope.launch {
            connectionService.sendCommand(action, payload)
        }
    }

    // Switch active workspace selection
    fun changeWorkspace(workspaceId: String) {
        viewModelScope.launch {
            settingsStore.saveActiveWorkspace(workspaceId)
            connectionService.sendCommand("switch_workspace", mapOf("workspaceId" to workspaceId))
        }
    }

    // Save Settings updates
    fun saveSettings(ip: String, portVal: Int, haptics: Boolean, voice: Boolean, theme: String) {
        viewModelScope.launch {
            settingsStore.saveIpAddress(ip)
            settingsStore.savePort(portVal)
            settingsStore.saveHapticsEnabled(haptics)
            settingsStore.saveVoiceEnabled(voice)
            settingsStore.saveThemeMode(theme)
            
            connectionService.sendCommand("set_theme", mapOf("theme" to theme))
        }
    }

    // Save custom new button to current workspace
    fun addMacroButton(label: String, shortcut: String, colorHex: String, page: Int, row: Int, col: Int) {
        viewModelScope.launch {
            val currentId = activeWorkspaceId.value
            val newBtn = MacroButton(
                workspaceId = currentId,
                label = label,
                iconName = "Star",
                colorHex = colorHex,
                actionShortcut = shortcut,
                page = page,
                row = row,
                column = col
            )
            repository.addButton(newBtn)
        }
    }

    // Add recent color used
    fun selectColor(hex: String) {
        viewModelScope.launch {
            repository.addRecentColor(hex)
            connectionService.sendCommand("color_picker", mapOf("hex" to hex))
        }
    }

    // Delete custom button
    fun deleteMacroButton(id: Int) {
        viewModelScope.launch {
            repository.deleteButton(id)
        }
    }
}

class OniViewModelFactory(
    private val repository: OniRepository,
    private val settingsStore: SettingsStore,
    private val connectionService: ConnectionService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OniViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OniViewModel(repository, settingsStore, connectionService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
