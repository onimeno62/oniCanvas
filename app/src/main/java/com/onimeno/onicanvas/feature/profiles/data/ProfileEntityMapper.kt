package com.onimeno.onicanvas.feature.profiles.data

import com.onimeno.onicanvas.feature.profiles.state.AppProfile

fun ProfileEntity.toDomain(): AppProfile = AppProfile(
    id = id,
    name = name,
    targetApp = targetApp,
    description = description,
    layoutCount = layoutCount,
    isActive = isActive,
    isDefault = isDefault
)

fun AppProfile.toEntity(): ProfileEntity = ProfileEntity(
    id = id,
    name = name,
    targetApp = targetApp,
    description = description,
    layoutCount = layoutCount,
    isActive = isActive,
    isDefault = isDefault
)
