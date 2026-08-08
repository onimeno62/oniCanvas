package com.onimeno.onicanvas.feature.workspace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.onimeno.onicanvas.feature.workspace.data.WorkspaceRepository

class WorkspaceEditorViewModelFactory(
    private val repository: WorkspaceRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(WorkspaceEditorViewModel::class.java))
        return WorkspaceEditorViewModel(repository) as T
    }
}
