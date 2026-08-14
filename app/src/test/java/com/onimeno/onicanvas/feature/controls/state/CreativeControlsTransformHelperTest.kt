package com.onimeno.onicanvas.feature.controls.state

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class CreativeControlsTransformHelperTest {

    @Test
    fun resolveBinding_returnsMatchingBinding() {
        val config = CreativeControlsConfig()
        val binding = CreativeControlsTransformHelper.resolveBinding(config, GestureType.ONE_FINGER_PAN)
        assertNotNull(binding)
        assertEquals(GestureAction.PAN, binding?.action)
    }

    @Test
    fun resolveBinding_returnsNullWhenDisabled() {
        val config = CreativeControlsConfig(
            gestureBindings = listOf(
                GestureBinding(
                    gestureType = GestureType.ONE_FINGER_PAN,
                    action = GestureAction.PAN,
                    enabled = false
                )
            )
        )
        val binding = CreativeControlsTransformHelper.resolveBinding(config, GestureType.ONE_FINGER_PAN)
        assertNull(binding)
    }

    @Test
    fun calculatePanDelta_appliesSensitivitiesAndInversions() {
        val config = CreativeControlsConfig(
            panSensitivity = 2.0f,
            invertPanX = true,
            invertPanY = false
        )
        val binding = GestureBinding(
            gestureType = GestureType.ONE_FINGER_PAN,
            action = GestureAction.PAN,
            sensitivity = 1.5f,
            isInverted = false,
            enabled = true
        )

        val (dx, dy) = CreativeControlsTransformHelper.calculatePanDelta(10.0, 20.0, config, binding)
        // dx = 10.0 * 2.0 * 1.5 * (-1) = -30.0
        // dy = 20.0 * 2.0 * 1.5 * (1) = 60.0
        assertEquals(-30.0, dx, 0.001)
        assertEquals(60.0, dy, 0.001)
    }

    @Test
    fun calculateZoomAmount_appliesZoomSensitivityAndInversion() {
        val config = CreativeControlsConfig(
            zoomSensitivity = 2.0f,
            invertZoom = false
        )
        val binding = GestureBinding(
            gestureType = GestureType.PINCH_ZOOM,
            action = GestureAction.ZOOM,
            sensitivity = 1.0f,
            isInverted = false,
            enabled = true
        )

        // Raw zoom = 1.1 -> delta = 0.1, effSens = 2.0 -> adjusted = 1.0 + (0.1 * 2.0) = 1.2
        val zoom = CreativeControlsTransformHelper.calculateZoomAmount(1.1, config, binding)
        assertEquals(1.2, zoom, 0.001)

        // With inversion
        val invertedConfig = config.copy(invertZoom = true)
        val invertedZoom = CreativeControlsTransformHelper.calculateZoomAmount(1.1, invertedConfig, binding)
        // 1.0 / 1.2 = 0.83333...
        assertEquals(1.0 / 1.2, invertedZoom, 0.001)
    }

    @Test
    fun calculateRotationAngle_appliesSensitivityAndInversion() {
        val config = CreativeControlsConfig(
            rotationSensitivity = 1.5f,
            invertRotation = true
        )
        val binding = GestureBinding(
            gestureType = GestureType.ROTATE_CANVAS,
            action = GestureAction.ROTATE,
            sensitivity = 2.0f,
            isInverted = false,
            enabled = true
        )

        // angle = 15.0 * 1.5 * 2.0 * (-1) = -45.0
        val angle = CreativeControlsTransformHelper.calculateRotationAngle(15.0, config, binding)
        assertEquals(-45.0, angle, 0.001)
    }

    @Test
    fun normalizedConfig_fillsMissingBindings() {
        val partialConfig = CreativeControlsConfig(
            gestureBindings = listOf(
                GestureBinding(
                    gestureType = GestureType.ONE_FINGER_PAN,
                    action = GestureAction.PAN,
                    enabled = true
                )
            )
        )
        val normalized = partialConfig.normalized()
        assertEquals(6, normalized.gestureBindings.size)
    }
}
