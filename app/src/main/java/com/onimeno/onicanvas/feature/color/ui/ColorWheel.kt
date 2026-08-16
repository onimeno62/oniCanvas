package com.onimeno.onicanvas.feature.color.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.onimeno.onicanvas.feature.color.model.ColorConversion
import com.onimeno.onicanvas.feature.color.model.ColorModel
import com.onimeno.onicanvas.feature.color.model.HsvColor
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

private enum class DragTarget {
    NONE,
    HUE_RING,
    SV_BOX
}

@Composable
fun ColorWheel(
    selectedColor: ColorModel,
    onHsvChanged: (hue: Float, saturation: Float, value: Float) -> Unit,
    onColorCommitted: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeDragTarget by remember { mutableStateOf(DragTarget.NONE) }

    val hueRainbow = remember {
        listOf(
            Color.Red,
            Color.Yellow,
            Color.Green,
            Color.Cyan,
            Color.Blue,
            Color.Magenta,
            Color.Red
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .sizeIn(minWidth = 200.dp, minHeight = 200.dp, maxWidth = 380.dp, maxHeight = 380.dp)
            .aspectRatio(1f)
            .semantics {
                contentDescription = "Interactive Color Wheel and Saturation Brightness Box"
            }
            .testTag("color_wheel"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .pointerInput(selectedColor) {
                    detectTapGestures(
                        onPress = { offset ->
                            val sizeMin = min(size.width, size.height)
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val outerRadius = sizeMin / 2f - 8f
                            val ringStroke = outerRadius * 0.16f
                            val innerRingRadius = outerRadius - ringStroke

                            val dist = (offset - center).getDistance()
                            val boxHalfSize = (innerRingRadius * 0.65f)
                            val boxLeft = center.x - boxHalfSize
                            val boxTop = center.y - boxHalfSize
                            val boxSize = boxHalfSize * 2f

                            if (dist >= innerRingRadius - 16f && dist <= outerRadius + 16f) {
                                activeDragTarget = DragTarget.HUE_RING
                                val angleRad = atan2(offset.y - center.y, offset.x - center.x)
                                var deg = Math.toDegrees(angleRad.toDouble()).toFloat()
                                if (deg < 0f) deg += 360f
                                onHsvChanged(deg, selectedColor.hsv.saturation, selectedColor.hsv.value)
                            } else if (offset.x in (boxLeft - 16f)..(boxLeft + boxSize + 16f) &&
                                offset.y in (boxTop - 16f)..(boxTop + boxSize + 16f)
                            ) {
                                activeDragTarget = DragTarget.SV_BOX
                                val s = ((offset.x - boxLeft) / boxSize).coerceIn(0f, 1f) * 100f
                                val v = (1f - ((offset.y - boxTop) / boxSize).coerceIn(0f, 1f)) * 100f
                                onHsvChanged(selectedColor.hsv.hue, s, v)
                            }

                            val success = tryAwaitRelease()
                            if (success) {
                                onColorCommitted()
                            }
                            activeDragTarget = DragTarget.NONE
                        }
                    )
                }
                .pointerInput(selectedColor) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val sizeMin = min(size.width, size.height)
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val outerRadius = sizeMin / 2f - 8f
                            val ringStroke = outerRadius * 0.16f
                            val innerRingRadius = outerRadius - ringStroke

                            val dist = (offset - center).getDistance()
                            val boxHalfSize = (innerRingRadius * 0.65f)
                            val boxLeft = center.x - boxHalfSize
                            val boxTop = center.y - boxHalfSize
                            val boxSize = boxHalfSize * 2f

                            if (dist >= innerRingRadius - 20f && dist <= outerRadius + 20f) {
                                activeDragTarget = DragTarget.HUE_RING
                                val angleRad = atan2(offset.y - center.y, offset.x - center.x)
                                var deg = Math.toDegrees(angleRad.toDouble()).toFloat()
                                if (deg < 0f) deg += 360f
                                onHsvChanged(deg, selectedColor.hsv.saturation, selectedColor.hsv.value)
                            } else if (offset.x in (boxLeft - 20f)..(boxLeft + boxSize + 20f) &&
                                offset.y in (boxTop - 20f)..(boxTop + boxSize + 20f)
                            ) {
                                activeDragTarget = DragTarget.SV_BOX
                                val s = ((offset.x - boxLeft) / boxSize).coerceIn(0f, 1f) * 100f
                                val v = (1f - ((offset.y - boxTop) / boxSize).coerceIn(0f, 1f)) * 100f
                                onHsvChanged(selectedColor.hsv.hue, s, v)
                            } else {
                                activeDragTarget = DragTarget.NONE
                            }
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val pos = change.position
                            val sizeMin = min(size.width, size.height)
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val outerRadius = sizeMin / 2f - 8f
                            val ringStroke = outerRadius * 0.16f
                            val innerRingRadius = outerRadius - ringStroke

                            val boxHalfSize = (innerRingRadius * 0.65f)
                            val boxLeft = center.x - boxHalfSize
                            val boxTop = center.y - boxHalfSize
                            val boxSize = boxHalfSize * 2f

                            when (activeDragTarget) {
                                DragTarget.HUE_RING -> {
                                    val angleRad = atan2(pos.y - center.y, pos.x - center.x)
                                    var deg = Math.toDegrees(angleRad.toDouble()).toFloat()
                                    if (deg < 0f) deg += 360f
                                    onHsvChanged(deg, selectedColor.hsv.saturation, selectedColor.hsv.value)
                                }
                                DragTarget.SV_BOX -> {
                                    val s = ((pos.x - boxLeft) / boxSize).coerceIn(0f, 1f) * 100f
                                    val v = (1f - ((pos.y - boxTop) / boxSize).coerceIn(0f, 1f)) * 100f
                                    onHsvChanged(selectedColor.hsv.hue, s, v)
                                }
                                DragTarget.NONE -> Unit
                            }
                        },
                        onDragEnd = {
                            onColorCommitted()
                            activeDragTarget = DragTarget.NONE
                        },
                        onDragCancel = {
                            activeDragTarget = DragTarget.NONE
                        }
                    )
                }
        ) {
            val sizeMin = min(size.width, size.height)
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = sizeMin / 2f - 8f
            val ringStrokeWidth = outerRadius * 0.16f
            val ringMidRadius = outerRadius - ringStrokeWidth / 2f
            val innerRingRadius = outerRadius - ringStrokeWidth

            // 1. Draw Outer Hue Ring
            val sweepBrush = Brush.sweepGradient(
                colors = hueRainbow,
                center = center
            )
            drawCircle(
                brush = sweepBrush,
                radius = ringMidRadius,
                center = center,
                style = Stroke(width = ringStrokeWidth, cap = StrokeCap.Round)
            )

            // Outer and inner subtle ring borders
            drawCircle(
                color = Color.White.copy(alpha = 0.15f),
                radius = outerRadius,
                center = center,
                style = Stroke(width = 1.5f)
            )
            drawCircle(
                color = Color.Black.copy(alpha = 0.3f),
                radius = innerRingRadius,
                center = center,
                style = Stroke(width = 1.5f)
            )

            // 2. Draw Hue Indicator Thumb on Ring
            val hueRad = Math.toRadians(selectedColor.hsv.hue.toDouble())
            val hueThumbPos = Offset(
                x = center.x + (ringMidRadius * cos(hueRad)).toFloat(),
                y = center.y + (ringMidRadius * sin(hueRad)).toFloat()
            )
            val pureHueRgb = ColorConversion.hsvToRgb(selectedColor.hsv.hue, 100f, 100f)
            val pureHueColor = Color(pureHueRgb.r, pureHueRgb.g, pureHueRgb.b)

            // Thumb shadow & ring
            drawCircle(
                color = Color.Black.copy(alpha = 0.6f),
                radius = ringStrokeWidth * 0.48f + 2f,
                center = hueThumbPos
            )
            drawCircle(
                color = Color.White,
                radius = ringStrokeWidth * 0.48f,
                center = hueThumbPos
            )
            drawCircle(
                color = pureHueColor,
                radius = ringStrokeWidth * 0.34f,
                center = hueThumbPos
            )

            // 3. Draw Inner Saturation-Value Box
            val boxHalfSize = (innerRingRadius * 0.65f)
            val boxLeft = center.x - boxHalfSize
            val boxTop = center.y - boxHalfSize
            val boxSize = boxHalfSize * 2f

            // Horizontal gradient: White to Pure Hue
            val satBrush = Brush.horizontalGradient(
                colors = listOf(Color.White, pureHueColor),
                startX = boxLeft,
                endX = boxLeft + boxSize
            )
            drawRect(
                brush = satBrush,
                topLeft = Offset(boxLeft, boxTop),
                size = Size(boxSize, boxSize)
            )

            // Vertical gradient: Transparent to Black
            val valBrush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black),
                startY = boxTop,
                endY = boxTop + boxSize
            )
            drawRect(
                brush = valBrush,
                topLeft = Offset(boxLeft, boxTop),
                size = Size(boxSize, boxSize)
            )

            // Box outline
            drawRect(
                color = Color.White.copy(alpha = 0.3f),
                topLeft = Offset(boxLeft, boxTop),
                size = Size(boxSize, boxSize),
                style = Stroke(width = 2f)
            )

            // 4. Draw SV Thumb Indicator
            val svX = boxLeft + (selectedColor.hsv.satFraction * boxSize)
            val svY = boxTop + ((1f - selectedColor.hsv.valFraction) * boxSize)
            val svThumbPos = Offset(svX, svY)

            // Draw thumb with dual ring for high visibility against any lightness
            drawCircle(
                color = Color.Black.copy(alpha = 0.5f),
                radius = 11f,
                center = svThumbPos
            )
            drawCircle(
                color = Color.White,
                radius = 9f,
                center = svThumbPos,
                style = Stroke(width = 2.5f)
            )
            drawCircle(
                color = selectedColor.composeColor,
                radius = 6.5f,
                center = svThumbPos
            )
        }
    }
}
