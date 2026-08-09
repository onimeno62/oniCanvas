package com.onimeno.onicanvas.feature.profiles.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val targetApp: String,
    val description: String,
    val layoutCount: Int,
    val isActive: Boolean,
    val isDefault: Boolean
)
