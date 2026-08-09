package com.onimeno.onicanvas

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.onimeno.onicanvas.feature.settings.data.SettingsRepository
import com.onimeno.onicanvas.feature.workspace.data.OniDatabase
import com.onimeno.onicanvas.feature.workspace.data.WorkspaceRepository

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "oni_settings")

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

    val settingsRepository: SettingsRepository = SettingsRepository(
        applicationContext.dataStore
    )
}
