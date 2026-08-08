package com.onimeno.onicanvas.feature.workspace.data

import com.onimeno.onicanvas.feature.workspace.state.ControlModule
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WorkspaceRepository {

    private val _workspaces = MutableStateFlow<List<WorkspaceItem>>(emptyList())
    val workspaces: StateFlow<List<WorkspaceItem>> = _workspaces.asStateFlow()

    init {
        _workspaces.value = listOf(
            WorkspaceItem("illust_layout", "Illustration Master", "Optimized for painting, blending, and detailing brush settings", "Clip Studio Paint", 16, "brush", true, "10 mins ago", listOf(ControlModule.BRUSH_CONTROLS, ControlModule.MACRO_PAD, ControlModule.RADIAL_MENU, ControlModule.GESTURE_PAD)),
            WorkspaceItem("manga_layout", "Manga Page Setup", "Fast paneling, line-art macros, and halftone layers", "Clip Studio Paint", 12, "book", true, "2 hours ago", listOf(ControlModule.MACRO_PAD, ControlModule.SHORTCUT_GRID, ControlModule.GESTURE_PAD)),
            WorkspaceItem("sculpt_3d", "3D Sculpt Companion", "Camera rotation, brush sizing, and viewport settings", "Blender", 20, "cube", false, "Yesterday", listOf(ControlModule.RADIAL_MENU, ControlModule.MACRO_PAD, ControlModule.GESTURE_PAD, ControlModule.BRUSH_CONTROLS)),
            WorkspaceItem("photoshop_concept", "Concept Speed-painter", "Quick layer opacity toggles, custom lasso, and brush flow presets", "Photoshop", 14, "palette", false, "3 days ago", listOf(ControlModule.BRUSH_CONTROLS, ControlModule.SHORTCUT_GRID, ControlModule.MACRO_PAD)),
            WorkspaceItem("krita_sketch", "Krita Sketcher", "Minimalist layout mapping core shortcuts, canvas stabilizer, and zoom wheel", "Krita", 8, "edit", true, "Last week", listOf(ControlModule.MACRO_PAD, ControlModule.GESTURE_PAD))
        )
    }

    fun getWorkspace(id: String): WorkspaceItem? = _workspaces.value.find { it.id == id }

    fun createWorkspace(name: String, targetApp: String): WorkspaceItem {
        val newItem = WorkspaceItem("custom_${System.currentTimeMillis()}", name, "Custom layout for $targetApp actions", targetApp, 12, "category", false, "Just now", ControlModule.values().toList())
        _workspaces.value = listOf(newItem) + _workspaces.value
        return newItem
    }

    fun importWorkspace(importedName: String, appName: String): WorkspaceItem {
        val newItem = WorkspaceItem("imported_${System.currentTimeMillis()}", importedName, "Imported layout tailored to $appName shortcuts", appName, 18, "folder_special", true, "Just imported", ControlModule.values().toList())
        _workspaces.value = listOf(newItem) + _workspaces.value
        return newItem
    }

    fun saveWorkspace(workspace: WorkspaceItem) {
        val currentList = _workspaces.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == workspace.id }
        if (index != -1) currentList[index] = workspace else currentList.add(0, workspace)
        _workspaces.value = currentList
    }

    fun deleteWorkspace(id: String) {
        _workspaces.value = _workspaces.value.filterNot { it.id == id }
    }

    fun duplicateWorkspace(id: String, newName: String): WorkspaceItem? {
        val original = getWorkspace(id) ?: return null
        val duplicated = original.copy(id = "duplicated_${System.currentTimeMillis()}", name = newName, description = "Duplicate of ${original.name}", isFavorite = false, lastUsed = "Just duplicated")
        val currentList = _workspaces.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) currentList.add(index + 1, duplicated) else currentList.add(0, duplicated)
        _workspaces.value = currentList
        return duplicated
    }

    fun toggleFavorite(id: String) {
        _workspaces.value = _workspaces.value.map { if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it }
    }
}
