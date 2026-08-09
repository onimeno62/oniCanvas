package com.onimeno.onicanvas.feature.workspace.data

import com.onimeno.onicanvas.feature.workspace.state.ControlModule
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WorkspaceRepository(
    private val dao: WorkspaceDao
) {

    val workspaces: Flow<List<WorkspaceItem>> = dao.observeAll().map { entities ->
        entities.map(WorkspaceEntity::toDomain)
    }

    suspend fun ensureSeeded() {
        if (dao.count() == 0) {
            dao.upsertAll(defaultWorkspaces().map(WorkspaceItem::toEntity))
        }
    }

    suspend fun getWorkspace(id: String): WorkspaceItem? = dao.getById(id)?.toDomain()

    suspend fun createWorkspace(name: String, targetApp: String): WorkspaceItem {
        val newItem = WorkspaceItem(
            "custom_${System.currentTimeMillis()}",
            name,
            "Custom layout for $targetApp actions",
            targetApp,
            12,
            "category",
            false,
            "Just now",
            ControlModule.values().toList()
        )
        dao.upsert(newItem.toEntity())
        return newItem
    }

    suspend fun importWorkspace(importedName: String, appName: String): WorkspaceItem {
        val newItem = WorkspaceItem(
            "imported_${System.currentTimeMillis()}",
            importedName,
            "Imported layout tailored to $appName shortcuts",
            appName,
            18,
            "folder_special",
            true,
            "Just imported",
            ControlModule.values().toList()
        )
        dao.upsert(newItem.toEntity())
        return newItem
    }

    suspend fun saveWorkspace(workspace: WorkspaceItem) {
        dao.upsert(workspace.toEntity())
    }

    suspend fun deleteWorkspace(id: String) {
        dao.deleteById(id)
    }

    suspend fun duplicateWorkspace(id: String, newName: String): WorkspaceItem? {
        val original = getWorkspace(id) ?: return null
        val duplicated = original.copy(
            id = "duplicated_${System.currentTimeMillis()}",
            name = newName,
            description = "Duplicate of ${original.name}",
            isFavorite = false,
            lastUsed = "Just duplicated"
        )
        dao.upsert(duplicated.toEntity())
        return duplicated
    }

    suspend fun toggleFavorite(id: String) {
        val workspace = getWorkspace(id) ?: return
        dao.upsert(workspace.copy(isFavorite = !workspace.isFavorite).toEntity())
    }
}
