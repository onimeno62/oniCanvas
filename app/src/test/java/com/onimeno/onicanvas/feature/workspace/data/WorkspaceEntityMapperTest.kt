package com.onimeno.onicanvas.feature.workspace.data

import com.onimeno.onicanvas.feature.controls.state.CreativeControlsConfig
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class WorkspaceEntityMapperTest {

    @Test
    fun legacyEntityWithEmptyCreativeControlsJsonMapsToDefaultConfig() {
        val legacyEntity = WorkspaceEntity(
            id = "ws_test_1",
            name = "Photoshop Workspace",
            description = "Photoshop controls",
            targetApp = "Photoshop",
            buttonCount = 9,
            iconName = "brush",
            isFavorite = true,
            lastUsed = "2026-08-11",
            enabledModules = "[\"MACRO_PAD\"]",
            gridSize = 3,
            macroPagesJson = "[]",
            creativeControlsJson = "{}"
        )

        val domain = legacyEntity.toDomain()

        assertEquals("ws_test_1", domain.id)
        assertNotNull(domain.creativeControlsConfig)
        assertEquals(1.0f, domain.creativeControlsConfig.panSensitivity, 0.001f)
        assertEquals(6, domain.creativeControlsConfig.gestureBindings.size)
    }

    @Test
    fun roundTripWorkspaceEntityAndDomainMapping() {
        val originalDomain = WorkspaceItem(
            id = "ws_test_2",
            name = "Illustrator Workspace",
            description = "Vector editing",
            targetApp = "Illustrator",
            buttonCount = 12,
            iconName = "palette",
            isFavorite = false,
            lastUsed = "2026-08-11",
            enabledModules = emptyList(),
            gridSize = 4,
            macroPages = emptyList(),
            creativeControlsConfig = CreativeControlsConfig(
                panSensitivity = 1.8f,
                zoomSensitivity = 2.2f,
                invertPanX = true
            )
        )

        val entity = originalDomain.toEntity()
        val mappedDomain = entity.toDomain()

        assertEquals(originalDomain.id, mappedDomain.id)
        assertEquals(1.8f, mappedDomain.creativeControlsConfig.panSensitivity, 0.001f)
        assertEquals(2.2f, mappedDomain.creativeControlsConfig.zoomSensitivity, 0.001f)
        assertEquals(true, mappedDomain.creativeControlsConfig.invertPanX)
    }
}
