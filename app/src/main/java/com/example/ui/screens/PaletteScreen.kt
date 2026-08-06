package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.BrushFavorite
import com.example.data.database.RecentColor
import com.example.ui.components.CircularRadialMenu
import com.example.ui.components.CircularZoomWheel
import com.example.ui.components.GlassCard
import com.example.ui.components.InteractiveColorWheel
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryPurple

@Composable
fun PaletteScreen(
    recentColors: List<RecentColor>,
    favoriteBrushes: List<BrushFavorite>,
    onTriggerAction: (String, String) -> Unit,
    onColorSelected: (String) -> Unit
) {
    var activeSubTab by remember { mutableStateOf("Color") }
    var currentZoom by remember { mutableStateOf(100) }
    var selectedColorHex by remember { mutableStateOf("#6366F1") }

    val tabs = listOf("Color", "Radial", "Zoom", "Brushes", "Layers", "Canvas")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080710))
            .padding(16.dp)
    ) {
        // Header
        Column {
            Text(
                text = "Creative Controls",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Pro-artist tactile control panels",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Horizontal scrolling subtabs bar
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(tabs) { tab ->
                val isSelected = tab == activeSubTab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) PrimaryIndigo else Color(0xFF131124))
                        .border(
                            width = 0.5.dp,
                            color = if (isSelected) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { activeSubTab = tab }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = tab,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Multi-view panels using Animated Crossfade
        Crossfade(
            targetState = activeSubTab,
            animationSpec = tween(durationMillis = 180),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            label = "CreativeToolsTab"
        ) { tab ->
            when (tab) {
                "Color" -> ColorPickerPanel(
                    recentColors = recentColors,
                    selectedColorHex = selectedColorHex,
                    onColorChanged = { hex ->
                        selectedColorHex = hex
                        onColorSelected(hex)
                    }
                )
                "Radial" -> RadialMenuPanel(onItemSelected = { onTriggerAction(it, "RadialTrigger") })
                "Zoom" -> ZoomWheelPanel(
                    currentZoom = currentZoom,
                    onZoomChanged = { valZoom ->
                        currentZoom = valZoom
                        onTriggerAction("Zoom", "Ctrl+[$valZoom%]")
                    }
                )
                "Brushes" -> BrushesPanel(
                    brushes = favoriteBrushes,
                    onBrushSelected = { onTriggerAction(it, "SelectBrush") }
                )
                "Layers" -> LayersPanel(onLayerAction = { onTriggerAction(it, "LayerOp") })
                "Canvas" -> CanvasControlsPanel(onCanvasAction = { onTriggerAction(it, "CanvasOp") })
            }
        }
    }
}

@Composable
fun ColorPickerPanel(
    recentColors: List<RecentColor>,
    selectedColorHex: String,
    onColorChanged: (String) -> Unit
) {
    val selectedColor = remember(selectedColorHex) {
        try {
            Color(android.graphics.Color.parseColor(selectedColorHex))
        } catch (e: Exception) {
            Color(0xFF6366F1)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(240.dp)
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            InteractiveColorWheel(
                selectedColor = selectedColor,
                onColorSelected = { col ->
                    val hexStr = String.format("#%06X", 0xFFFFFF and col.value.toLong().toInt())
                    onColorChanged(hexStr)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Palette Display / Recent Colors
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Colors",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(selectedColor)
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Color Swatches
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                recentColors.take(6).forEach { rc ->
                    val colorHex = rc.hex
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(android.graphics.Color.parseColor(colorHex)))
                            .border(
                                width = if (colorHex == selectedColorHex) 2.dp else 0.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onColorChanged(colorHex) }
                    )
                }
                
                // Add button for preset
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1F1D36))
                        .clickable { onColorChanged("#06B6D4") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun RadialMenuPanel(
    onItemSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularRadialMenu(onItemSelected = onItemSelected, modifier = Modifier.size(260.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Hover thumb or tap a slice to perform drawing actions",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ZoomWheelPanel(
    currentZoom: Int,
    onZoomChanged: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularZoomWheel(
            currentZoom = currentZoom,
            onZoomChanged = onZoomChanged,
            modifier = Modifier.size(240.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onZoomChanged((currentZoom - 25).coerceAtLeast(25)) },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF131124), CircleShape)
                    .border(0.5.dp, Color.White.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = "Zoom Out", tint = Color.White)
            }

            Button(
                onClick = { onZoomChanged(100) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F1D36)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.border(0.5.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
            ) {
                Text(text = "Reset 100%", color = Color.White, fontSize = 12.sp)
            }

            IconButton(
                onClick = { onZoomChanged((currentZoom + 25).coerceAtMost(800)) },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF131124), CircleShape)
                    .border(0.5.dp, Color.White.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Zoom In", tint = Color.White)
            }
        }
    }
}

@Composable
fun BrushesPanel(
    brushes: List<BrushFavorite>,
    onBrushSelected: (String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(brushes) { brush ->
            var isStarred by remember { mutableStateOf(true) }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF131124).copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                    .border(0.5.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                    .clickable { onBrushSelected(brush.name) }
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(AccentCyan.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Brush, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = brush.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = brush.category, color = Color.White.copy(alpha = 0.45f), fontSize = 11.sp)
                        }
                    }

                    IconButton(onClick = { isStarred = !isStarred }) {
                        Icon(
                            imageVector = if (isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (isStarred) Color(0xFFF59E0B) else Color.White.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LayersPanel(
    onLayerAction: (String) -> Unit
) {
    val layerOps = listOf(
        Pair("New Layer", Icons.Default.Add),
        Pair("Duplicate", Icons.Default.CopyAll),
        Pair("Merge Down", Icons.Default.Merge),
        Pair("Delete", Icons.Default.Delete),
        Pair("Lock Layer", Icons.Default.Lock),
        Pair("Opacity", Icons.Default.Tune)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Tactile Layer Operations",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        layerOps.chunked(2).forEach { rowOps ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowOps.forEach { op ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF131124).copy(alpha = 0.8f), RoundedCornerShape(10.dp))
                            .border(0.5.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                            .clickable { onLayerAction(op.first) }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = op.second, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = op.first, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CanvasControlsPanel(
    onCanvasAction: (String) -> Unit
) {
    val canvasOps = listOf(
        Triple("Rotate Left", Icons.Default.RotateLeft, "RotateLeft"),
        Triple("Rotate Right", Icons.Default.RotateRight, "RotateRight"),
        Triple("Flip Horizontal", Icons.Default.Flip, "FlipH"),
        Triple("Flip Vertical", Icons.Default.Flip, "FlipV"),
        Triple("Reset Rotation", Icons.Default.Refresh, "ResetRot"),
        Triple("Fit Screen", Icons.Default.Fullscreen, "Fit")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Canvas Manipulation Tools",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        canvasOps.chunked(2).forEach { rowOps ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowOps.forEach { op ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF131124).copy(alpha = 0.8f), RoundedCornerShape(10.dp))
                            .border(0.5.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                            .clickable { onCanvasAction(op.first) }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = op.second, contentDescription = null, tint = SecondaryPurple, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = op.first, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
