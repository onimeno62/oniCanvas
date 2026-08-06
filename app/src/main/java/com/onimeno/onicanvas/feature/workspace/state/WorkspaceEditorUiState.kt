package com.onimeno.onicanvas.feature.workspace.state

sealed interface WorkspaceEditorUiState {
    object Loading : WorkspaceEditorUiState
    data class Success(
        val originalWorkspace: WorkspaceItem,
        val editingWorkspace: WorkspaceItem,
        val isDirty: Boolean = false,
        val canUndo: Boolean = false,
        val canRedo: Boolean = false,
        val showSaveAsDialog: Boolean = false,
        val showRenameDialog: Boolean = false,
        val showDeleteConfirmDialog: Boolean = false
    ) : WorkspaceEditorUiState
    data class Error(val message: String) : WorkspaceEditorUiState
}
