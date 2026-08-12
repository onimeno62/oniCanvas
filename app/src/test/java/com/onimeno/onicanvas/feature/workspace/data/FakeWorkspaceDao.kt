package com.onimeno.onicanvas.feature.workspace.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeWorkspaceDao : WorkspaceDao {
    private val storage = mutableMapOf<String, WorkspaceEntity>()
    private val flow = MutableStateFlow<List<WorkspaceEntity>>(emptyList())

    private fun notifyFlow() {
        flow.value = storage.values.toList()
    }

    override fun observeAll(): Flow<List<WorkspaceEntity>> = flow

    override suspend fun getById(id: String): WorkspaceEntity? = storage[id]

    override suspend fun upsert(workspace: WorkspaceEntity) {
        storage[workspace.id] = workspace
        notifyFlow()
    }

    override suspend fun upsertAll(workspaces: List<WorkspaceEntity>) {
        workspaces.forEach { storage[it.id] = it }
        notifyFlow()
    }

    override suspend fun delete(workspace: WorkspaceEntity) {
        storage.remove(workspace.id)
        notifyFlow()
    }

    override suspend fun deleteById(id: String) {
        storage.remove(id)
        notifyFlow()
    }

    override suspend fun count(): Int = storage.size
}
