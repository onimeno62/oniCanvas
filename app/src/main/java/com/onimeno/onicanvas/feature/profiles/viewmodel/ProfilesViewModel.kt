package com.onimeno.onicanvas.feature.profiles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onimeno.onicanvas.feature.profiles.data.ProfileRepository
import com.onimeno.onicanvas.feature.profiles.state.ProfilesUiState
import com.onimeno.onicanvas.feature.profiles.state.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfilesViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfilesUiState>(ProfilesUiState.Loading)
    val uiState: StateFlow<ProfilesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedIfEmpty()
        }
        observeProfiles()
    }

    fun loadProfiles() {
        _uiState.value = ProfilesUiState.Loading
    }

    fun selectProfile(id: String) {
        viewModelScope.launch {
            repository.selectProfile(id)
        }
    }

    fun addProfile(name: String, targetApp: String, description: String) {
        viewModelScope.launch {
            repository.addProfile(name, targetApp, description)
        }
    }

    private fun observeProfiles() {
        viewModelScope.launch {
            repository.profiles.collect { profiles ->
                val activeProfileId = profiles.firstOrNull { it.isActive }?.id ?: "csp_master"
                _uiState.value = ProfilesUiState.Success(
                    user = UserProfile(
                        username = "OniArtist_Studio",
                        artistTier = "Professional Illustrator",
                        activeProfileId = activeProfileId,
                        syncCount = 248
                    ),
                    availableProfiles = profiles
                )
            }
        }
    }
}
