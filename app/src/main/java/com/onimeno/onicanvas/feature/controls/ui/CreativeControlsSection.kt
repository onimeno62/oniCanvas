package com.onimeno.onicanvas.feature.controls.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AspectRatio
import androidx.compose.material.icons.rounded.ArrowDropDown
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.onimeno.onicanvas.feature.controls.state.GestureAction
import com.onimeno.onicanvas.feature.controls.state.GestureBinding
import com.onimeno.onicanvas.feature.controls.state.GestureType
import com.onimeno.onicanvas.feature.controls.viewmodel.ControlsViewModel

@Composable
fun CreativeControlsSection(
    state: ControlsUiState.Success,
    viewModel: ControlsViewModel,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    var showSettingsDialog by remember { mutableStateOf(false) }

    androidx.compose.foundation.layout.BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val isTablet = maxWidth >= 720.dp

        if (isTablet) {
            Column(
                modifier = Modifier.fillMaxWidth(),
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.medium)
                ) {
                    // Left Column: Gesture Pad Surface (larger touch area)
                    Box(modifier = Modifier.weight(1.1f)) {
                        GesturePadSurface(
                            isConnected = state.isConnected,
                            config = state.activeWorkspace.creativeControlsConfig,
                            viewModel = viewModel,
                            modifier = Modifier.height(340.dp)
                        )
                    }

                    // Right Column: Zoom & Canvas Actions
                    Column(
                        modifier = Modifier.weight(0.9f),
                        verticalArrangement = Arrangement.spacedBy(spacing.medium)
                    ) {
                        ZoomControllerSection(
                            isConnected = state.isConnected,
                            config = state.activeWorkspace.creativeControlsConfig,
                            zoomSliderValue = state.zoomSliderValue,
                            viewModel = viewModel
                        )
                        CanvasControlsSection(
                            isConnected = state.isConnected,
                            config = state.activeWorkspace.creativeControlsConfig,
                            viewModel = viewModel
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
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
                    viewModel = viewModel
                )

                // 2. Zoom Controller Section
                ZoomControllerSection(
                    isConnected = state.isConnected,
                    config = state.activeWorkspace.creativeControlsConfig,
                    zoomSliderValue = state.zoomSliderValue,
                    viewModel = viewModel
                )

                // 3. Canvas Action Controls
                CanvasControlsSection(
                    isConnected = state.isConnected,
                    config = state.activeWorkspace.creativeControlsConfig,
                    viewModel = viewModel
                )
            }
        }
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
    viewModel: ControlsViewModel,
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
                    detectCreativeCanvasGestures(
                        onOneFingerPan = { dx, dy ->
                            viewModel.onOneFingerPan(dx.toDouble(), dy.toDouble())
                        },
                        onTwoFingerPan = { dx, dy ->
                            viewModel.onTwoFingerPan(dx.toDouble(), dy.toDouble())
                        },
                        onPinchZoom = { factor ->
                            viewModel.onPinchZoom(factor.toDouble())
                        },
                        onRotate = { angle ->
                            viewModel.onRotate(angle.toDouble())
                        },
                        onTwoFingerTap = {
                            viewModel.onTwoFingerTap()
                            if (config.hapticsEnabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        },
                        onThreeFingerTap = {
                            viewModel.onThreeFingerTap()
                            if (config.hapticsEnabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        },
                        onGestureEnd = {
                            viewModel.onGestureEnd()
                        }
                    )
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
                    text = if (isConnected) "1-Finger: Pan | 2-Finger: Pinch/Zoom/Rotate/Tap Undo | 3-Finger Tap: Redo"
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
                            viewModel.onTapUndo()
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
                            viewModel.onTapRedo()
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
    config: CreativeControlsConfig,
    zoomSliderValue: Float,
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
                    text = "${(zoomSliderValue * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Slider(
                value = zoomSliderValue,
                onValueChange = { newValue ->
                    viewModel.setZoomSliderValue(newValue)
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
                        if (config.hapticsEnabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
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
                        if (config.hapticsEnabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
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
                        if (config.hapticsEnabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
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
                        if (config.hapticsEnabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
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
    config: CreativeControlsConfig,
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
                        if (config.hapticsEnabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
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
                        if (config.hapticsEnabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
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
                        if (config.hapticsEnabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
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
                        if (config.hapticsEnabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
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
                        if (config.hapticsEnabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
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
                        if (config.hapticsEnabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
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
    val normalizedConfig = remember(config) { config.normalized() }
    var panSens by remember { mutableStateOf(normalizedConfig.panSensitivity) }
    var zoomSens by remember { mutableStateOf(normalizedConfig.zoomSensitivity) }
    var rotSens by remember { mutableStateOf(normalizedConfig.rotationSensitivity) }
    var invPanX by remember { mutableStateOf(normalizedConfig.invertPanX) }
    var invPanY by remember { mutableStateOf(normalizedConfig.invertPanY) }
    var invZoom by remember { mutableStateOf(normalizedConfig.invertZoom) }
    var invRot by remember { mutableStateOf(normalizedConfig.invertRotation) }
    var haptics by remember { mutableStateOf(normalizedConfig.hapticsEnabled) }

    var bindings by remember { mutableStateOf(normalizedConfig.gestureBindings) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("gesture_settings_dialog"),
        title = { Text("Creative Controls Settings") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section: Gesture Bindings Configuration
                Text(
                    text = "GESTURE BINDINGS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                bindings.forEachIndexed { index, binding ->
                    GestureBindingItem(
                        binding = binding,
                        onBindingChange = { updated ->
                            bindings = bindings.toMutableList().also { it[index] = updated }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Section: Global Canvas Controls Settings
                Text(
                    text = "GLOBAL SENSITIVITY & INVERSION",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Master Pan Sensitivity: ${"%.1f".format(panSens)}x",
                    style = MaterialTheme.typography.bodySmall
                )
                Slider(
                    value = panSens,
                    onValueChange = { panSens = it },
                    valueRange = 0.2f..3.0f,
                    modifier = Modifier.testTag("dialog_pan_sens_slider")
                )

                Text(
                    text = "Master Zoom Sensitivity: ${"%.1f".format(zoomSens)}x",
                    style = MaterialTheme.typography.bodySmall
                )
                Slider(
                    value = zoomSens,
                    onValueChange = { zoomSens = it },
                    valueRange = 0.2f..3.0f,
                    modifier = Modifier.testTag("dialog_zoom_sens_slider")
                )

                Text(
                    text = "Master Rotation Sensitivity: ${"%.1f".format(rotSens)}x",
                    style = MaterialTheme.typography.bodySmall
                )
                Slider(
                    value = rotSens,
                    onValueChange = { rotSens = it },
                    valueRange = 0.2f..3.0f,
                    modifier = Modifier.testTag("dialog_rot_sens_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Invert Pan X / Y")
                    Row {
                        Switch(checked = invPanX, onCheckedChange = { invPanX = it }, modifier = Modifier.testTag("dialog_inv_pan_x_switch"))
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(checked = invPanY, onCheckedChange = { invPanY = it }, modifier = Modifier.testTag("dialog_inv_pan_y_switch"))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Invert Zoom / Rotation")
                    Row {
                        Switch(checked = invZoom, onCheckedChange = { invZoom = it }, modifier = Modifier.testTag("dialog_inv_zoom_switch"))
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(checked = invRot, onCheckedChange = { invRot = it }, modifier = Modifier.testTag("dialog_inv_rot_switch"))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Haptic Feedback")
                    Switch(checked = haptics, onCheckedChange = { haptics = it }, modifier = Modifier.testTag("dialog_haptics_switch"))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        CreativeControlsConfig(
                            gestureBindings = bindings,
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
                },
                modifier = Modifier.testTag("dialog_save_btn")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.testTag("dialog_cancel_btn")) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun GestureBindingItem(
    binding: GestureBinding,
    onBindingChange: (GestureBinding) -> Unit
) {
    var expandedDropdown by remember { mutableStateOf(false) }
    val isContinuous = binding.gestureType in listOf(
        GestureType.ONE_FINGER_PAN,
        GestureType.TWO_FINGER_PAN,
        GestureType.PINCH_ZOOM,
        GestureType.ROTATE_CANVAS
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = binding.gestureType.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Switch(
                    checked = binding.enabled,
                    onCheckedChange = { onBindingChange(binding.copy(enabled = it)) },
                    modifier = Modifier.testTag("gesture_switch_${binding.gestureType.name.lowercase()}")
                )
            }

            if (binding.enabled) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Action:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Box {
                        Row(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { expandedDropdown = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = binding.action.displayName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Rounded.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false }
                        ) {
                            GestureAction.entries.forEach { action ->
                                DropdownMenuItem(
                                    text = { Text(action.displayName) },
                                    onClick = {
                                        onBindingChange(binding.copy(action = action))
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                if (isContinuous) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Sensitivity",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${"%.1f".format(binding.sensitivity)}x",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = binding.sensitivity,
                            onValueChange = { onBindingChange(binding.copy(sensitivity = it)) },
                            valueRange = 0.2f..3.0f
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Invert Direction",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Switch(
                            checked = binding.isInverted,
                            onCheckedChange = { onBindingChange(binding.copy(isInverted = it)) }
                        )
                    }
                }
            }
        }
    }
}

