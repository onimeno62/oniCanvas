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

class WorkspaceEditorViewModel : ViewModel() {

    private val repository = WorkspaceRepository.getInstance()

    private val _uiState = MutableStateFlow<WorkspaceEditorUiState>(WorkspaceEditorUiState.Loading)
    val uiState: StateFlow<WorkspaceEditorUiState> = _uiState.asStateFlow()

    private val undoStack = mutableListOf<EditorCommand>()
    private val redoStack = mutableListOf<EditorCommand>()

    fun loadWorkspace(id: String) {
        viewModelScope.launch {
            _uiState.value = WorkspaceEditorUiState.Loading
            val workspace = repository.getWorkspace(id)
            if (workspace != null) {
                undoStack.clear()
                redoStack.clear()
                _uiState.value = WorkspaceEditorUiState.Success(
                    originalWorkspace = workspace,
                    editingWorkspace = workspace,
                    isDirty = false,
                    canUndo = false,
                    canRedo = false
                )
            } else {
                _uiState.value = WorkspaceEditorUiState.Error("Workspace not found")
            }
        }
    }

    private fun executeCommand(command: EditorCommand) {
        val currentState = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        val newEditing = command.execute(currentState.editingWorkspace)
        undoStack.add(command)
        redoStack.clear()
        _uiState.value = currentState.copy(
            editingWorkspace = newEditing,
            isDirty = newEditing != currentState.originalWorkspace,
            canUndo = undoStack.isNotEmpty(),
            canRedo = redoStack.isNotEmpty()
        )
    }

    fun undo() {
        val currentState = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        if (undoStack.isEmpty()) return
        val command = undoStack.removeAt(undoStack.lastIndex)
        val newEditing = command.undo(currentState.editingWorkspace)
        redoStack.add(command)
        _uiState.value = currentState.copy(
            editingWorkspace = newEditing,
            isDirty = newEditing != currentState.originalWorkspace,
            canUndo = undoStack.isNotEmpty(),
            canRedo = redoStack.isNotEmpty()
        )
    }

    fun redo() {
        val currentState = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        if (redoStack.isEmpty()) return
        val command = redoStack.removeAt(redoStack.lastIndex)
        val newEditing = command.execute(currentState.editingWorkspace)
        undoStack.add(command)
        _uiState.value = currentState.copy(
            editingWorkspace = newEditing,
            isDirty = newEditing != currentState.originalWorkspace,
            canUndo = undoStack.isNotEmpty(),
            canRedo = redoStack.isNotEmpty()
        )
    }

    fun toggleModule(module: ControlModule) {
        executeCommand(ToggleModuleCommand(module))
    }

    fun moveModuleUp(index: Int) {
        if (index > 0) {
            executeCommand(MoveModuleCommand(index, index - 1))
        }
    }

    fun moveModuleDown(index: Int) {
        val currentState = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        if (index < currentState.editingWorkspace.enabledModules.lastIndex) {
            executeCommand(MoveModuleCommand(index, index + 1))
        }
    }

    fun updateDetails(name: String, targetApp: String, description: String) {
        val currentState = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        val editing = currentState.editingWorkspace
        executeCommand(
            UpdateDetailsCommand(
                oldName = editing.name, newName = name,
                oldApp = editing.targetApp, newApp = targetApp,
                oldDesc = editing.description, newDesc = description
            )
        )
    }

    fun toggleFavorite() {
        val currentState = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        val editing = currentState.editingWorkspace
        val updated = editing.copy(isFavorite = !editing.isFavorite)
        repository.saveWorkspace(updated)
        _uiState.value = currentState.copy(
            originalWorkspace = if (!currentState.isDirty) updated else currentState.originalWorkspace,
            editingWorkspace = updated
        )
    }

    fun save() {
        val currentState = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        val savedWorkspace = currentState.editingWorkspace.copy(lastUsed = "Just edited")
        repository.saveWorkspace(savedWorkspace)
        undoStack.clear()
        redoStack.clear()
        _uiState.value = currentState.copy(
            originalWorkspace = savedWorkspace,
            editingWorkspace = savedWorkspace,
            isDirty = false,
            canUndo = false,
            canRedo = false
        )
    }

    fun saveAs(newName: String) {
        val currentState = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        val newId = "custom_${System.currentTimeMillis()}"
        val newWorkspace = currentState.editingWorkspace.copy(
            id = newId,
            name = newName,
            isFavorite = false,
            lastUsed = "Just created"
        )
        repository.saveWorkspace(newWorkspace)
        loadWorkspace(newId)
    }

    fun revert() {
        val currentState = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        undoStack.clear()
        redoStack.clear()
        _uiState.value = currentState.copy(
            editingWorkspace = currentState.originalWorkspace,
            isDirty = false,
            canUndo = false,
            canRedo = false
        )
    }

    fun duplicate() {
        val currentState = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        val originalId = currentState.originalWorkspace.id
        val duplicated = repository.duplicateWorkspace(originalId, "${currentState.originalWorkspace.name} (Copy)")
        if (duplicated != null) {
            loadWorkspace(duplicated.id)
        }
    }

    fun delete(onDeleted: () -> Unit) {
        val currentState = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        repository.deleteWorkspace(currentState.originalWorkspace.id)
        onDeleted()
    }

    fun showRenameDialog(show: Boolean) {
        val currentState = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        _uiState.value = currentState.copy(showRenameDialog = show)
    }

    fun showSaveAsDialog(show: Boolean) {
        val currentState = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        _uiState.value = currentState.copy(showSaveAsDialog = show)
    }

    fun showDeleteConfirmDialog(show: Boolean) {
        val currentState = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        _uiState.value = currentState.copy(showDeleteConfirmDialog = show)
    }
}

private interface EditorCommand {
    fun execute(workspace: WorkspaceItem): WorkspaceItem
    fun undo(workspace: WorkspaceItem): WorkspaceItem
}

private class ToggleModuleCommand(private val module: ControlModule) : EditorCommand {
    override fun execute(workspace: WorkspaceItem): WorkspaceItem {
        val current = workspace.enabledModules.toMutableList()
        if (current.contains(module)) {
            current.remove(module)
        } else {
            current.add(module)
        }
        return workspace.copy(
            enabledModules = current,
            buttonCount = current.size * 4
        )
    }

    override fun undo(workspace: WorkspaceItem): WorkspaceItem {
        return execute(workspace)
    }
}

private class MoveModuleCommand(private val fromIndex: Int, private val toIndex: Int) : EditorCommand {
    override fun execute(workspace: WorkspaceItem): WorkspaceItem {
        val current = workspace.enabledModules.toMutableList()
        if (fromIndex in current.indices && toIndex in current.indices) {
            val item = current.removeAt(fromIndex)
            current.add(toIndex, item)
        }
        return workspace.copy(enabledModules = current)
    }

    override fun undo(workspace: WorkspaceItem): WorkspaceItem {
        val current = workspace.enabledModules.toMutableList()
        if (toIndex in current.indices && fromIndex in current.indices) {
            val item = current.removeAt(toIndex)
            current.add(fromIndex, item)
        }
        return workspace.copy(enabledModules = current)
    }
}

private class UpdateDetailsCommand(
    private val oldName: String, private val newName: String,
    private val oldApp: String, private val newApp: String,
    private val oldDesc: String, private val newDesc: String
) : EditorCommand {
    override fun execute(workspace: WorkspaceItem): WorkspaceItem {
        return workspace.copy(name = newName, targetApp = newApp, description = newDesc)
    }

    override fun undo(workspace: WorkspaceItem): WorkspaceItem {
        return workspace.copy(name = oldName, targetApp = oldApp, description = oldDesc)
    }
}
