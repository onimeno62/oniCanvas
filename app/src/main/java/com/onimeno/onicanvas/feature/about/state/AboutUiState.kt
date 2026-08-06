package com.onimeno.onicanvas.feature.about.state

data class ChangelogItem(
    val version: String,
    val date: String,
    val changes: List<String>
)

data class AboutData(
    val appVersion: String,
    val developer: String,
    val website: String,
    val docsLink: String,
    val license: String,
    val changelogs: List<ChangelogItem>,
    val acknowledgements: List<String>
)

sealed interface AboutUiState {
    object Loading : AboutUiState
    data class Success(val info: AboutData) : AboutUiState
    data class Error(val message: String) : AboutUiState
}
