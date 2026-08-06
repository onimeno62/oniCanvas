package com.onimeno.onicanvas.feature.workspace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceItem
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkspaceViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<WorkspaceUiState>(WorkspaceUiState.Loading)
    val uiState: StateFlow<WorkspaceUiState> = _uiState.asStateFlow()

    private val allWorkspaces = mutableListOf(
        WorkspaceItem(
            id = "illust_layout",
            name = "Illustration Master",
            description = "Optimized for painting, blending, and detailing brush settings",
            targetApp = "Clip Studio Paint",
            buttonCount = 16,
            iconName = "brush",
            isFavorite = true,
            lastUsed = "10 mins ago"
        ),
        WorkspaceItem(
            id = "manga_layout",
            name = "Manga Page Setup",
            description = "Fast paneling, line-art macros, and halftone layers",
            targetApp = "Clip Studio Paint",
            buttonCount = 12,
            iconName = "book",
            isFavorite = true,
            lastUsed = "2 hours ago"
        ),
        WorkspaceItem(
            id = "sculpt_3d",
            name = "3D Sculpt Companion",
            description = "Camera rotation, brush sizing, and viewport settings",
            targetApp = "Blender",
            buttonCount = 20,
            iconName = "cube",
            isFavorite = false,
            lastUsed = "Yesterday"
        ),
        WorkspaceItem(
            id = "photoshop_concept",
            name = "Concept Speed-painter",
            description = "Quick layer opacity toggles, custom lasso, and brush flow presets",
            targetApp = "Photoshop",
            buttonCount = 14,
            iconName = "palette",
            isFavorite = false,
            lastUsed = "3 days ago"
        ),
        WorkspaceItem(
            id = "krita_sketch",
            name = "Krita Sketcher",
            description = "Minimalist layout mapping core shortcuts, canvas stabilizer, and zoom wheel",
            targetApp = "Krita",
            buttonCount = 8,
            iconName = "edit",
            isFavorite = true,
            lastUsed = "Last week"
        )
    )

    private var currentSearchQuery = ""
    private var currentFavoritesOnly = false

    init {
        loadWorkspaces()
    }

    fun loadWorkspaces() {
        viewModelScope.launch {
            _uiState.value = WorkspaceUiState.Loading
            delay(300) // Brief aesthetic delay
            updateState()
        }
    }

    fun updateSearchQuery(query: String) {
        currentSearchQuery = query
        updateState()
    }

    fun toggleFavoritesOnly() {
        currentFavoritesOnly = !currentFavoritesOnly
        updateState()
    }

    fun toggleFavorite(id: String) {
        val index = allWorkspaces.indexOfFirst { it.id == id }
        if (index != -1) {
            val item = allWorkspaces[index]
            allWorkspaces[index] = item.copy(isFavorite = !item.isFavorite)
            updateState()
        }
    }

    fun createWorkspace(name: String, targetApp: String) {
        viewModelScope.launch {
            val newId = "custom_${System.currentTimeMillis()}"
            val newItem = WorkspaceItem(
                id = newId,
                name = name,
                description = "Custom layout for $targetApp actions",
                targetApp = targetApp,
                buttonCount = 12,
                iconName = "category",
                isFavorite = false,
                lastUsed = "Just now"
            )
            allWorkspaces.add(0, newItem)
            updateState()
        }
    }

    fun importWorkspace(importedName: String, appName: String) {
        viewModelScope.launch {
            val newId = "imported_${System.currentTimeMillis()}"
            val newItem = WorkspaceItem(
                id = newId,
                name = importedName,
                description = "Imported layout tailored to $appName shortcuts",
                targetApp = appName,
                buttonCount = 18,
                iconName = "folder_special",
                isFavorite = true,
                lastUsed = "Just imported"
            )
            allWorkspaces.add(0, newItem)
            updateState()
        }
    }

    private fun updateState() {
        val filtered = allWorkspaces.filter { item ->
            val matchesSearch = item.name.contains(currentSearchQuery, ignoreCase = true) ||
                    item.description.contains(currentSearchQuery, ignoreCase = true) ||
                    item.targetApp.contains(currentSearchQuery, ignoreCase = true)
            val matchesFavorite = !currentFavoritesOnly || item.isFavorite
            matchesSearch && matchesFavorite
        }
        _uiState.value = WorkspaceUiState.Success(
            workspaces = filtered,
            searchQuery = currentSearchQuery,
            showFavoritesOnly = currentFavoritesOnly
        )
    }
}
