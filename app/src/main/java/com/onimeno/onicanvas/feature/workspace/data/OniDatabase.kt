package com.onimeno.onicanvas.feature.workspace.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.onimeno.onicanvas.feature.profiles.data.ProfileEntity

@Database(
    entities = [WorkspaceEntity::class, ProfileEntity::class],
    version = 3,
    exportSchema = false
)
abstract class OniDatabase : RoomDatabase() {
    abstract fun workspaceDao(): WorkspaceDao
    abstract fun profileDao(): com.onimeno.onicanvas.feature.profiles.data.ProfileDao
}
