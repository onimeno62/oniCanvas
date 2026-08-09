package com.onimeno.onicanvas.feature.workspace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onimeno.onicanvas.feature.workspace.data.WorkspaceRepository
import com.onimeno.onicanvas.feature.workspace.state.ControlModule
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceEditorUiState
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkspaceEditorViewModel(
    private val repository: WorkspaceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkspaceEditorUiState>(WorkspaceEditorUiState.Loading)
    val uiState: StateFlow<WorkspaceEditorUiState> = _uiState.asStateFlow()
    private val undoStack = mutableListOf<EditorCommand>()
    private val redoStack = mutableListOf<EditorCommand>()

    fun loadWorkspace(id: String) = viewModelScope.launch {
        _uiState.value = WorkspaceEditorUiState.Loading
        val workspace = repository.getWorkspace(id)
        if (workspace != null) {
            undoStack.clear(); redoStack.clear()
            _uiState.value = WorkspaceEditorUiState.Success(workspace, workspace, false, false, false)
        } else _uiState.value = WorkspaceEditorUiState.Error("Workspace not found")
    }

    private fun executeCommand(command: EditorCommand) {
        val state = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        val editing = command.execute(state.editingWorkspace)
        undoStack.add(command); redoStack.clear()
        _uiState.value = state.copy(editingWorkspace = editing, isDirty = editing != state.originalWorkspace, canUndo = true, canRedo = false)
    }

    fun undo() {
        val state = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        if (undoStack.isEmpty()) return
        val command = undoStack.removeAt(undoStack.lastIndex); redoStack.add(command)
        val editing = command.undo(state.editingWorkspace)
        _uiState.value = state.copy(editingWorkspace = editing, isDirty = editing != state.originalWorkspace, canUndo = undoStack.isNotEmpty(), canRedo = true)
    }

    fun redo() {
        val state = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        if (redoStack.isEmpty()) return
        val command = redoStack.removeAt(redoStack.lastIndex); undoStack.add(command)
        val editing = command.execute(state.editingWorkspace)
        _uiState.value = state.copy(editingWorkspace = editing, isDirty = editing != state.originalWorkspace, canUndo = true, canRedo = redoStack.isNotEmpty())
    }

    fun toggleModule(module: ControlModule) { executeCommand(ToggleModuleCommand(module)) }
    fun moveModuleUp(index: Int) { if (index > 0) executeCommand(MoveModuleCommand(index, index - 1)) }
    fun moveModuleDown(index: Int) {
        val state = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        if (index < state.editingWorkspace.enabledModules.lastIndex) executeCommand(MoveModuleCommand(index, index + 1))
    }

    fun updateDetails(name: String, targetApp: String, description: String) {
        val state = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        val editing = state.editingWorkspace
        executeCommand(UpdateDetailsCommand(editing.name, name, editing.targetApp, targetApp, editing.description, description))
    }

    fun toggleFavorite() = viewModelScope.launch {
        val state = _uiState.value as? WorkspaceEditorUiState.Success ?: return@launch
        val updated = state.editingWorkspace.copy(isFavorite = !state.editingWorkspace.isFavorite)
        repository.saveWorkspace(updated)
        _uiState.value = state.copy(
            originalWorkspace = if (!state.isDirty) updated else state.originalWorkspace,
            editingWorkspace = updated
        )
    }

    fun save() = viewModelScope.launch {
        val state = _uiState.value as? WorkspaceEditorUiState.Success ?: return@launch
        val saved = state.editingWorkspace.copy(lastUsed = "Just edited")
        repository.saveWorkspace(saved); undoStack.clear(); redoStack.clear()
        _uiState.value = state.copy(originalWorkspace = saved, editingWorkspace = saved, isDirty = false, canUndo = false, canRedo = false)
    }

    fun saveAs(newName: String) = viewModelScope.launch {
        val state = _uiState.value as? WorkspaceEditorUiState.Success ?: return@launch
        val newWorkspace = state.editingWorkspace.copy(id = "custom_${System.currentTimeMillis()}", name = newName, isFavorite = false, lastUsed = "Just created")
        repository.saveWorkspace(newWorkspace)
        undoStack.clear(); redoStack.clear()
        _uiState.value = WorkspaceEditorUiState.Success(newWorkspace, newWorkspace, false, false, false)
    }

    fun revert() {
        val state = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        undoStack.clear(); redoStack.clear()
        _uiState.value = state.copy(editingWorkspace = state.originalWorkspace, isDirty = false, canUndo = false, canRedo = false)
    }

    fun duplicate() = viewModelScope.launch {
        val state = _uiState.value as? WorkspaceEditorUiState.Success ?: return@launch
        repository.duplicateWorkspace(state.originalWorkspace.id, "${state.originalWorkspace.name} (Copy)")?.let {
            undoStack.clear(); redoStack.clear()
            _uiState.value = WorkspaceEditorUiState.Success(it, it, false, false, false)
        }
    }

    fun delete(onDeleted: () -> Unit) = viewModelScope.launch {
        val state = _uiState.value as? WorkspaceEditorUiState.Success ?: return@launch
        repository.deleteWorkspace(state.originalWorkspace.id)
        onDeleted()
    }

    fun showRenameDialog(show: Boolean) { updateDialogState { it.copy(showRenameDialog = show) } }
    fun showSaveAsDialog(show: Boolean) { updateDialogState { it.copy(showSaveAsDialog = show) } }
    fun showDeleteConfirmDialog(show: Boolean) { updateDialogState { it.copy(showDeleteConfirmDialog = show) } }
    private fun updateDialogState(transform: (WorkspaceEditorUiState.Success) -> WorkspaceEditorUiState.Success) { (_uiState.value as? WorkspaceEditorUiState.Success)?.let { _uiState.value = transform(it) } }
}

private interface EditorCommand { fun execute(workspace: WorkspaceItem): WorkspaceItem; fun undo(workspace: WorkspaceItem): WorkspaceItem }
private class ToggleModuleCommand(private val module: ControlModule) : EditorCommand {
    override fun execute(workspace: WorkspaceItem): WorkspaceItem { val list = workspace.enabledModules.toMutableList(); if (module in list) list.remove(module) else list.add(module); return workspace.copy(enabledModules = list, buttonCount = list.size * 4) }
    override fun undo(workspace: WorkspaceItem) = execute(workspace)
}
private class MoveModuleCommand(private val from: Int, private val to: Int) : EditorCommand {
    override fun execute(workspace: WorkspaceItem): WorkspaceItem { val list = workspace.enabledModules.toMutableList(); if (from in list.indices && to in list.indices) list.add(to, list.removeAt(from)); return workspace.copy(enabledModules = list) }
    override fun undo(workspace: WorkspaceItem): WorkspaceItem { val list = workspace.enabledModules.toMutableList(); if (to in list.indices && from in list.indices) list.add(from, list.removeAt(to)); return workspace.copy(enabledModules = list) }
}
private class UpdateDetailsCommand(private val oldName: String, private val newName: String, private val oldApp: String, private val newApp: String, private val oldDesc: String, private val newDesc: String) : EditorCommand {
    override fun execute(workspace: WorkspaceItem) = workspace.copy(name = newName, targetApp = newApp, description = newDesc)
    override fun undo(workspace: WorkspaceItem) = workspace.copy(name = oldName, targetApp = oldApp, description = oldDesc)
}
