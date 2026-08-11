package com.onimeno.onicanvas.feature.controls.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.onimeno.onicanvas.feature.connection.data.ConnectionRepository
import com.onimeno.onicanvas.feature.workspace.data.WorkspaceRepository

class ControlsViewModelFactory(
    private val workspaceRepository: WorkspaceRepository,
    private val connectionRepository: ConnectionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(ControlsViewModel::class.java)) { "Unknown ViewModel class" }
        return ControlsViewModel(workspaceRepository, connectionRepository) as T
    }
}
