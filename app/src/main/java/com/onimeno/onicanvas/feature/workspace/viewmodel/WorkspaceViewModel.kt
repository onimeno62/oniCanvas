package com.onimeno.onicanvas.feature.workspace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onimeno.onicanvas.feature.workspace.data.WorkspaceRepository
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkspaceViewModel(
    private val repository: WorkspaceRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _showFavoritesOnly = MutableStateFlow(false)

    val uiState: StateFlow<WorkspaceUiState> = combine(
        repository.workspaces,
        _searchQuery,
        _showFavoritesOnly
    ) { workspaces, searchQuery, showFavoritesOnly ->
        val filtered = workspaces.filter { item ->
            val matchesSearch = item.name.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true) ||
                item.targetApp.contains(searchQuery, ignoreCase = true)
            val matchesFavorite = !showFavoritesOnly || item.isFavorite
            matchesSearch && matchesFavorite
        }
        WorkspaceUiState.Success(filtered, searchQuery, showFavoritesOnly)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WorkspaceUiState.Loading)

    fun loadWorkspaces() {
        _searchQuery.value = ""
        _showFavoritesOnly.value = false
    }

    fun updateSearchQuery(query: String) { _searchQuery.value = query }
    fun toggleFavoritesOnly() { _showFavoritesOnly.value = !_showFavoritesOnly.value }
    fun toggleFavorite(id: String) { viewModelScope.launch { repository.toggleFavorite(id) } }
    fun createWorkspace(name: String, targetApp: String) { viewModelScope.launch { repository.createWorkspace(name, targetApp) } }
    fun importWorkspace(importedName: String, appName: String) { viewModelScope.launch { repository.importWorkspace(importedName, appName) } }
}
