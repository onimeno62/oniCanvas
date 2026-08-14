package com.onimeno.onicanvas.feature.workspace

import com.onimeno.onicanvas.feature.controls.state.CreativeControlsConfig
import com.onimeno.onicanvas.feature.controls.state.GestureAction
import com.onimeno.onicanvas.feature.controls.state.GestureBinding
import com.onimeno.onicanvas.feature.controls.state.GestureType
import com.onimeno.onicanvas.feature.workspace.data.WorkspaceEntity
import com.onimeno.onicanvas.feature.workspace.data.createDefaultPages
import com.onimeno.onicanvas.feature.workspace.data.toDomain
import com.onimeno.onicanvas.feature.workspace.data.toEntity
import com.onimeno.onicanvas.feature.workspace.state.MacroButton
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceColorPalette
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceCustomization
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceIconLibrary
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceItem
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
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
            customization = WorkspaceCustomization(
                themeKey = WorkspaceTheme.VIOLET.key,
                accentColorHex = "#8B5CF6",
                enableHaptics = true,
                enableAnimations = true
            )
        )

        val restored = original.toEntity().toDomain()

        assertEquals(WorkspaceTheme.VIOLET.key, restored.customization.themeKey)
        assertEquals("#8B5CF6", restored.customization.accentColorHex)
        assertTrue(restored.customization.enableHaptics)
        assertTrue(restored.customization.enableAnimations)
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

        val domain = entity.toDomain()
        assertEquals(WorkspaceTheme.DEFAULT.key, domain.customization.themeKey)
        assertFalse(domain.customization.themeKey.isBlank())
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
        assertTrue(WorkspaceIconLibrary.availableIcons.contains("crop"))
        assertTrue(WorkspaceIconLibrary.availableIcons.contains("ruler"))
        assertTrue(WorkspaceIconLibrary.availableIcons.contains("eyedropper"))
    }

    @Test
    fun icon_library_resolves_icons_reliably_with_fallback() {
        val brushIcon = WorkspaceIconLibrary.getIcon("brush")
        assertNotNull(brushIcon)

        val unknownIcon = WorkspaceIconLibrary.getIcon("non_existent_icon_xyz")
        assertNotNull(unknownIcon)
    }

    @Test
    fun color_palette_presets_are_valid_and_non_empty() {
        assertTrue(WorkspaceColorPalette.presetColors.isNotEmpty())
        assertTrue(WorkspaceColorPalette.themeAccentColors.isNotEmpty())
        assertTrue(WorkspaceColorPalette.presetColors.contains("#3B82F6"))
        assertTrue(WorkspaceColorPalette.presetColors.contains("#10B981"))
        assertTrue(WorkspaceColorPalette.presetColors.contains("#EF4444"))
    }

    @Test
    fun workspace_themes_have_valid_keys_and_hex_values() {
        val allThemes = WorkspaceTheme.values()
        assertTrue(allThemes.size >= 8)

        allThemes.forEach { theme ->
            assertTrue(theme.key.isNotBlank())
            assertTrue(theme.displayName.isNotBlank())
            assertTrue(theme.accentHex.startsWith("#"))
            assertTrue(theme.surfaceHex.startsWith("#"))
        }

        assertEquals(WorkspaceTheme.OCEAN, WorkspaceTheme.fromKey(WorkspaceTheme.OCEAN.key))
        assertEquals(WorkspaceTheme.DEFAULT, WorkspaceTheme.fromKey("unknown_theme_key"))
    }

    @Test
    fun button_color_serialization_preserves_custom_hex() {
        val buttonWithColor = MacroButton(
            id = "btn_color",
            position = 0,
            label = "Brush Red",
            iconName = "brush",
            action = com.onimeno.onicanvas.feature.workspace.state.MacroAction.Brush,
            colorHex = "#EF4444"
        )
        assertEquals("#EF4444", buttonWithColor.colorHex)
    }

    @Test
    fun creative_controls_config_persists_with_custom_sensitivities_and_bindings() {
        val customConfig = CreativeControlsConfig(
            zoomSensitivity = 1.8f,
            panSensitivity = 0.5f,
            rotationSensitivity = 1.2f,
            invertZoom = true,
            invertPanX = false,
            invertPanY = true,
            hapticsEnabled = false,
            gestureBindings = listOf(
                GestureBinding(
                    gestureType = GestureType.PINCH_ZOOM,
                    action = GestureAction.ZOOM,
                    enabled = true,
                    sensitivity = 2.0f
                ),
                GestureBinding(
                    gestureType = GestureType.TWO_FINGER_PAN,
                    action = GestureAction.PAN,
                    enabled = true,
                    sensitivity = 0.8f
                )
            )
        )

        val workspace = WorkspaceItem(
            id = "ws_custom_gestures",
            name = "Gesture Studio",
            description = "Custom gesture settings",
            targetApp = "Blender",
            buttonCount = 4,
            iconName = "gesture",
            lastUsed = "now",
            creativeControlsConfig = customConfig
        )

        val restored = workspace.toEntity().toDomain()
        assertEquals(1.8f, restored.creativeControlsConfig.zoomSensitivity, 0.01f)
        assertEquals(0.5f, restored.creativeControlsConfig.panSensitivity, 0.01f)
        assertTrue(restored.creativeControlsConfig.invertZoom)
        assertTrue(restored.creativeControlsConfig.invertPanY)
        assertFalse(restored.creativeControlsConfig.hapticsEnabled)
        val pinchBinding = restored.creativeControlsConfig.gestureBindings.firstOrNull { it.gestureType == GestureType.PINCH_ZOOM }
        assertEquals(GestureAction.ZOOM, pinchBinding?.action)
        assertEquals(2.0f, pinchBinding?.sensitivity ?: 0f, 0.01f)
    }
}
