package com.onimeno.onicanvas.feature.controls.state

object CreativeControlsTransformHelper {

    fun resolveBinding(config: CreativeControlsConfig, gestureType: GestureType): GestureBinding? {
        val binding = config.gestureBindings.find { it.gestureType == gestureType } ?: return null
        if (!binding.enabled || binding.action == GestureAction.NONE) {
            return null
        }
        return binding
    }

    fun calculatePanDelta(
        deltaX: Double,
        deltaY: Double,
        config: CreativeControlsConfig,
        binding: GestureBinding
    ): Pair<Double, Double> {
        val effectiveSens = (config.panSensitivity * binding.sensitivity).toDouble()
        val invertX = if (config.invertPanX xor binding.isInverted) -1.0 else 1.0
        val invertY = if (config.invertPanY xor binding.isInverted) -1.0 else 1.0
        return Pair(deltaX * effectiveSens * invertX, deltaY * effectiveSens * invertY)
    }

    fun calculateZoomAmount(
        zoomFactor: Double,
        config: CreativeControlsConfig,
        binding: GestureBinding
    ): Double {
        val effectiveSens = (config.zoomSensitivity * binding.sensitivity).toDouble()
        val isInverted = config.invertZoom xor binding.isInverted
        val delta = (zoomFactor - 1.0) * effectiveSens
        return if (isInverted) {
            1.0 / (1.0 + delta).coerceAtLeast(0.01)
        } else {
            (1.0 + delta).coerceAtLeast(0.01)
        }
    }

    fun calculateRotationAngle(
        angleDegrees: Double,
        config: CreativeControlsConfig,
        binding: GestureBinding
    ): Double {
        val effectiveSens = (config.rotationSensitivity * binding.sensitivity).toDouble()
        val isInverted = config.invertRotation xor binding.isInverted
        val direction = if (isInverted) -1.0 else 1.0
        return angleDegrees * effectiveSens * direction
    }
}
