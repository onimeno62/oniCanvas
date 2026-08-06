package com.onimeno.onicanvas.feature.profiles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onimeno.onicanvas.feature.profiles.state.AppProfile
import com.onimeno.onicanvas.feature.profiles.state.ProfilesUiState
import com.onimeno.onicanvas.feature.profiles.state.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfilesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ProfilesUiState>(ProfilesUiState.Loading)
    val uiState: StateFlow<ProfilesUiState> = _uiState.asStateFlow()

    private var currentUser = UserProfile(
        username = "OniArtist_Studio",
        artistTier = "Professional Illustrator",
        activeProfileId = "csp_master",
        syncCount = 248
    )

    private val allProfiles = mutableListOf(
        AppProfile(
            id = "csp_master",
            name = "CSP Master Layout",
            targetApp = "Clip Studio Paint",
            description = "Optimized for digital ink, sketch layers, and flat coloring tool sets.",
            layoutCount = 4,
            isActive = true,
            isDefault = true
        ),
        AppProfile(
            id = "photoshop_speed",
            name = "Photoshop SpeedPaint",
            targetApp = "Photoshop",
            description = "Tailored for digital concept art, high opacity brush flow sliders, and fast undo layers.",
            layoutCount = 3,
            isActive = false,
            isDefault = false
        ),
        AppProfile(
            id = "krita_animation",
            name = "Krita Animator Layout",
            targetApp = "Krita",
            description = "Custom macro setup mapped to animation frames, onion skin toggles, and drawing tools.",
            layoutCount = 2,
            isActive = false,
            isDefault = false
        ),
        AppProfile(
            id = "blender_sculpt",
            name = "Blender Sculpting Companion",
            targetApp = "Blender",
            description = "Camera workspace controls, grab/draw brush modifiers, and custom subdivisions.",
            layoutCount = 5,
            isActive = false,
            isDefault = false
        )
    )

    init {
        loadProfiles()
    }

    fun loadProfiles() {
        viewModelScope.launch {
            _uiState.value = ProfilesUiState.Loading
            delay(300)
            updateState()
        }
    }

    fun selectProfile(id: String) {
        val index = allProfiles.indexOfFirst { it.id == id }
        if (index != -1) {
            // Uncheck previous active, check current
            for (i in allProfiles.indices) {
                allProfiles[i] = allProfiles[i].copy(isActive = allProfiles[i].id == id)
            }
            currentUser = currentUser.copy(activeProfileId = id)
            updateState()
        }
    }

    fun addProfile(name: String, targetApp: String, description: String) {
        val newId = "custom_${System.currentTimeMillis()}"
        allProfiles.add(
            AppProfile(
                id = newId,
                name = name,
                targetApp = targetApp,
                description = description,
                layoutCount = 1,
                isActive = false,
                isDefault = false
            )
        )
        updateState()
    }

    private fun updateState() {
        _uiState.value = ProfilesUiState.Success(
            user = currentUser,
            availableProfiles = allProfiles.toList()
        )
    }
}
