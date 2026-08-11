package com.onimeno.onicanvas

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.onimeno.onicanvas.feature.connection.data.ConnectionRepository
import com.onimeno.onicanvas.feature.dashboard.data.DashboardRepository
import com.onimeno.onicanvas.feature.dashboard.data.RealDashboardRepository
import com.onimeno.onicanvas.feature.profiles.data.ProfileRepository
import com.onimeno.onicanvas.feature.settings.data.SettingsRepository
import com.onimeno.onicanvas.feature.workspace.data.OniDatabase
import com.onimeno.onicanvas.feature.workspace.data.WorkspaceRepository

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "oni_settings")

private val PROFILE_MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS profiles (
                id TEXT NOT NULL,
                name TEXT NOT NULL,
                targetApp TEXT NOT NULL,
                description TEXT NOT NULL,
                layoutCount INTEGER NOT NULL,
                isActive INTEGER NOT NULL,
                isDefault INTEGER NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )
    }
}

private val WORKSPACE_MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE workspaces ADD COLUMN gridSize INTEGER NOT NULL DEFAULT 3")
        db.execSQL("ALTER TABLE workspaces ADD COLUMN macroPagesJson TEXT NOT NULL DEFAULT '[]'")
    }
}

private val CREATIVE_CONTROLS_MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE workspaces ADD COLUMN creativeControlsJson TEXT NOT NULL DEFAULT '{}'")
    }
}

class AppContainer(
    context: Context
) {

    val applicationContext = context.applicationContext

    private val workspaceDatabase: OniDatabase = Room.databaseBuilder(
        applicationContext,
        OniDatabase::class.java,
        "oni_canvas.db"
    )
        .addMigrations(PROFILE_MIGRATION_1_2, WORKSPACE_MIGRATION_2_3, CREATIVE_CONTROLS_MIGRATION_3_4)
        .build()

    val settingsRepository: SettingsRepository = SettingsRepository(
        applicationContext.dataStore
    )

    val workspaceRepository: WorkspaceRepository = WorkspaceRepository(
        workspaceDatabase.workspaceDao(),
        settingsRepository
    )

    val profileRepository: ProfileRepository = ProfileRepository(
        workspaceDatabase.profileDao()
    )

    val connectionRepository: ConnectionRepository = ConnectionRepository()

    val dashboardRepository: DashboardRepository = RealDashboardRepository(
        connectionRepository = connectionRepository,
        workspaceRepository = workspaceRepository
    )
}
