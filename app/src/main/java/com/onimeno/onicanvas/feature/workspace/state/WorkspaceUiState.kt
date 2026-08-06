package com.onimeno.onicanvas.feature.workspace.state

data class WorkspaceItem(
    val id: String,
    val name: String,
    val description: String,
    val targetApp: String,
    val buttonCount: Int,
    val iconName: String,
    val isFavorite: Boolean = false,
    val lastUsed: String
)

sealed interface WorkspaceUiState {
    object Loading : WorkspaceUiState
    data class Success(
        val workspaces: List<WorkspaceItem>,
        val searchQuery: String = "",
        val showFavoritesOnly: Boolean = false
    ) : WorkspaceUiState
    data class Error(val message: String) : WorkspaceUiState
}
