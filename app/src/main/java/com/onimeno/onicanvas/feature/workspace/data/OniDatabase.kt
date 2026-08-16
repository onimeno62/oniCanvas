package com.onimeno.onicanvas.feature.workspace.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.onimeno.onicanvas.feature.color.data.ColorPaletteDao
import com.onimeno.onicanvas.feature.color.data.ColorPaletteEntity
import com.onimeno.onicanvas.feature.color.data.RecentColorDao
import com.onimeno.onicanvas.feature.color.data.RecentColorEntity
import com.onimeno.onicanvas.feature.profiles.data.ProfileDao
import com.onimeno.onicanvas.feature.profiles.data.ProfileEntity

@Database(
    entities = [
        WorkspaceEntity::class,
        ProfileEntity::class,
        ColorPaletteEntity::class,
        RecentColorEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class OniDatabase : RoomDatabase() {
    abstract fun workspaceDao(): WorkspaceDao
    abstract fun profileDao(): ProfileDao
    abstract fun colorPaletteDao(): ColorPaletteDao
    abstract fun recentColorDao(): RecentColorDao
}

