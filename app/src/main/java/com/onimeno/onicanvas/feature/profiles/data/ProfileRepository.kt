package com.onimeno.onicanvas.feature.profiles.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileRepository(
    private val dao: ProfileDao
) {
    val profiles: Flow<List<com.onimeno.onicanvas.feature.profiles.state.AppProfile>> =
        dao.observeAll().map { profiles -> profiles.map(ProfileEntity::toDomain) }

    suspend fun seedIfEmpty() {
        if (dao.count() == 0) {
            dao.upsertAll(defaultProfiles.map { it.toEntity() })
        }
    }

    suspend fun selectProfile(id: String) {
        dao.setActive(id)
    }

    suspend fun addProfile(name: String, targetApp: String, description: String) {
        dao.upsert(
            com.onimeno.onicanvas.feature.profiles.state.AppProfile(
                id = "custom_${System.currentTimeMillis()}",
                name = name,
                targetApp = targetApp,
                description = description,
                layoutCount = 1,
                isActive = false,
                isDefault = false
            ).toEntity()
        )
    }

    private companion object {
        val defaultProfiles = listOf(
            com.onimeno.onicanvas.feature.profiles.state.AppProfile(
                id = "csp_master",
                name = "CSP Master Layout",
                targetApp = "Clip Studio Paint",
                description = "Optimized for digital ink, sketch layers, and flat coloring tool sets.",
                layoutCount = 4,
                isActive = true,
                isDefault = true
            ),
            com.onimeno.onicanvas.feature.profiles.state.AppProfile(
                id = "photoshop_speed",
                name = "Photoshop SpeedPaint",
                targetApp = "Photoshop",
                description = "Tailored for digital concept art, high opacity brush flow sliders, and fast undo layers.",
                layoutCount = 3
            ),
            com.onimeno.onicanvas.feature.profiles.state.AppProfile(
                id = "krita_animation",
                name = "Krita Animator Layout",
                targetApp = "Krita",
                description = "Custom macro setup mapped to animation frames, onion skin toggles, and drawing tools.",
                layoutCount = 2
            ),
            com.onimeno.onicanvas.feature.profiles.state.AppProfile(
                id = "blender_sculpt",
                name = "Blender Sculpting Companion",
                targetApp = "Blender",
                description = "Camera workspace controls, grab/draw brush modifiers, and custom subdivisions.",
                layoutCount = 5
            )
        )
    }
}
