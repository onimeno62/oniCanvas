package com.onimeno.onicanvas

import android.content.Context
import androidx.room.Room
import com.onimeno.onicanvas.feature.workspace.data.OniDatabase
import com.onimeno.onicanvas.feature.workspace.data.WorkspaceRepository

class AppContainer(
    context: Context
) {

    val applicationContext = context.applicationContext

    private val workspaceDatabase: OniDatabase = Room.databaseBuilder(
        applicationContext,
        OniDatabase::class.java,
        "oni_canvas.db"
    ).build()

    val workspaceRepository: WorkspaceRepository = WorkspaceRepository(
        workspaceDatabase.workspaceDao()
    )
}
