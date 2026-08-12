package com.onimeno.onicanvas.feature.workspace

import com.onimeno.onicanvas.feature.workspace.data.WorkspaceEntity
import com.onimeno.onicanvas.feature.workspace.data.createDefaultPages
import com.onimeno.onicanvas.feature.workspace.data.toDomain
import com.onimeno.onicanvas.feature.workspace.data.toEntity
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceCustomization
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceIconLibrary
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceItem
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkspaceCustomizationTest {
    @Test
    fun customization_roundTripsThroughWorkspaceEntity() {
        val original = WorkspaceItem(
            id = "custom_test",
            name = "Test Workspace",
            description = "test",
            targetApp = "Clip Studio Paint",
            buttonCount = 9,
            iconName = "brush",
            lastUsed = "now",
            customization = WorkspaceCustomization(WorkspaceTheme.VIOLET.key)
        )

        val restored = original.toEntity().toDomain()

        assertEquals(WorkspaceTheme.VIOLET.key, restored.customization.themeKey)
        assertEquals(original.iconName, restored.iconName)
    }

    @Test
    fun legacy_workspace_entity_defaults_to_default_customization() {
        val entity = WorkspaceEntity(
            id = "legacy",
            name = "Legacy",
            description = "legacy",
            targetApp = "Clip Studio Paint",
            buttonCount = 9,
            iconName = "category",
            isFavorite = false,
            lastUsed = "old",
            enabledModules = "[]",
            gridSize = 3,
            macroPagesJson = "[]",
            creativeControlsJson = "{}"
        )

        assertEquals(WorkspaceTheme.DEFAULT.key, entity.toDomain().customization.themeKey)
    }

    @Test
    fun default_pages_never_exceed_grid_capacity() {
        val pages = createDefaultPages("test", 4)
        assertEquals(1, pages.size)
        assertTrue(pages.single().buttons.size <= 16)
        assertEquals(16, pages.single().buttons.last().position + 1)
    }

    @Test
    fun icon_library_contains_core_workspace_icons() {
        assertTrue(WorkspaceIconLibrary.names.contains("brush"))
        assertTrue(WorkspaceIconLibrary.names.contains("palette"))
        assertTrue(WorkspaceIconLibrary.names.contains("layers"))
    }
}
