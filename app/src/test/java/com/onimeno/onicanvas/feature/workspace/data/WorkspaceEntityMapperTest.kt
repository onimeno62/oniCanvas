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

    @Test
    fun legacyEntityWithRawCreativeControlsConfigJsonMapsConfigCorrectly() {
        val legacyEntity = WorkspaceEntity(
            id = "legacy_raw_1",
            name = "Legacy Raw",
            description = "Legacy JSON without Phase 7 payload",
            targetApp = "Krita",
            buttonCount = 9,
            iconName = "layers",
            isFavorite = false,
            lastUsed = "2026-08-11",
            enabledModules = "[\"MACRO_PAD\"]",
            gridSize = 3,
            macroPagesJson = "[]",
            creativeControlsJson = "{\"panSensitivity\":2.5,\"zoomSensitivity\":1.5,\"invertPanY\":true}"
        )

        val domain = legacyEntity.toDomain()

        assertEquals("legacy_raw_1", domain.id)
        assertEquals(2.5f, domain.creativeControlsConfig.panSensitivity, 0.001f)
        assertEquals(1.5f, domain.creativeControlsConfig.zoomSensitivity, 0.001f)
        assertEquals(true, domain.creativeControlsConfig.invertPanY)
        assertEquals("default", domain.customization.themeKey)
    }

    @Test
    fun roundTripThemeAndIconCustomization() {
        val originalDomain = WorkspaceItem(
            id = "ws_custom_1",
            name = "Customized Workspace",
            description = "Theme and Icon test",
            targetApp = "Blender",
            buttonCount = 16,
            iconName = "palette",
            lastUsed = "2026-08-11",
            customization = com.onimeno.onicanvas.feature.workspace.state.WorkspaceCustomization(
                themeKey = "midnight"
            )
        )

        val entity = originalDomain.toEntity()
        val mapped = entity.toDomain()

        assertEquals("ws_custom_1", mapped.id)
        assertEquals("palette", mapped.iconName)
        assertEquals("midnight", mapped.customization.themeKey)
    }
}
