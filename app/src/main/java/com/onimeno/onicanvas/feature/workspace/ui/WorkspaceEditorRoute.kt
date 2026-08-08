package com.onimeno.onicanvas.feature.workspace.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.onimeno.onicanvas.OniCanvasApp
import com.onimeno.onicanvas.feature.workspace.viewmodel.WorkspaceEditorViewModel
import com.onimeno.onicanvas.feature.workspace.viewmodel.WorkspaceEditorViewModelFactory

@Composable
fun WorkspaceEditorRoute(workspaceId: String, onBackClick: () -> Unit) {
    val app = LocalContext.current.applicationContext as OniCanvasApp
    val viewModel: WorkspaceEditorViewModel = viewModel(
        factory = WorkspaceEditorViewModelFactory(app.container.workspaceRepository)
    )
    WorkspaceEditorScreen(workspaceId = workspaceId, onBackClick = onBackClick, viewModel = viewModel)
}
