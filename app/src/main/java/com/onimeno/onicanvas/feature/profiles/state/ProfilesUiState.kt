package com.onimeno.onicanvas.feature.profiles.state

data class AppProfile(
    val id: String,
    val name: String,
    val targetApp: String,
    val description: String,
    val layoutCount: Int,
    val isActive: Boolean = false,
    val isDefault: Boolean = false
)

data class UserProfile(
    val username: String,
    val artistTier: String,
    val activeProfileId: String,
    val syncCount: Int
)

sealed interface ProfilesUiState {
    object Loading : ProfilesUiState
    data class Success(
        val user: UserProfile,
        val availableProfiles: List<AppProfile>
    ) : ProfilesUiState
    data class Error(val message: String) : ProfilesUiState
}
