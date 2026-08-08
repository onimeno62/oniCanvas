package com.onimeno.onicanvas.feature.controls.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Gesture
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.onimeno.onicanvas.core.designsystem.components.OniButton
import com.onimeno.onicanvas.core.designsystem.components.OniCard
import com.onimeno.onicanvas.core.designsystem.components.OniEmptyState
import com.onimeno.onicanvas.core.designsystem.components.OniSectionHeader
import com.onimeno.onicanvas.core.designsystem.components.OniTopBar
import com.onimeno.onicanvas.core.designsystem.theme.LocalSpacing
import com.onimeno.onicanvas.feature.controls.state.ControlModule
import com.onimeno.onicanvas.feature.controls.state.ControlsUiState
import com.onimeno.onicanvas.feature.controls.viewmodel.ControlsViewModel

@Composable
fun ControlsScreen(modifier: Modifier = Modifier, viewModel: ControlsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    var selectedModuleForConfig by remember { mutableStateOf<ControlModule?>(null) }
    Scaffold(modifier = modifier.fillMaxSize(), topBar = {
        OniTopBar(title = "Controls Layout", actions = { IconButton(onClick = viewModel::loadControls) { Icon(Icons.Rounded.Tune, contentDescription = "Reload layouts", tint = MaterialTheme.colorScheme.primary) } })
    }) { innerPadding ->
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(innerPadding)) {
            when (val state = uiState) {
                ControlsUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
                is ControlsUiState.Success -> ControlsContent(state = state, onModuleClick = { selectedModuleForConfig = it })
                is ControlsUiState.Error -> OniEmptyState(title = "Failed to load controls", description = state.message, icon = Icons.Rounded.Category, actionText = "Retry", onActionClick = viewModel::loadControls)
            }
            selectedModuleForConfig?.let { module -> ControlConfigSimulatorDialog(module = module, onDismiss = { selectedModuleForConfig = null }) { updated -> viewModel.toggleModuleState(module.id, updated); selectedModuleForConfig = null } }
        }
    }
}

@Composable
fun ControlsContent(state: ControlsUiState.Success, onModuleClick: (ControlModule) -> Unit, modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    LazyColumn(modifier = modifier.fillMaxSize().testTag("controls_screen_container"), contentPadding = PaddingValues(spacing.medium), verticalArrangement = Arrangement.spacedBy(spacing.medium)) {
        item { Column(modifier = Modifier.fillMaxWidth().padding(bottom = spacing.small)) {
            Text("ACTIVE PROFILE MAPPING", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text(state.currentProfileName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(4.dp)); Text("Control pads auto-sync inputs directly to this application.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } }
        item { OniSectionHeader(title = "Hardware Control Nodes") }
        items(state.modules, key = { it.id }) { module -> ControlModuleCard(module = module, onClick = { onModuleClick(module) }) }
    }
}

@Composable
fun ControlModuleCard(module: ControlModule, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val icon = when (module.iconName) { "apps" -> Icons.Rounded.Apps; "gesture" -> Icons.Rounded.Gesture; "track_changes" -> Icons.Rounded.Category; "grid_view" -> Icons.Rounded.GridView; "tune" -> Icons.Rounded.Tune; else -> Icons.Rounded.Category }
    OniCard(modifier = modifier.fillMaxWidth().testTag("control_card_${module.id}"), onClick = onClick) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(Modifier.size(44.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), CircleShape), contentAlignment = Alignment.Center) { Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp)) }
                Spacer(Modifier.width(spacing.medium)); Column { Text(module.title, MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface); Spacer(Modifier.height(2.dp)); Text(module.description, MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape).padding(horizontal = 8.dp, vertical = 2.dp)) { Text(module.activeState.uppercase(), MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }
                Text(module.lastSync, MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun ControlConfigSimulatorDialog(module: ControlModule, onDismiss: () -> Unit, onSaveState: (String) -> Unit) {
    var sizeSliderVal by remember { mutableFloatStateOf(45f) }
    var opacitySliderVal by remember { mutableFloatStateOf(80f) }
    var activeSubMode by remember { mutableStateOf(module.activeState) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Configure ${module.title}", fontWeight = FontWeight.Bold) }, text = {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Simulating Live Input / Mapping controls:", MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            when (module.id) {
                "brush_controls" -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Brush Size: ${sizeSliderVal.toInt()} px", MaterialTheme.typography.bodyMedium); Slider(value = sizeSliderVal, onValueChange = { sizeSliderVal = it }, valueRange = 1f..100f, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(4.dp)); Text("Brush Opacity: ${opacitySliderVal.toInt()}%", MaterialTheme.typography.bodyMedium); Slider(value = opacitySliderVal, onValueChange = { opacitySliderVal = it }, valueRange = 1f..100f, modifier = Modifier.fillMaxWidth())
                }
                "macro_pad" -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Configure Button Layout", MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("Standard", "Advanced", "Color").forEach { mode ->
                        Box(Modifier.weight(1f).background(if (activeSubMode.contains(mode)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small).clickable { activeSubMode = "$mode Mode" }.padding(vertical = 12.dp), contentAlignment = Alignment.Center) { Text(mode, color = if (activeSubMode.contains(mode)) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold) }
                    } }
                }
                "gesture_pad" -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Sensitivity Control", MaterialTheme.typography.bodyMedium); Slider(value = sizeSliderVal, onValueChange = { sizeSliderVal = it }, valueRange = 10f..100f)
                    OniButton(text = "Toggle Precision Lock", onClick = { activeSubMode = if (activeSubMode.contains("Lock ON")) "Precision Lock OFF" else "Precision Lock ON" }, isPrimary = true, modifier = Modifier.fillMaxWidth())
                }
                else -> Text("This module connects live parameters such as radial wheels or shortcuts. Live values sync seamlessly to Clip Studio Paint over local transport protocols.", MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }, confirmButton = { TextButton(onClick = { onSaveState(activeSubMode) }) { Text("Save Layout") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}
