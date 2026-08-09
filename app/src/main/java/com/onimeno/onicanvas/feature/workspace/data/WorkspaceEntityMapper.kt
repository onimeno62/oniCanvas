package com.onimeno.onicanvas.feature.workspace.data

import com.onimeno.onicanvas.feature.workspace.state.ControlModule
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceItem
import kotlinx.serialization.json.Json

private val workspaceJson = Json { ignoreUnknownKeys = true }

fun WorkspaceEntity.toDomain(): WorkspaceItem = WorkspaceItem(
    id = id,
    name = name,
    description = description,
    targetApp = targetApp,
    buttonCount = buttonCount,
    iconName = iconName,
    isFavorite = isFavorite,
    lastUsed = lastUsed,
    enabledModules = runCatching {
        workspaceJson.decodeFromString<List<String>>(enabledModules)
            .mapNotNull { name -> ControlModule.values().find { it.name == name } }
    }.getOrDefault(emptyList())
)

fun WorkspaceItem.toEntity(): WorkspaceEntity = WorkspaceEntity(
    id = id,
    name = name,
    description = description,
    targetApp = targetApp,
    buttonCount = buttonCount,
    iconName = iconName,
    isFavorite = isFavorite,
    lastUsed = lastUsed,
    enabledModules = workspaceJson.encodeToString(enabledModules.map { it.name })
)
