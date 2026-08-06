package com.onimeno.onicanvas.feature.about.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onimeno.onicanvas.feature.about.state.AboutData
import com.onimeno.onicanvas.feature.about.state.AboutUiState
import com.onimeno.onicanvas.feature.about.state.ChangelogItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AboutViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AboutUiState>(AboutUiState.Loading)
    val uiState: StateFlow<AboutUiState> = _uiState.asStateFlow()

    private val staticAboutData = AboutData(
        appVersion = "v1.2.5 (Stable Build)",
        developer = "Onimeno Creative Labs",
        website = "https://github.com/onimeno/onicanvas",
        docsLink = "https://onicanvas.onimeno.com/docs",
        license = "MIT License",
        changelogs = listOf(
            ChangelogItem(
                version = "v1.2.5",
                date = "August 2026",
                changes = listOf(
                    "Implemented full-bleed Material 3 glassmorphic design language.",
                    "Optimized UDP Discovery scan latency down to 12ms.",
                    "Added custom multi-touch gesture layout supports for Zoom and Rotate."
                )
            ),
            ChangelogItem(
                version = "v1.1.0",
                date = "June 2026",
                changes = listOf(
                    "Added live terminal logging controls with severity highlights.",
                    "Improved active profile switching across Krita, Blender, and Photoshop."
                )
            )
        ),
        acknowledgements = listOf(
            "Jetpack Compose Toolkits",
            "Kotlin Coroutines & Flow Engines",
            "Material Icons Extended Library",
            "Android Edge-to-Edge System Bars API"
        )
    )

    init {
        loadAboutInfo()
    }

    fun loadAboutInfo() {
        viewModelScope.launch {
            _uiState.value = AboutUiState.Loading
            delay(150)
            _uiState.value = AboutUiState.Success(staticAboutData)
        }
    }
}
