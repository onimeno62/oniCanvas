package com.onimeno.onicanvas.feature.controls.ui

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlin.math.atan2

suspend fun PointerInputScope.detectCreativeCanvasGestures(
    onOneFingerPan: (dx: Float, dy: Float) -> Unit,
    onTwoFingerPan: (dx: Float, dy: Float) -> Unit,
    onPinchZoom: (zoomFactor: Float) -> Unit,
    onRotate: (angleDegrees: Float) -> Unit,
    onTwoFingerTap: () -> Unit,
    onThreeFingerTap: () -> Unit,
    onGestureEnd: () -> Unit
) {
    awaitEachGesture {
        val firstDown = awaitFirstDown(requireUnconsumed = false)
        val startTime = System.currentTimeMillis()
        var maxPointers = 1
        var hasMovedSignificant = false
        val touchSlop = viewConfiguration.touchSlop
        var accumulatedPanDistance = 0f

        var prevCentroid = firstDown.position
        var prevSpan = 0f
        var prevAngle = 0f
        var twoFingerInitialized = false

        while (true) {
            val event = awaitPointerEvent()
            val activePointers = event.changes.filter { it.pressed }
            if (activePointers.isEmpty()) {
                break
            }

            if (activePointers.size > maxPointers) {
                maxPointers = activePointers.size
            }

            when (activePointers.size) {
                1 -> {
                    if (maxPointers == 1) {
                        val change = activePointers.first()
                        val delta = change.position - change.previousPosition
                        accumulatedPanDistance += delta.getDistance()
                        if (accumulatedPanDistance > touchSlop) {
                            hasMovedSignificant = true
                        }
                        if (hasMovedSignificant && (delta.x != 0f || delta.y != 0f)) {
                            onOneFingerPan(delta.x, delta.y)
                            change.consume()
                        }
                    }
                }
                2 -> {
                    val p1 = activePointers[0]
                    val p2 = activePointers[1]
                    val currentCentroid = (p1.position + p2.position) / 2f
                    val currentSpan = (p1.position - p2.position).getDistance()
                    val currentAngle = calculateAngle(p1.position, p2.position)

                    if (!twoFingerInitialized) {
                        prevCentroid = currentCentroid
                        prevSpan = currentSpan
                        prevAngle = currentAngle
                        twoFingerInitialized = true
                    } else {
                        val panDelta = currentCentroid - prevCentroid
                        val zoomFactor = if (prevSpan > 0f) currentSpan / prevSpan else 1f
                        var angleDelta = currentAngle - prevAngle
                        while (angleDelta > 180f) angleDelta -= 360f
                        while (angleDelta < -180f) angleDelta += 360f

                        if (panDelta.getDistance() > touchSlop * 0.4f ||
                            kotlin.math.abs(zoomFactor - 1f) > 0.015f ||
                            kotlin.math.abs(angleDelta) > 1.2f
                        ) {
                            hasMovedSignificant = true
                        }

                        if (hasMovedSignificant) {
                            if (panDelta.x != 0f || panDelta.y != 0f) {
                                onTwoFingerPan(panDelta.x, panDelta.y)
                            }
                            if (zoomFactor != 1f && zoomFactor > 0f) {
                                onPinchZoom(zoomFactor)
                            }
                            if (angleDelta != 0f) {
                                onRotate(angleDelta)
                            }
                            p1.consume()
                            p2.consume()
                        }

                        prevCentroid = currentCentroid
                        prevSpan = currentSpan
                        prevAngle = currentAngle
                    }
                }
                else -> {
                    for (pointer in activePointers) {
                        val delta = pointer.position - pointer.previousPosition
                        if (delta.getDistance() > touchSlop) {
                            hasMovedSignificant = true
                        }
                    }
                }
            }
        }

        onGestureEnd()

        val elapsed = System.currentTimeMillis() - startTime
        if (!hasMovedSignificant && elapsed < 350) {
            when (maxPointers) {
                2 -> onTwoFingerTap()
                3 -> onThreeFingerTap()
            }
        }
    }
}

private fun calculateAngle(p1: Offset, p2: Offset): Float {
    val delta = p2 - p1
    return Math.toDegrees(atan2(delta.y.toDouble(), delta.x.toDouble())).toFloat()
}
