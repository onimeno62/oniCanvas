package com.onimeno.onicanvas.feature.controls.state

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreativeControlsModelTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun defaultConfigHasDefaultBindings() {
        val config = CreativeControlsConfig()
        assertEquals(6, config.gestureBindings.size)
        assertTrue(config.hapticsEnabled)
        assertEquals(1.0f, config.panSensitivity, 0.001f)
        assertEquals(1.0f, config.zoomSensitivity, 0.001f)
        assertEquals(1.0f, config.rotationSensitivity, 0.001f)
    }

    @Test
    fun roundTripSerializationOfConfig() {
        val original = CreativeControlsConfig(
            panSensitivity = 1.5f,
            zoomSensitivity = 2.0f,
            rotationSensitivity = 0.8f,
            invertPanX = true,
            invertPanY = false,
            invertZoom = true,
            invertRotation = false,
            hapticsEnabled = false
        )

        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<CreativeControlsConfig>(encoded)

        assertEquals(original, decoded)
    }
}
