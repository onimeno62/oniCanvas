package com.onimeno.onicanvas.feature.workspace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onimeno.onicanvas.feature.workspace.data.WorkspaceRepository
import com.onimeno.onicanvas.feature.workspace.state.ControlModule
import com.onimeno.onicanvas.feature.workspace.state.MacroAction
import com.onimeno.onicanvas.feature.workspace.state.MacroButton
import com.onimeno.onicanvas.feature.workspace.state.MacroPage
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceCustomization
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceEditorUiState
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkspaceEditorViewModel(private val repository: WorkspaceRepository) : ViewModel() {
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

    fun toggleModule(module: ControlModule) = executeCommand(ToggleModuleCommand(module))
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

    fun setWorkspaceIcon(iconName: String) {
        val state = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        if (iconName.isBlank() || iconName == state.editingWorkspace.iconName) return
        executeCommand(UpdateWorkspaceCommand { it.copy(iconName = iconName) })
    }

    fun setWorkspaceTheme(themeKey: String) {
        val state = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        if (themeKey == state.editingWorkspace.customization.themeKey) return
        executeCommand(UpdateWorkspaceCommand { it.copy(customization = it.customization.copy(themeKey = themeKey)) })
    }

    fun setWorkspaceAccentColor(colorHex: String?) {
        val state = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        if (colorHex == state.editingWorkspace.customization.accentColorHex) return
        executeCommand(UpdateWorkspaceCommand { it.copy(customization = it.customization.copy(accentColorHex = colorHex)) })
    }

    fun setCustomization(customization: WorkspaceCustomization) {
        val state = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        if (customization == state.editingWorkspace.customization) return
        executeCommand(UpdateWorkspaceCommand { it.copy(customization = customization) })
    }

    fun setGridSize(size: Int) {
        val clamped = size.coerceIn(2, 6)
        val state = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        if (clamped == state.editingWorkspace.gridSize) return
        executeCommand(ResizeGridCommand(clamped))
    }

    fun addButton(label: String, iconName: String, action: MacroAction, colorHex: String? = null) {
        val state = _uiState.value as? WorkspaceEditorUiState.Success ?: return
        val page = state.editingWorkspace.macroPages.minByOrNull(MacroPage::orderIndex) ?: return
        val capacity = state.editingWorkspace.gridSize * state.editingWorkspace.gridSize
        if (page.buttons.size >= capacity) return
        val nextPosition = (page.buttons.maxOfOrNull { it.position } ?: -1) + 1
        val button = MacroButton(
            id = "${page.id}_btn_${System.currentTimeMillis()}", position = nextPosition,
            label = label.trim().ifBlank { "Action ${nextPosition + 1}" }, iconName = iconName,
            action = action, colorHex = colorHex
        )
        executeCommand(UpdateWorkspaceCommand { workspace ->
            workspace.copy(
                macroPages = workspace.macroPages.map { current ->
                    if (current.id != page.id) current else current.copy(buttons = current.buttons + button)
                },
                buttonCount = workspace.macroPages.sumOf { it.buttons.size } + 1
            )
        })
    }

    fun removeButton(pageId: String, buttonId: String) {
        executeCommand(UpdateWorkspaceCommand { workspace ->
            val pages = workspace.macroPages.map { page ->
                if (page.id != pageId) page else page.copy(
                    buttons = page.buttons.filterNot { it.id == buttonId }.mapIndexed { index, button -> button.copy(position = index) }
                )
            }
            workspace.copy(macroPages = pages, buttonCount = pages.sumOf { it.buttons.size })
        })
    }

    fun moveButton(pageId: String, fromPosition: Int, toPosition: Int) {
        if (fromPosition == toPosition) return
        executeCommand(UpdateWorkspaceCommand { workspace ->
            workspace.copy(macroPages = workspace.macroPages.map { page ->
                if (page.id != pageId) page else {
                    val buttons = page.buttons.sortedBy { it.position }.toMutableList()
                    if (fromPosition !in buttons.indices || toPosition !in buttons.indices) page
                    else {
                        val moved = buttons.removeAt(fromPosition)
                        buttons.add(toPosition, moved)
                        page.copy(buttons = buttons.mapIndexed { index, button -> button.copy(position = index) })
                    }
                }
            })
        })
    }

    fun updateButton(pageId: String, buttonId: String, label: String, iconName: String, action: MacroAction, colorHex: String? = null) {
        executeCommand(UpdateWorkspaceCommand { workspace ->
            workspace.copy(macroPages = workspace.macroPages.map { page ->
                if (page.id != pageId) page else page.copy(buttons = page.buttons.map { button ->
                    if (button.id != buttonId) button else button.copy(
                        label = label.trim().ifBlank { button.label },
                        iconName = iconName,
                        action = action,
                        colorHex = colorHex ?: button.colorHex
                    )
                })
            })
        })
    }

    fun updateButton(pageId: String, updatedButton: MacroButton) {
        executeCommand(UpdateWorkspaceCommand { workspace ->
            workspace.copy(macroPages = workspace.macroPages.map { page ->
                if (page.id != pageId) page else page.copy(buttons = page.buttons.map { button ->
                    if (button.id != updatedButton.id) button else updatedButton
                })
            })
        })
    }

    fun setButtonColor(pageId: String, buttonId: String, colorHex: String?) {
        executeCommand(UpdateWorkspaceCommand { workspace ->
            workspace.copy(macroPages = workspace.macroPages.map { page ->
                if (page.id != pageId) page else page.copy(buttons = page.buttons.map { button ->
                    if (button.id != buttonId) button else button.copy(colorHex = colorHex)
                })
            })
        })
    }

    fun updateCreativeControlsConfig(config: com.onimeno.onicanvas.feature.controls.state.CreativeControlsConfig) {
        executeCommand(UpdateWorkspaceCommand { it.copy(creativeControlsConfig = config) })
    }

    fun updateGestureBinding(binding: com.onimeno.onicanvas.feature.controls.state.GestureBinding) {
        executeCommand(UpdateWorkspaceCommand { workspace ->
            val current = workspace.creativeControlsConfig
            val updatedBindings = current.gestureBindings.map { if (it.gestureType == binding.gestureType) binding else it }
            workspace.copy(creativeControlsConfig = current.copy(gestureBindings = updatedBindings))
        })
    }

    fun updateGestureSensitivities(
        zoom: Float? = null,
        pan: Float? = null,
        rotation: Float? = null,
        invertZoom: Boolean? = null,
        invertPanX: Boolean? = null,
        invertPanY: Boolean? = null,
        invertRotation: Boolean? = null,
        hapticsEnabled: Boolean? = null
    ) {
        executeCommand(UpdateWorkspaceCommand { workspace ->
            val current = workspace.creativeControlsConfig
            workspace.copy(
                creativeControlsConfig = current.copy(
                    zoomSensitivity = zoom ?: current.zoomSensitivity,
                    panSensitivity = pan ?: current.panSensitivity,
                    rotationSensitivity = rotation ?: current.rotationSensitivity,
                    invertZoom = invertZoom ?: current.invertZoom,
                    invertPanX = invertPanX ?: current.invertPanX,
                    invertPanY = invertPanY ?: current.invertPanY,
                    invertRotation = invertRotation ?: current.invertRotation,
                    hapticsEnabled = hapticsEnabled ?: current.hapticsEnabled
                )
            )
        })
    }

    fun toggleFavorite() = viewModelScope.launch {
        val state = _uiState.value as? WorkspaceEditorUiState.Success ?: return@launch
        val updated = state.editingWorkspace.copy(isFavorite = !state.editingWorkspace.isFavorite)
        repository.saveWorkspace(updated)
        _uiState.value = state.copy(originalWorkspace = if (!state.isDirty) updated else state.originalWorkspace, editingWorkspace = updated)
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
        repository.saveWorkspace(newWorkspace); undoStack.clear(); redoStack.clear()
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
            undoStack.clear(); redoStack.clear(); _uiState.value = WorkspaceEditorUiState.Success(it, it, false, false, false)
        }
    }

    fun delete(onDeleted: () -> Unit) = viewModelScope.launch {
        val state = _uiState.value as? WorkspaceEditorUiState.Success ?: return@launch
        repository.deleteWorkspace(state.originalWorkspace.id); onDeleted()
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
private class UpdateWorkspaceCommand(private val transform: (WorkspaceItem) -> WorkspaceItem) : EditorCommand {
    private var before: WorkspaceItem? = null
    override fun execute(workspace: WorkspaceItem): WorkspaceItem { before = workspace; return transform(workspace) }
    override fun undo(workspace: WorkspaceItem): WorkspaceItem = before ?: workspace
}
private class ResizeGridCommand(private val newSize: Int) : EditorCommand {
    private var before: WorkspaceItem? = null
    override fun execute(workspace: WorkspaceItem): WorkspaceItem {
        before = workspace
        val capacity = newSize * newSize
        val pages = workspace.macroPages.map { page -> page.copy(buttons = page.buttons.sortedBy { it.position }.take(capacity).mapIndexed { index, button -> button.copy(position = index) }) }
        return workspace.copy(gridSize = newSize, macroPages = pages, buttonCount = pages.sumOf { it.buttons.size })
    }
    override fun undo(workspace: WorkspaceItem): WorkspaceItem = before ?: workspace
}
