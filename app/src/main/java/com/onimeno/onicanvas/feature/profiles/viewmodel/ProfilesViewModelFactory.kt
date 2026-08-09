package com.onimeno.onicanvas.feature.profiles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.onimeno.onicanvas.feature.profiles.data.ProfileRepository

class ProfilesViewModelFactory(
    private val repository: ProfileRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfilesViewModel::class.java)) {
            return ProfilesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
