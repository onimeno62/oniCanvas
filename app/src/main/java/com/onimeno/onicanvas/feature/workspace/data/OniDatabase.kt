package com.onimeno.onicanvas.feature.workspace.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [WorkspaceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class OniDatabase : RoomDatabase() {
    abstract fun workspaceDao(): WorkspaceDao
}
