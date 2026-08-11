package com.onimeno.onicanvas.feature.controls.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AspectRatio
import androidx.compose.material.icons.rounded.Flip
import androidx.compose.material.icons.rounded.Redo
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.RotateLeft
import androidx.compose.material.icons.rounded.RotateRight
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.onimeno.onicanvas.core.designsystem.theme.LocalSpacing
import com.onimeno.onicanvas.feature.controls.state.ControlsUiState
import com.onimeno.onicanvas.feature.controls.state.CreativeControlsConfig
import com.onimeno.onicanvas.feature.controls.viewmodel.ControlsViewModel

@Composable
fun CreativeControlsSection(
    state: ControlsUiState.Success,
    viewModel: ControlsViewModel,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    var showSettingsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CREATIVE CANVAS CONTROLS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Gesture Surface & Real-time Canvas Manipulation",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = { showSettingsDialog = true },
                modifier = Modifier.testTag("gesture_settings_btn")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Tune,
                    contentDescription = "Gesture Settings",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 1. Gesture Pad Surface
        GesturePadSurface(
            isConnected = state.isConnected,
            config = state.activeWorkspace.creativeControlsConfig,
            onPan = { dx, dy -> viewModel.onPan(dx, dy) },
            onZoom = { factor -> viewModel.onZoom(factor) },
            onRotate = { angle -> viewModel.onRotate(angle) },
            onTapUndo = { viewModel.onTapUndo() },
            onTapRedo = { viewModel.onTapRedo() }
        )

        // 2. Zoom Controller Section
        ZoomControllerSection(
            isConnected = state.isConnected,
            viewModel = viewModel
        )

        // 3. Canvas Action Controls
        CanvasControlsSection(
            isConnected = state.isConnected,
            viewModel = viewModel
        )
    }

    if (showSettingsDialog) {
        GestureConfigDialog(
            config = state.activeWorkspace.creativeControlsConfig,
            onDismiss = { showSettingsDialog = false },
            onSave = { updated ->
                viewModel.updateCreativeControlsConfig(updated)
                showSettingsDialog = false
            }
        )
    }
}

@Composable
fun GesturePadSurface(
    isConnected: Boolean,
    config: CreativeControlsConfig,
    onPan: (Double, Double) -> Unit,
    onZoom: (Double) -> Unit,
    onRotate: (Double) -> Unit,
    onTapUndo: () -> Unit,
    onTapRedo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .testTag("gesture_pad_surface")
            .border(
                width = 2.dp,
                color = if (isConnected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isConnected) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .pointerInput(isConnected, config) {
                    if (!isConnected) return@pointerInput
                    detectTransformGestures(panZoomLock = false) { _, pan, zoom, rotation ->
                        if (pan.x != 0f || pan.y != 0f) {
                            onPan(pan.x.toDouble(), pan.y.toDouble())
                        }
                        if (zoom != 1f) {
                            onZoom(zoom.toDouble())
                        }
                        if (rotation != 0f) {
                            onRotate(rotation.toDouble())
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.TouchApp,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = if (isConnected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isConnected) "Gesture Trackpad Surface" else "Disconnected — Gestures Disabled",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isConnected) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isConnected) "1-Finger: Pan | 2-Finger: Pinch/Zoom/Rotate"
                    else "Connect to companion to manipulate canvas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            if (isConnected) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            onTapUndo()
                            if (config.hapticsEnabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                            .testTag("gesture_undo_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Undo,
                            contentDescription = "Undo",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = {
                            onTapRedo()
                            if (config.hapticsEnabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                            .testTag("gesture_redo_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Redo,
                            contentDescription = "Redo",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ZoomControllerSection(
    isConnected: Boolean,
    viewModel: ControlsViewModel,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var sliderValue by remember { mutableStateOf(1.0f) }

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Zoom Controller",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${(sliderValue * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Slider(
                value = sliderValue,
                onValueChange = { newValue ->
                    if (isConnected) {
                        val factor = newValue / sliderValue
                        viewModel.onZoom(factor.toDouble())
                    }
                    sliderValue = newValue
                },
                valueRange = 0.2f..4.0f,
                enabled = isConnected,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("zoom_slider")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.zoomOut()
                        sliderValue = (sliderValue * 0.8f).coerceAtLeast(0.2f)
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    enabled = isConnected,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("zoom_out_btn")
                ) {
                    Icon(Icons.Rounded.Remove, contentDescription = "Zoom Out")
                }

                Button(
                    onClick = {
                        viewModel.zoomIn()
                        sliderValue = (sliderValue * 1.25f).coerceAtMost(4.0f)
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    enabled = isConnected,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("zoom_in_btn")
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Zoom In")
                }

                Button(
                    onClick = {
                        viewModel.resetZoom()
                        sliderValue = 1.0f
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    enabled = isConnected,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("reset_zoom_btn")
                ) {
                    Text("100%")
                }

                Button(
                    onClick = {
                        viewModel.fitCanvas()
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    enabled = isConnected,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("fit_canvas_btn")
                ) {
                    Icon(Icons.Rounded.AspectRatio, contentDescription = "Fit Canvas")
                }
            }
        }
    }
}

@Composable
fun CanvasControlsSection(
    isConnected: Boolean,
    viewModel: ControlsViewModel,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Canvas Actions",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.resetView()
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    enabled = isConnected,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("reset_view_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Rounded.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("Reset View")
                    }
                }

                Button(
                    onClick = {
                        viewModel.rotateLeft()
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    enabled = isConnected,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("rotate_left_btn")
                ) {
                    Icon(Icons.Rounded.RotateLeft, contentDescription = "Rotate Left -90°")
                }

                Button(
                    onClick = {
                        viewModel.rotateRight()
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    enabled = isConnected,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("rotate_right_btn")
                ) {
                    Icon(Icons.Rounded.RotateRight, contentDescription = "Rotate Right +90°")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.resetRotation()
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    enabled = isConnected,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("reset_rotation_btn")
                ) {
                    Text("0° Rotation")
                }

                Button(
                    onClick = {
                        viewModel.flipHorizontal()
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    enabled = isConnected,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("flip_horizontal_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Rounded.Flip, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("Flip H")
                    }
                }

                Button(
                    onClick = {
                        viewModel.flipVertical()
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    enabled = isConnected,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("flip_vertical_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Rounded.SwapVert, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("Flip V")
                    }
                }
            }
        }
    }
}

@Composable
fun GestureConfigDialog(
    config: CreativeControlsConfig,
    onDismiss: () -> Unit,
    onSave: (CreativeControlsConfig) -> Unit
) {
    var panSens by remember { mutableStateOf(config.panSensitivity) }
    var zoomSens by remember { mutableStateOf(config.zoomSensitivity) }
    var rotSens by remember { mutableStateOf(config.rotationSensitivity) }
    var invPanX by remember { mutableStateOf(config.invertPanX) }
    var invPanY by remember { mutableStateOf(config.invertPanY) }
    var invZoom by remember { mutableStateOf(config.invertZoom) }
    var invRot by remember { mutableStateOf(config.invertRotation) }
    var haptics by remember { mutableStateOf(config.hapticsEnabled) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("gesture_settings_dialog"),
        title = { Text("Creative Controls Settings") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Pan Sensitivity: ${"%.1f".format(panSens)}x", style = MaterialTheme.typography.bodySmall)
                Slider(value = panSens, onValueChange = { panSens = it }, valueRange = 0.2f..3.0f)

                Text(text = "Zoom Sensitivity: ${"%.1f".format(zoomSens)}x", style = MaterialTheme.typography.bodySmall)
                Slider(value = zoomSens, onValueChange = { zoomSens = it }, valueRange = 0.2f..3.0f)

                Text(text = "Rotation Sensitivity: ${"%.1f".format(rotSens)}x", style = MaterialTheme.typography.bodySmall)
                Slider(value = rotSens, onValueChange = { rotSens = it }, valueRange = 0.2f..3.0f)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Invert Pan X / Y")
                    Row {
                        Switch(checked = invPanX, onCheckedChange = { invPanX = it })
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(checked = invPanY, onCheckedChange = { invPanY = it })
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Invert Zoom / Rotation")
                    Row {
                        Switch(checked = invZoom, onCheckedChange = { invZoom = it })
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(checked = invRot, onCheckedChange = { invRot = it })
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Haptic Feedback")
                    Switch(checked = haptics, onCheckedChange = { haptics = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        config.copy(
                            panSensitivity = panSens,
                            zoomSensitivity = zoomSens,
                            rotationSensitivity = rotSens,
                            invertPanX = invPanX,
                            invertPanY = invPanY,
                            invertZoom = invZoom,
                            invertRotation = invRot,
                            hapticsEnabled = haptics
                        )
                    )
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
