package com.onimeno.onicanvas.feature.workspace.data

import com.onimeno.onicanvas.feature.controls.state.CreativeControlsConfig
import com.onimeno.onicanvas.feature.workspace.state.ControlModule
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceItem
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceCustomization
import com.onimeno.onicanvas.feature.workspace.state.MacroPage
import com.onimeno.onicanvas.feature.workspace.state.MacroButton
import com.onimeno.onicanvas.feature.workspace.state.MacroAction
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

private val workspaceJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

@Serializable
private data class WorkspacePersistencePayload(
    val creativeControls: CreativeControlsConfig = CreativeControlsConfig(),
    val customization: WorkspaceCustomization = WorkspaceCustomization()
)

fun createDefaultPages(workspaceId: String, gridSize: Int): List<MacroPage> {
    val pageId = "${workspaceId}_page_1"
    val buttons = mutableListOf<MacroButton>()
    val maxButtons = gridSize * gridSize
    val actionsList = listOf(
        Pair("Undo", MacroAction.Undo), Pair("Redo", MacroAction.Redo), Pair("Save", MacroAction.Save),
        Pair("Brush", MacroAction.Brush), Pair("Eraser", MacroAction.Eraser), Pair("Fill", MacroAction.Fill),
        Pair("Select", MacroAction.Selection), Pair("Transform", MacroAction.Transform), Pair("Copy", MacroAction.Copy),
        Pair("Paste", MacroAction.Paste)
    )
    for (i in 0 until maxButtons) {
        val actionPair = actionsList.getOrNull(i % actionsList.size) ?: continue
        buttons.add(MacroButton(
            id = "${pageId}_btn_${i}", position = i, label = actionPair.first,
            iconName = actionPair.first.lowercase(), action = actionPair.second,
            longPressAction = if (actionPair.second is MacroAction.Undo) MacroAction.Redo else null,
            repeatEnabled = false, enabled = true, hidden = false
        ))
    }
    return listOf(MacroPage(pageId, "Default Page", 0, buttons))
}

fun WorkspaceEntity.toDomain(): WorkspaceItem {
    val decodedModules = runCatching {
        workspaceJson.decodeFromString<List<String>>(enabledModules)
            .mapNotNull { name -> ControlModule.values().find { it.name == name } }
    }.getOrDefault(emptyList())

    val decodedPages = runCatching { workspaceJson.decodeFromString<List<MacroPage>>(macroPagesJson) }.getOrDefault(emptyList())
    val persistenceJson = runCatching { workspaceJson.parseToJsonElement(creativeControlsJson).jsonObject }.getOrNull()
    val isPhase7Payload = persistenceJson?.containsKey("creativeControls") == true || persistenceJson?.containsKey("customization") == true
    val persistence = if (isPhase7Payload) {
        runCatching { workspaceJson.decodeFromString<WorkspacePersistencePayload>(creativeControlsJson) }.getOrNull()
    } else null
    val creativeControls = (persistence?.creativeControls ?: runCatching {
        workspaceJson.decodeFromString<CreativeControlsConfig>(creativeControlsJson)
    }.getOrDefault(CreativeControlsConfig())).normalized()
    val customization = persistence?.customization ?: WorkspaceCustomization()
    val pages = if (decodedPages.isEmpty()) createDefaultPages(id, gridSize) else decodedPages

    return WorkspaceItem(
        id = id, name = name, description = description, targetApp = targetApp,
        buttonCount = buttonCount, iconName = iconName, isFavorite = isFavorite,
        lastUsed = lastUsed, enabledModules = decodedModules, gridSize = gridSize,
        macroPages = pages, creativeControlsConfig = creativeControls, customization = customization
    )
}

fun WorkspaceItem.toEntity(): WorkspaceEntity = WorkspaceEntity(
    id = id, name = name, description = description, targetApp = targetApp,
    buttonCount = buttonCount, iconName = iconName, isFavorite = isFavorite,
    lastUsed = lastUsed,
    enabledModules = workspaceJson.encodeToString(enabledModules.map { it.name }),
    gridSize = gridSize,
    macroPagesJson = workspaceJson.encodeToString(macroPages),
    creativeControlsJson = workspaceJson.encodeToString(WorkspacePersistencePayload(creativeControlsConfig, customization))
)
