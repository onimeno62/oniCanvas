package com.onimeno.onicanvas.feature.workspace.data

import com.onimeno.onicanvas.feature.workspace.state.ControlModule
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WorkspaceRepository private constructor() {

    private val _workspaces = MutableStateFlow<List<WorkspaceItem>>(emptyList())
    val workspaces: StateFlow<List<WorkspaceItem>> = _workspaces.asStateFlow()

    init {
        // Initialize with default workspaces
        _workspaces.value = listOf(
            WorkspaceItem(
                id = "illust_layout",
                name = "Illustration Master",
                description = "Optimized for painting, blending, and detailing brush settings",
                targetApp = "Clip Studio Paint",
                buttonCount = 16,
                iconName = "brush",
                isFavorite = true,
                lastUsed = "10 mins ago",
                enabledModules = listOf(
                    ControlModule.BRUSH_CONTROLS,
                    ControlModule.MACRO_PAD,
                    ControlModule.RADIAL_MENU,
                    ControlModule.GESTURE_PAD
                )
            ),
            WorkspaceItem(
                id = "manga_layout",
                name = "Manga Page Setup",
                description = "Fast paneling, line-art macros, and halftone layers",
                targetApp = "Clip Studio Paint",
                buttonCount = 12,
                iconName = "book",
                isFavorite = true,
                lastUsed = "2 hours ago",
                enabledModules = listOf(
                    ControlModule.MACRO_PAD,
                    ControlModule.SHORTCUT_GRID,
                    ControlModule.GESTURE_PAD
                )
            ),
            WorkspaceItem(
                id = "sculpt_3d",
                name = "3D Sculpt Companion",
                description = "Camera rotation, brush sizing, and viewport settings",
                targetApp = "Blender",
                buttonCount = 20,
                iconName = "cube",
                isFavorite = false,
                lastUsed = "Yesterday",
                enabledModules = listOf(
                    ControlModule.RADIAL_MENU,
                    ControlModule.MACRO_PAD,
                    ControlModule.GESTURE_PAD,
                    ControlModule.BRUSH_CONTROLS
                )
            ),
            WorkspaceItem(
                id = "photoshop_concept",
                name = "Concept Speed-painter",
                description = "Quick layer opacity toggles, custom lasso, and brush flow presets",
                targetApp = "Photoshop",
                buttonCount = 14,
                iconName = "palette",
                isFavorite = false,
                lastUsed = "3 days ago",
                enabledModules = listOf(
                    ControlModule.BRUSH_CONTROLS,
                    ControlModule.SHORTCUT_GRID,
                    ControlModule.MACRO_PAD
                )
            ),
            WorkspaceItem(
                id = "krita_sketch",
                name = "Krita Sketcher",
                description = "Minimalist layout mapping core shortcuts, canvas stabilizer, and zoom wheel",
                targetApp = "Krita",
                buttonCount = 8,
                iconName = "edit",
                isFavorite = true,
                lastUsed = "Last week",
                enabledModules = listOf(
                    ControlModule.MACRO_PAD,
                    ControlModule.GESTURE_PAD
                )
            )
        )
    }

    fun getWorkspace(id: String): WorkspaceItem? {
        return _workspaces.value.find { it.id == id }
    }

    fun createWorkspace(name: String, targetApp: String): WorkspaceItem {
        val newId = "custom_${System.currentTimeMillis()}"
        val newItem = WorkspaceItem(
            id = newId,
            name = name,
            description = "Custom layout for $targetApp actions",
            targetApp = targetApp,
            buttonCount = 12,
            iconName = "category",
            isFavorite = false,
            lastUsed = "Just now",
            enabledModules = ControlModule.values().toList()
        )
        val currentList = _workspaces.value.toMutableList()
        currentList.add(0, newItem)
        _workspaces.value = currentList
        return newItem
    }

    fun importWorkspace(importedName: String, appName: String): WorkspaceItem {
        val newId = "imported_${System.currentTimeMillis()}"
        val newItem = WorkspaceItem(
            id = newId,
            name = importedName,
            description = "Imported layout tailored to $appName shortcuts",
            targetApp = appName,
            buttonCount = 18,
            iconName = "folder_special",
            isFavorite = true,
            lastUsed = "Just imported",
            enabledModules = ControlModule.values().toList()
        )
        val currentList = _workspaces.value.toMutableList()
        currentList.add(0, newItem)
        _workspaces.value = currentList
        return newItem
    }

    fun saveWorkspace(workspace: WorkspaceItem) {
        val currentList = _workspaces.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == workspace.id }
        if (index != -1) {
            currentList[index] = workspace
        } else {
            currentList.add(0, workspace)
        }
        _workspaces.value = currentList
    }

    fun deleteWorkspace(id: String) {
        val currentList = _workspaces.value.toMutableList()
        currentList.removeAll { it.id == id }
        _workspaces.value = currentList
    }

    fun duplicateWorkspace(id: String, newName: String): WorkspaceItem? {
        val original = getWorkspace(id) ?: return null
        val duplicatedId = "duplicated_${System.currentTimeMillis()}"
        val duplicated = original.copy(
            id = duplicatedId,
            name = newName,
            description = "Duplicate of ${original.name}",
            isFavorite = false,
            lastUsed = "Just duplicated"
        )
        val currentList = _workspaces.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            // Insert right after the original
            currentList.add(index + 1, duplicated)
        } else {
            currentList.add(0, duplicated)
        }
        _workspaces.value = currentList
        return duplicated
    }

    fun toggleFavorite(id: String) {
        val currentList = _workspaces.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            val item = currentList[index]
            currentList[index] = item.copy(isFavorite = !item.isFavorite)
            _workspaces.value = currentList
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: WorkspaceRepository? = null

        fun getInstance(): WorkspaceRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = WorkspaceRepository()
                INSTANCE = instance
                instance
            }
        }
    }
}
