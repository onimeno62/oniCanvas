package com.onimeno.onicanvas

import android.content.Context
import com.onimeno.onicanvas.feature.workspace.data.WorkspaceRepository

class AppContainer(
    context: Context
) {

    val applicationContext = context.applicationContext

    val workspaceRepository: WorkspaceRepository = WorkspaceRepository()

}
