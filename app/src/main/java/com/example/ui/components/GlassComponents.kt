package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.MacroButton
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = Color.White.copy(alpha = 0.08f),
    backgroundColor: Color = Color(0xFF131124).copy(alpha = 0.65f),
    cornerRadius: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.12f),
                        Color.White.copy(alpha = 0.03f)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        backgroundColor.copy(alpha = 0.85f),
                        backgroundColor.copy(alpha = 0.65f)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(16.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun MacroButtonView(
    button: MacroButton,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = remember(button.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(button.colorHex))
        } catch (e: Exception) {
            Color(0xFF6366F1)
        }
    }

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = tween(durationMillis = 100)
    )

    Box(
        modifier = modifier
            .testTag("macro_button_${button.label.lowercase().replace(" ", "_")}")
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        try {
                            awaitRelease()
                        } finally {
                            isPressed = false
                        }
                        onClick()
                    }
                )
            }
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.3f),
                        accentColor.copy(alpha = 0.05f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B1836).copy(alpha = 0.85f),
                        Color(0xFF131124).copy(alpha = 0.95f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .drawBehind {
                // Glow shadow matching the button accent color
                drawCircle(
                    color = accentColor.copy(alpha = 0.04f),
                    radius = size.maxDimension / 2.2f,
                    center = center
                )
            }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(accentColor.copy(alpha = 0.1f), CircleShape)
                    .border(0.5.dp, accentColor.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getIconByName(button.iconName),
                    contentDescription = button.label,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = button.label,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = button.actionShortcut,
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatusCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFF131124).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .border(0.5.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Column {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(color, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = value,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun CircularZoomWheel(
    currentZoom: Int,
    onZoomChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val sizePx = constraints.maxWidth.coerceAtMost(constraints.maxHeight)
        val sizeDp = (sizePx / LocalContext.current.resources.displayMetrics.density).dp

        Canvas(
            modifier = Modifier
                .size(sizeDp)
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        val angle = atan2(
                            change.position.y - (sizePx / 2f),
                            change.position.x - (sizePx / 2f)
                        )
                        var degrees = Math.toDegrees(angle.toDouble())
                        if (degrees < 0) degrees += 360.0
                        
                        // Map degrees 0-360 to zoom percentage 25% to 800%
                        val zoomValue = (25 + (degrees / 360.0) * 775).toInt().coerceIn(25, 800)
                        onZoomChanged(zoomValue)
                    }
                }
        ) {
            val center = this.center
            val radius = size.minDimension / 2.2f
            
            // Draw dial background track
            drawCircle(
                color = Color(0xFF1F1D36).copy(alpha = 0.4f),
                radius = radius,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw glowing active progress
            val sweepAngle = ((currentZoom - 25) / 775f) * 360f
            drawArc(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF6366F1), Color(0xFF06B6D4))
                ),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw indicator point
            val radAngle = Math.toRadians((sweepAngle - 90).toDouble())
            val ptX = center.x + radius * cos(radAngle).toFloat()
            val ptY = center.y + radius * sin(radAngle).toFloat()
            
            drawCircle(
                color = Color.White,
                radius = 6.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(ptX, ptY)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$currentZoom%",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Fit Screen",
                color = Color(0xFF06B6D4),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onZoomChanged(100) }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun CircularRadialMenu(
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf("Undo", "Redo", "Eraser", "Picker", "Brush", "Rotate")
    val icons = listOf(
        Icons.Default.Star, // We will map them properly
        Icons.Default.Star,
        Icons.Default.Star,
        Icons.Default.Star,
        Icons.Default.Star,
        Icons.Default.Star
    )
    val colors = listOf(
        Color(0xFF6366F1),
        Color(0xFF8B5CF6),
        Color(0xFF06B6D4),
        Color(0xFFF59E0B),
        Color(0xFF10B981),
        Color(0xFFEF4444)
    )

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val sizePx = constraints.maxWidth.coerceAtMost(constraints.maxHeight)
        val sizeDp = (sizePx / LocalContext.current.resources.displayMetrics.density).dp

        var hoveredIndex by remember { mutableStateOf(-1) }

        Canvas(
            modifier = Modifier
                .size(sizeDp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { offset ->
                            val dx = offset.x - (sizePx / 2f)
                            val dy = offset.y - (sizePx / 2f)
                            val distance = Math.sqrt((dx * dx + dy * dy).toDouble())
                            
                            // Ensure click is in the ring zone, not the center
                            if (distance in (sizePx / 6f)..(sizePx / 2f)) {
                                var angle = atan2(dy, dx)
                                if (angle < 0) angle += (2 * PI).toFloat()
                                val segmentAngle = ((2 * PI) / items.size).toFloat()
                                val index = (angle / segmentAngle).toInt() % items.size
                                onItemSelected(items[index])
                            }
                        }
                    )
                }
        ) {
            val center = this.center
            val outerRadius = size.minDimension / 2.1f
            val innerRadius = size.minDimension / 5f

            val segmentAngle = 360f / items.size

            for (i in items.indices) {
                val startAngle = i * segmentAngle
                
                // Draw arc segment
                drawArc(
                    color = if (hoveredIndex == i) colors[i].copy(alpha = 0.25f) else Color(0xFF1F1D36).copy(alpha = 0.6f),
                    startAngle = startAngle,
                    sweepAngle = segmentAngle - 2f,
                    useCenter = true,
                    style = Stroke(width = (outerRadius - innerRadius))
                )

                // Draw segment separator lines
                val separatorAngle = Math.toRadians(startAngle.toDouble())
                val endX = center.x + outerRadius * cos(separatorAngle).toFloat()
                val endY = center.y + outerRadius * sin(separatorAngle).toFloat()
                
                drawLine(
                    color = Color.White.copy(alpha = 0.08f),
                    start = center,
                    end = androidx.compose.ui.geometry.Offset(endX, endY),
                    strokeWidth = 2.dp.toPx()
                )
            }

            // Draw inner center hole
            drawCircle(
                color = Color(0xFF080710),
                radius = innerRadius
            )
            drawCircle(
                color = Color(0xFF06B6D4).copy(alpha = 0.15f),
                radius = innerRadius,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Add visual text overlay labels
        items.forEachIndexed { i, item ->
            val angle = (i * (360f / items.size)) + (360f / items.size / 2)
            val radAngle = Math.toRadians(angle.toDouble())
            val offsetRadius = sizeDp / 3.2f

            val ptX = (sizeDp / 2) + offsetRadius * cos(radAngle).toFloat()
            val ptY = (sizeDp / 2) + offsetRadius * sin(radAngle).toFloat()

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .padding(4.dp)
                    .align(Alignment.TopStart)
                    .padding(
                        start = ptX - 27.dp,
                        top = ptY - 27.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = getIconByName(
                            when (item) {
                                "Undo" -> "Undo"
                                "Redo" -> "Redo"
                                "Eraser" -> "Delete"
                                "Picker" -> "Colorize"
                                "Brush" -> "Brush"
                                else -> "Refresh"
                            }
                        ),
                        contentDescription = item,
                        tint = colors[i],
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = item,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Center stylized logo
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(Color(0xFF131124), CircleShape)
                .border(1.5.dp, Color(0xFF06B6D4), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getIconByName("Brush"),
                contentDescription = "OniCenter",
                tint = Color(0xFF06B6D4),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun InteractiveColorWheel(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val sizePx = constraints.maxWidth.coerceAtMost(constraints.maxHeight)
        val sizeDp = (sizePx / LocalContext.current.resources.displayMetrics.density).dp

        Canvas(
            modifier = Modifier
                .size(sizeDp)
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        val dx = change.position.x - (sizePx / 2f)
                        val dy = change.position.y - (sizePx / 2f)
                        val distance = Math.sqrt((dx * dx + dy * dy).toDouble())
                        
                        // Treat drag around outer ring as Hue selection
                        if (distance in (sizePx / 3f)..(sizePx / 2f)) {
                            var angle = atan2(dy, dx)
                            if (angle < 0) angle += (2 * PI).toFloat()
                            val hue = (angle / (2 * PI).toFloat() * 360f)
                            val hsv = FloatArray(3)
                            android.graphics.Color.colorToHSV(selectedColor.value.toLong().toInt(), hsv)
                            hsv[0] = hue
                            onColorSelected(Color(android.graphics.Color.HSVToColor(hsv)))
                        }
                    }
                }
        ) {
            val center = this.center
            val radius = size.minDimension / 2.2f
            
            // Draw Color Ring
            for (angle in 0 until 360 step 2) {
                val hsv = floatArrayOf(angle.toFloat(), 1f, 1f)
                val color = Color(android.graphics.Color.HSVToColor(hsv))
                
                drawArc(
                    color = color,
                    startAngle = angle.toFloat(),
                    sweepAngle = 3f,
                    useCenter = false,
                    style = Stroke(width = 16.dp.toPx())
                )
            }

            // Inner indicator square (just visually placeholder for high fidelity)
            val rectSize = radius * 1.0f
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, Color.Black)
                ),
                topLeft = androidx.compose.ui.geometry.Offset(center.x - rectSize/2f, center.y - rectSize/2f),
                size = androidx.compose.ui.geometry.Size(rectSize, rectSize)
            )
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, selectedColor)
                ),
                topLeft = androidx.compose.ui.geometry.Offset(center.x - rectSize/2f, center.y - rectSize/2f),
                size = androidx.compose.ui.geometry.Size(rectSize, rectSize)
            )
            drawRect(
                color = Color.White.copy(alpha = 0.2f),
                topLeft = androidx.compose.ui.geometry.Offset(center.x - rectSize/2f, center.y - rectSize/2f),
                size = androidx.compose.ui.geometry.Size(rectSize, rectSize),
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}

// Map logical string icon names to beautiful Material vector icons
@Composable
fun getIconByName(name: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (name) {
        "Undo" -> androidx.compose.material.icons.Icons.AutoMirrored.Filled.Undo
        "Redo" -> androidx.compose.material.icons.Icons.AutoMirrored.Filled.Redo
        "Save" -> androidx.compose.material.icons.Icons.Filled.Save
        "Brush" -> androidx.compose.material.icons.Icons.Filled.Brush
        "Delete" -> androidx.compose.material.icons.Icons.Filled.Delete
        "Colorize" -> androidx.compose.material.icons.Icons.Filled.Colorize
        "Flip" -> androidx.compose.material.icons.Icons.Filled.Flip
        "Refresh" -> androidx.compose.material.icons.Icons.Filled.Refresh
        "PanTool" -> androidx.compose.material.icons.Icons.Filled.PanTool
        "ZoomIn" -> androidx.compose.material.icons.Icons.Filled.ZoomIn
        "ZoomOut" -> androidx.compose.material.icons.Icons.Filled.ZoomOut
        "Share" -> androidx.compose.material.icons.Icons.Filled.Share
        "Lock" -> androidx.compose.material.icons.Icons.Filled.Lock
        "Star" -> androidx.compose.material.icons.Icons.Filled.Star
        "StarBorder" -> androidx.compose.material.icons.Icons.Filled.StarBorder
        "FormatPaint" -> androidx.compose.material.icons.Icons.Filled.FormatPaint
        else -> androidx.compose.material.icons.Icons.Filled.Info
    }
}
