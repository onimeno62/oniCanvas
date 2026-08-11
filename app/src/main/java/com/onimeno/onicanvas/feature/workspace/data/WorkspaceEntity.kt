package com.onimeno.onicanvas.feature.workspace.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workspaces")
data class WorkspaceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val targetApp: String,
    val buttonCount: Int,
    val iconName: String,
    val isFavorite: Boolean,
    val lastUsed: String,
    val enabledModules: String,
    val gridSize: Int = 3,
    val macroPagesJson: String = "[]"
)
