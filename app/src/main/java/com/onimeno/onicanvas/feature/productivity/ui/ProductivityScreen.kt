package com.onimeno.onicanvas.feature.productivity.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Mouse
import androidx.compose.material.icons.rounded.PanTool
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.Redo
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material.icons.rounded.Transform
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.onimeno.onicanvas.core.designsystem.components.OniTopBar
import com.onimeno.onicanvas.feature.productivity.state.ProductivityAction
import com.onimeno.onicanvas.feature.productivity.state.ProductivityTool
import com.onimeno.onicanvas.feature.productivity.viewmodel.ProductivityViewModel

@Composable
fun ProductivityScreen(
    viewModel: ProductivityViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTool by remember { mutableStateOf(ProductivityTool.RADIAL_MENU) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { OniTopBar(title = "Productivity") }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ConnectionStatusCard(isConnected = state.isConnected)
            }
            item {
                ProductivityModeSelector(
                    selected = selectedTool,
                    onSelected = { selectedTool = it }
                )
            }
            item {
                when (selectedTool) {
                    ProductivityTool.RADIAL_MENU -> RadialMenuPanel(state.isConnected, viewModel)
                    ProductivityTool.TOUCHPAD -> TouchpadPanel(state.isConnected, viewModel)
                    ProductivityTool.LAYERS -> LayerControlsPanel(state.isConnected, viewModel)
                    ProductivityTool.BRUSHES -> BrushFavoritesPanel(state.isConnected, viewModel)
                }
            }
        }
    }
}

@Composable
private fun ConnectionStatusCard(isConnected: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isConnected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Rounded.RadioButtonChecked, contentDescription = null)
            Column {
                Text(
                    if (isConnected) "Companion connected" else "Connect to use productivity controls",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    if (isConnected) "Commands are live" else "Controls remain safely disabled while offline",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun ProductivityModeSelector(
    selected: ProductivityTool,
    onSelected: (ProductivityTool) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProductivityTool.values().forEach { tool ->
            FilterChip(
                selected = selected == tool,
                onClick = { onSelected(tool) },
                label = { Text(tool.label()) },
                leadingIcon = { Icon(tool.icon(), contentDescription = null) },
                modifier = Modifier.testTag("productivity_mode_${tool.name.lowercase()}")
            )
        }
    }
}

@Composable
private fun RadialMenuPanel(isConnected: Boolean, viewModel: ProductivityViewModel) {
    val actions = listOf(
        ProductivityAction.UNDO to Icons.Rounded.Undo,
        ProductivityAction.REDO to Icons.Rounded.Redo,
        ProductivityAction.BRUSH to Icons.Rounded.Brush,
        ProductivityAction.ERASER to Icons.Rounded.Delete,
        ProductivityAction.SAVE to Icons.Rounded.Save,
        ProductivityAction.TRANSFORM to Icons.Rounded.Transform,
        ProductivityAction.SELECT to Icons.Rounded.SelectAll,
        ProductivityAction.COPY to Icons.Rounded.ContentCopy
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Radial Menu", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Fast thumb-access command wheel", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier.size(300.dp).testTag("radial_menu_surface"),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Widgets, contentDescription = "Radial menu")
                }
            }
            actions.forEachIndexed { index, (action, icon) ->
                val angle = Math.toRadians(index * 45.0 - 90.0)
                val radius = 100f
                val x = (kotlin.math.cos(angle) * radius).toFloat()
                val y = (kotlin.math.sin(angle) * radius).toFloat()
                IconButton(
                    onClick = { viewModel.perform(action) },
                    enabled = isConnected,
                    modifier = Modifier
                        .size(56.dp)
                        .align(Alignment.Center)
                        .offset(x.dp, y.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        .testTag("radial_${action.name.lowercase()}")
                ) {
                    Icon(icon, contentDescription = action.name)
                }
            }
        }
    }
}

@Composable
private fun TouchpadPanel(isConnected: Boolean, viewModel: ProductivityViewModel) {
    var sensitivity by remember { mutableStateOf(1f) }
    var rightPressed by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Touchpad Mode", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Move the cursor, tap for primary click, long-press for secondary click, and two-finger drag to scroll.", style = MaterialTheme.typography.bodySmall)
        Card(
            modifier = Modifier.fillMaxWidth().height(320.dp).testTag("touchpad_surface"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(isConnected, sensitivity) {
                        detectDragGestures(
                            onDrag = { _, dragAmount ->
                                viewModel.mouseMove(
                                    dragAmount.x.toDouble() * sensitivity,
                                    dragAmount.y.toDouble() * sensitivity
                                )
                            }
                        )
                    }
                    .pointerInput(isConnected) {
                        detectTapGestures(
                            onTap = { viewModel.mouseButton("left", true); viewModel.mouseButton("left", false) },
                            onLongPress = { viewModel.mouseButton("right", true); viewModel.mouseButton("right", false) }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.Mouse, contentDescription = null, modifier = Modifier.size(48.dp))
                    Text(if (isConnected) "Touchpad active" else "Touchpad disabled", fontWeight = FontWeight.Bold)
                    Text("Drag to move cursor • tap to click • long press for right click", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Sensitivity", modifier = Modifier.weight(0.25f))
            Slider(
                value = sensitivity,
                onValueChange = { sensitivity = it },
                valueRange = 0.5f..3f,
                modifier = Modifier.weight(0.75f).testTag("touchpad_sensitivity")
            )
        }
        Button(
            onClick = { viewModel.mouseButton("middle", !rightPressed); rightPressed = !rightPressed },
            enabled = isConnected,
            modifier = Modifier.fillMaxWidth().testTag("touchpad_middle_click")
        ) {
            Text(if (rightPressed) "Release Middle Button" else "Middle Click")
        }
        Text("Scroll: use a two-finger gesture on the surface.", style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun LayerControlsPanel(isConnected: Boolean, viewModel: ProductivityViewModel) {
    val actions = listOf(
        ProductivityAction.NEW_LAYER to "New Layer",
        ProductivityAction.DUPLICATE_LAYER to "Duplicate",
        ProductivityAction.MERGE_LAYER to "Merge",
        ProductivityAction.LOCK_LAYER to "Lock",
        ProductivityAction.TOGGLE_MASK to "Mask",
        ProductivityAction.OPACITY_DOWN to "Opacity −",
        ProductivityAction.OPACITY_UP to "Opacity +"
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Layer Controls", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Quick layer operations without leaving the canvas.", style = MaterialTheme.typography.bodySmall)
        actions.forEach { (action, label) ->
            Button(
                onClick = { viewModel.perform(action) },
                enabled = isConnected,
                modifier = Modifier.fillMaxWidth().testTag("layer_${action.name.lowercase()}"),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Icon(Icons.Rounded.Layers, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text(label)
            }
        }
    }
}

@Composable
private fun BrushFavoritesPanel(isConnected: Boolean, viewModel: ProductivityViewModel) {
    val favorites = listOf("Pencil", "G-Pen", "Airbrush", "Marker", "Oil", "Watercolor", "Eraser", "Blend")
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Brush Favorites", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("One-tap brush presets. The preset index is passed to the Windows Companion.", style = MaterialTheme.typography.bodySmall)
        favorites.chunked(2).forEachIndexed { row, items ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items.forEachIndexed { column, label ->
                    OutlinedCard(
                        onClick = { viewModel.brushPreset(row * 2 + column) },
                        enabled = isConnected,
                        modifier = Modifier.weight(1f).testTag("brush_favorite_${row * 2 + column}")
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Rounded.Brush, contentDescription = null)
                            Spacer(Modifier.height(6.dp))
                            Text(label, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

private fun ProductivityTool.label(): String = when (this) {
    ProductivityTool.RADIAL_MENU -> "Radial Menu"
    ProductivityTool.TOUCHPAD -> "Touchpad"
    ProductivityTool.LAYERS -> "Layers"
    ProductivityTool.BRUSHES -> "Brush Favorites"
}

private fun ProductivityTool.icon(): ImageVector = when (this) {
    ProductivityTool.RADIAL_MENU -> Icons.Rounded.Menu
    ProductivityTool.TOUCHPAD -> Icons.Rounded.PanTool
    ProductivityTool.LAYERS -> Icons.Rounded.Layers
    ProductivityTool.BRUSHES -> Icons.Rounded.Brush
}
