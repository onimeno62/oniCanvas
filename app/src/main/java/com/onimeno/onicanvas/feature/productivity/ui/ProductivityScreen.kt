package com.onimeno.onicanvas.feature.productivity.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Mouse
import androidx.compose.material.icons.rounded.PanTool
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.Redo
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.material.icons.rounded.Transform
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
fun ProductivityScreen(viewModel: ProductivityViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selected by remember { mutableStateOf(ProductivityTool.RADIAL_MENU) }
    Scaffold(modifier.fillMaxSize(), topBar = { OniTopBar(title = "Productivity") }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { StatusCard(state.isConnected) }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ProductivityTool.values().forEach { tool ->
                        FilterChip(selected == tool, { selected = tool }, label = { Text(tool.label(), maxLines = 1) }, leadingIcon = { Icon(tool.icon(), null) }, modifier = Modifier.weight(1f).testTag("productivity_mode_${tool.name.lowercase()}"))
                    }
                }
            }
            item {
                when (selected) {
                    ProductivityTool.RADIAL_MENU -> RadialPanel(state.isConnected, viewModel)
                    ProductivityTool.TOUCHPAD -> TouchpadPanel(state.isConnected, viewModel)
                    ProductivityTool.LAYERS -> LayerPanel(state.isConnected, viewModel)
                    ProductivityTool.BRUSHES -> BrushPanel(state.isConnected, viewModel)
                }
            }
        }
    }
}

@Composable private fun StatusCard(connected: Boolean) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (connected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Rounded.RadioButtonChecked, null)
            Column { Text(if (connected) "Companion connected" else "Companion disconnected", fontWeight = FontWeight.Bold); Text(if (connected) "Productivity commands are live" else "Connect before using controls", style = MaterialTheme.typography.bodySmall) }
        }
    }
}

@Composable private fun RadialPanel(connected: Boolean, vm: ProductivityViewModel) {
    val actions = listOf(ProductivityAction.UNDO to Icons.Rounded.Undo, ProductivityAction.REDO to Icons.Rounded.Redo, ProductivityAction.BRUSH to Icons.Rounded.Brush, ProductivityAction.ERASER to Icons.Rounded.Delete, ProductivityAction.SAVE to Icons.Rounded.Save, ProductivityAction.TRANSFORM to Icons.Rounded.Transform, ProductivityAction.SELECT to Icons.Rounded.SelectAll, ProductivityAction.COPY to Icons.Rounded.ContentCopy)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Radial Menu", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Eight one-tap thumb actions", style = MaterialTheme.typography.bodySmall)
        Box(Modifier.fillMaxWidth().height(320.dp).testTag("radial_menu_surface"), contentAlignment = Alignment.Center) {
            Card(Modifier.size(92.dp), shape = CircleShape, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Icon(Icons.Rounded.Menu, "Radial menu") } }
            actions.forEachIndexed { index, pair ->
                val angle = Math.toRadians(index * 45.0 - 90.0)
                val x = (kotlin.math.cos(angle) * 105.0).toFloat(); val y = (kotlin.math.sin(angle) * 105.0).toFloat()
                androidx.compose.material3.IconButton(onClick = { vm.perform(pair.first) }, enabled = connected, modifier = Modifier.align(Alignment.Center).offset(x.dp, y.dp).size(54.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape).testTag("radial_${pair.first.name.lowercase()}")) { Icon(pair.second, pair.first.name) }
            }
        }
    }
}

@Composable private fun TouchpadPanel(connected: Boolean, vm: ProductivityViewModel) {
    var sensitivity by remember { mutableStateOf(1f) }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Touchpad Mode", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Drag to move the Windows cursor. Tap for left click; long-press for right click.", style = MaterialTheme.typography.bodySmall)
        Box(Modifier.fillMaxWidth().height(300.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp)).pointerInput(connected, sensitivity) { detectDragGestures { _, amount -> vm.mouseMove((amount.x * sensitivity).toDouble(), (amount.y * sensitivity).toDouble()) } }.pointerInput(connected) { detectTapGestures(onTap = { vm.mouseButton("left", true); vm.mouseButton("left", false) }, onLongPress = { vm.mouseButton("right", true); vm.mouseButton("right", false) }) }.testTag("touchpad_surface"), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Rounded.Mouse, null, Modifier.size(48.dp)); Text(if (connected) "Touchpad active" else "Touchpad disabled", fontWeight = FontWeight.Bold); Text("Drag • tap • long press", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall) }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Sensitivity")
            Slider(value = sensitivity, onValueChange = { sensitivity = it }, valueRange = 0.5f..3f, modifier = Modifier.weight(1f).testTag("touchpad_sensitivity"))
        }
        Button(onClick = { vm.scroll(0.0, -5.0) }, enabled = connected, modifier = Modifier.fillMaxWidth().testTag("touchpad_scroll_up")) { Text("Scroll Up") }
        Button(onClick = { vm.scroll(0.0, 5.0) }, enabled = connected, modifier = Modifier.fillMaxWidth().testTag("touchpad_scroll_down")) { Text("Scroll Down") }
    }
}

@Composable private fun LayerPanel(connected: Boolean, vm: ProductivityViewModel) {
    val actions = listOf(ProductivityAction.NEW_LAYER to "New Layer", ProductivityAction.DUPLICATE_LAYER to "Duplicate Layer", ProductivityAction.MERGE_LAYER to "Merge Layer", ProductivityAction.LOCK_LAYER to "Lock Layer", ProductivityAction.TOGGLE_MASK to "Toggle Mask", ProductivityAction.OPACITY_DOWN to "Opacity −", ProductivityAction.OPACITY_UP to "Opacity +")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Layer Controls", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        actions.forEach { (action, label) -> Button(onClick = { vm.perform(action) }, enabled = connected, modifier = Modifier.fillMaxWidth().testTag("layer_${action.name.lowercase()}")) { Icon(Icons.Rounded.Layers, null); Spacer(Modifier.size(8.dp)); Text(label) } }
    }
}

@Composable private fun BrushPanel(connected: Boolean, vm: ProductivityViewModel) {
    val favorites = listOf("Pencil", "G-Pen", "Airbrush", "Marker", "Oil", "Watercolor", "Eraser", "Blend")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Brush Favorites", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Quick preset slots forwarded to the companion.", style = MaterialTheme.typography.bodySmall)
        favorites.chunked(2).forEachIndexed { row, pair ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pair.forEachIndexed { column, name ->
                    OutlinedCard(onClick = { vm.brushPreset(row * 2 + column) }, enabled = connected, modifier = Modifier.weight(1f).testTag("brush_favorite_${row * 2 + column}")) {
                        Column(Modifier.fillMaxWidth().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Rounded.Brush, null); Spacer(Modifier.height(4.dp)); Text(name, fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
    }
}

private fun ProductivityTool.label() = when (this) { ProductivityTool.RADIAL_MENU -> "Radial"; ProductivityTool.TOUCHPAD -> "Touchpad"; ProductivityTool.LAYERS -> "Layers"; ProductivityTool.BRUSHES -> "Brushes" }
private fun ProductivityTool.icon(): ImageVector = when (this) { ProductivityTool.RADIAL_MENU -> Icons.Rounded.Menu; ProductivityTool.TOUCHPAD -> Icons.Rounded.PanTool; ProductivityTool.LAYERS -> Icons.Rounded.Layers; ProductivityTool.BRUSHES -> Icons.Rounded.Brush }
