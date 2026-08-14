package com.onimeno.onicanvas.feature.workspace.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Gesture
import androidx.compose.material.icons.rounded.GridOn
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onimeno.onicanvas.feature.controls.state.CreativeControlsConfig
import com.onimeno.onicanvas.feature.controls.state.GestureBinding
import com.onimeno.onicanvas.feature.controls.state.GestureType
import com.onimeno.onicanvas.feature.workspace.state.MacroAction
import com.onimeno.onicanvas.feature.workspace.state.MacroButton
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceActionLibrary
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceColorPalette
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceIconLibrary
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceItem
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceTheme

/**
 * Stateless customization surface used by the workspace editor.
 * Provides controls for grid size, themes, icons, button customization, and gesture settings.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkspaceCustomizationPanel(
    workspace: WorkspaceItem,
    onGridSizeChanged: (Int) -> Unit,
    onThemeSelected: (String) -> Unit,
    onIconSelected: (String) -> Unit,
    onAddButton: (String, String, MacroAction) -> Unit,
    onRemoveButton: (String, String) -> Unit,
    onMoveButton: (String, Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    onAccentColorSelected: ((String?) -> Unit)? = null,
    onEditButton: ((String, MacroButton) -> Unit)? = null,
    onCreativeControlsConfigChanged: ((CreativeControlsConfig) -> Unit)? = null
) {
    val page = workspace.macroPages.minByOrNull { it.orderIndex }
    var buttonToEdit by remember { mutableStateOf<MacroButton?>(null) }
    var showAddCustomDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.testTag("workspace_customization_panel"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Grid Size Section
        item {
            CustomizationCard(
                title = "Grid Layout (${workspace.gridSize}×${workspace.gridSize})",
                icon = Icons.Rounded.GridOn
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Capacity: ${workspace.gridSize * workspace.gridSize} buttons total",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        (2..6).forEach { size ->
                            val isSelected = workspace.gridSize == size
                            OutlinedButton(
                                onClick = { onGridSizeChanged(size) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("custom_grid_size_$size"),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                ),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${size}×$size",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Workspace Theme Section
        item {
            CustomizationCard(
                title = "Workspace Visual Theme",
                icon = Icons.Rounded.Palette
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Select an atmospheric color profile for your workspace.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        WorkspaceTheme.entries.forEach { theme ->
                            val isSelected = theme.key == workspace.customization.themeKey
                            ThemeBadge(
                                theme = theme,
                                selected = isSelected,
                                onClick = { onThemeSelected(theme.key) }
                            )
                        }
                    }
                }
            }
        }

        // 3. Workspace Icon Section
        item {
            CustomizationCard(
                title = "Workspace Icon",
                icon = Icons.Rounded.ColorLens
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Select an icon identifier for workspace navigation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        WorkspaceIconLibrary.names.forEach { iconName ->
                            val isSelected = iconName == workspace.iconName
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 0.5.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onIconSelected(iconName) }
                                    .testTag("ws_icon_$iconName"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = WorkspaceIconLibrary.getIcon(iconName),
                                    contentDescription = iconName,
                                    modifier = Modifier.size(20.dp),
                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Macro Buttons Management
        if (page != null) {
            val capacity = workspace.gridSize * workspace.gridSize
            val currentCount = page.buttons.size

            item {
                CustomizationCard(
                    title = "Macro Buttons (${currentCount}/$capacity)",
                    icon = Icons.Rounded.DragHandle
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Reorder, edit labels/icons/colors, or delete shortcuts.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (page.buttons.isEmpty()) {
                            Text(
                                text = "No buttons configured. Add actions below.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            page.buttons.sortedBy { it.position }.forEachIndexed { index, button ->
                                val buttonBgColor = WorkspaceColorPalette.parseColor(
                                    button.colorHex,
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                        .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                        .testTag("custom_btn_row_${button.id}"),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(buttonBgColor.copy(alpha = 0.3f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = WorkspaceIconLibrary.getIcon(button.iconName),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = button.label,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Pos ${index + 1} • ${button.action::class.simpleName ?: "Action"}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Move Up
                                    IconButton(
                                        onClick = { if (index > 0) onMoveButton(page.id, index, index - 1) },
                                        enabled = index > 0,
                                        modifier = Modifier.size(32.dp).testTag("move_up_${button.id}")
                                    ) {
                                        Icon(
                                            Icons.Rounded.ArrowUpward,
                                            contentDescription = "Move up",
                                            modifier = Modifier.size(16.dp),
                                            tint = if (index > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                        )
                                    }

                                    // Move Down
                                    IconButton(
                                        onClick = { if (index < page.buttons.lastIndex) onMoveButton(page.id, index, index + 1) },
                                        enabled = index < page.buttons.lastIndex,
                                        modifier = Modifier.size(32.dp).testTag("move_down_${button.id}")
                                    ) {
                                        Icon(
                                            Icons.Rounded.ArrowDownward,
                                            contentDescription = "Move down",
                                            modifier = Modifier.size(16.dp),
                                            tint = if (index < page.buttons.lastIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                        )
                                    }

                                    // Edit
                                    if (onEditButton != null) {
                                        IconButton(
                                            onClick = { buttonToEdit = button },
                                            modifier = Modifier.size(32.dp).testTag("edit_${button.id}")
                                        ) {
                                            Icon(
                                                Icons.Rounded.Edit,
                                                contentDescription = "Edit button",
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                    }

                                    // Remove
                                    IconButton(
                                        onClick = { onRemoveButton(page.id, button.id) },
                                        modifier = Modifier.size(32.dp).testTag("delete_${button.id}")
                                    ) {
                                        Icon(
                                            Icons.Rounded.Delete,
                                            contentDescription = "Remove button",
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }

                        // Add Control Bar
                        if (currentCount < capacity) {
                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { showAddCustomDialog = true },
                                    modifier = Modifier.weight(1f).testTag("add_custom_button_btn"),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Add New Button", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }
            }

            // 5. Quick Action Palette
            if (currentCount < capacity) {
                item {
                    CustomizationCard(
                        title = "Quick Add Presets",
                        icon = Icons.Rounded.Add
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val presetActions = listOf(
                                Triple("Undo", "undo", MacroAction.Undo),
                                Triple("Redo", "redo", MacroAction.Redo),
                                Triple("Save", "save", MacroAction.Save),
                                Triple("Brush", "brush", MacroAction.Brush),
                                Triple("Eraser", "eraser", MacroAction.Eraser),
                                Triple("Fill", "fill", MacroAction.Fill),
                                Triple("Select", "select", MacroAction.Selection),
                                Triple("Transform", "transform", MacroAction.Transform),
                                Triple("Copy", "copy", MacroAction.Copy),
                                Triple("Paste", "paste", MacroAction.Paste)
                            )
                            presetActions.forEach { (label, iconName, action) ->
                                OutlinedButton(
                                    onClick = { onAddButton(label, iconName, action) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("quick_add_$iconName")
                                ) {
                                    Icon(
                                        imageVector = WorkspaceIconLibrary.getIcon(iconName),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(label, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. Gesture & Canvas Controls Settings
        item {
            val config = workspace.creativeControlsConfig
            CustomizationCard(
                title = "Creative Gestures & Sensitivity",
                icon = Icons.Rounded.Gesture
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Fine-tune gesture sensitivity and canvas transformation behaviors.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Zoom Sensitivity Slider
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Zoom Sensitivity", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            Text(String.format("%.1fx", config.zoomSensitivity), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Slider(
                            value = config.zoomSensitivity,
                            onValueChange = {
                                onCreativeControlsConfigChanged?.invoke(config.copy(zoomSensitivity = it))
                            },
                            valueRange = 0.2f..3.0f,
                            modifier = Modifier.fillMaxWidth().testTag("zoom_sensitivity_slider")
                        )
                    }

                    // Pan Sensitivity Slider
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Pan Sensitivity", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            Text(String.format("%.1fx", config.panSensitivity), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Slider(
                            value = config.panSensitivity,
                            onValueChange = {
                                onCreativeControlsConfigChanged?.invoke(config.copy(panSensitivity = it))
                            },
                            valueRange = 0.2f..3.0f,
                            modifier = Modifier.fillMaxWidth().testTag("pan_sensitivity_slider")
                        )
                    }

                    // Rotation Sensitivity Slider
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Rotation Sensitivity", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            Text(String.format("%.1fx", config.rotationSensitivity), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Slider(
                            value = config.rotationSensitivity,
                            onValueChange = {
                                onCreativeControlsConfigChanged?.invoke(config.copy(rotationSensitivity = it))
                            },
                            valueRange = 0.2f..3.0f,
                            modifier = Modifier.fillMaxWidth().testTag("rotation_sensitivity_slider")
                        )
                    }

                    // Haptics & Inversion Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Haptic Feedback on Touches", style = MaterialTheme.typography.bodySmall)
                        Switch(
                            checked = config.hapticsEnabled,
                            onCheckedChange = {
                                onCreativeControlsConfigChanged?.invoke(config.copy(hapticsEnabled = it))
                            },
                            modifier = Modifier.testTag("custom_haptics_switch")
                        )
                    }
                }
            }
        }
    }

    // Edit Button Dialog
    buttonToEdit?.let { btn ->
        if (page != null && onEditButton != null) {
            ButtonEditorDialog(
                button = btn,
                onDismiss = { buttonToEdit = null },
                onSave = { updated ->
                    onEditButton(page.id, updated)
                    buttonToEdit = null
                }
            )
        }
    }

    // Add Custom Button Dialog
    if (showAddCustomDialog && page != null) {
        var label by remember { mutableStateOf("") }
        var selectedIcon by remember { mutableStateOf("tune") }
        var selectedActionType by remember { mutableStateOf("Undo") }
        var shortcutKeys by remember { mutableStateOf("") }
        var selectedColorHex by remember { mutableStateOf<String?>(null) }

        val actionOptions = listOf("Undo", "Redo", "Save", "Brush", "Eraser", "Fill", "Selection", "Transform", "Copy", "Paste", "CustomShortcut")

        AlertDialog(
            onDismissRequest = { showAddCustomDialog = false },
            title = { Text("Add Custom Macro Button", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = label,
                            onValueChange = { label = it },
                            label = { Text("Button Label") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("new_btn_label_input")
                        )
                    }

                    item {
                        Text("Select Action", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            actionOptions.forEach { opt ->
                                val isSel = selectedActionType == opt
                                Text(
                                    text = opt,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { selectedActionType = opt }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    if (selectedActionType == "CustomShortcut") {
                        item {
                            OutlinedTextField(
                                value = shortcutKeys,
                                onValueChange = { shortcutKeys = it },
                                label = { Text("Shortcut Keys (e.g. Ctrl,Alt,Z)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    item {
                        Text("Select Icon", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            WorkspaceIconLibrary.names.take(16).forEach { iconName ->
                                val isSel = selectedIcon == iconName
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { selectedIcon = iconName },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = WorkspaceIconLibrary.getIcon(iconName),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text("Button Color (Optional)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            WorkspaceColorPalette.presetColors.forEach { hex ->
                                val color = WorkspaceColorPalette.parseColor(hex)
                                val isSel = selectedColorHex == hex
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (isSel) 2.5.dp else 0.dp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            shape = CircleShape
                                        )
                                        .clickable { selectedColorHex = if (isSel) null else hex },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSel) {
                                        Icon(Icons.Rounded.Check, null, modifier = Modifier.size(14.dp), tint = Color.Black)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val action = if (selectedActionType == "CustomShortcut") {
                            val tokens = shortcutKeys.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            val modifiers = tokens.filter { it.lowercase() in listOf("ctrl", "alt", "shift", "meta") }
                            val keys = tokens.filter { it.lowercase() !in listOf("ctrl", "alt", "shift", "meta") }
                            MacroAction.CustomShortcut(keys = keys, modifiers = modifiers)
                        } else {
                            when (selectedActionType) {
                                "Undo" -> MacroAction.Undo
                                "Redo" -> MacroAction.Redo
                                "Save" -> MacroAction.Save
                                "Brush" -> MacroAction.Brush
                                "Eraser" -> MacroAction.Eraser
                                "Fill" -> MacroAction.Fill
                                "Selection" -> MacroAction.Selection
                                "Transform" -> MacroAction.Transform
                                "Copy" -> MacroAction.Copy
                                "Paste" -> MacroAction.Paste
                                else -> MacroAction.Undo
                            }
                        }
                        val buttonLabel = label.ifBlank { selectedActionType }
                        onAddButton(buttonLabel, selectedIcon, action)
                        showAddCustomDialog = false
                    },
                    modifier = Modifier.testTag("confirm_add_custom_btn")
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCustomDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun CustomizationCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            content()
        }
    }
}

@Composable
private fun ThemeBadge(
    theme: WorkspaceTheme,
    selected: Boolean,
    onClick: () -> Unit
) {
    val accentColor = runCatching { Color(android.graphics.Color.parseColor(theme.accentHex)) }.getOrDefault(MaterialTheme.colorScheme.primary)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("theme_chip_${theme.key}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(accentColor)
        )
        Text(
            text = theme.displayName,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ButtonEditorDialog(
    button: MacroButton,
    onDismiss: () -> Unit,
    onSave: (MacroButton) -> Unit
) {
    var label by remember { mutableStateOf(button.label) }
    var iconName by remember { mutableStateOf(button.iconName) }
    var colorHex by remember { mutableStateOf(button.colorHex) }
    var repeatEnabled by remember { mutableStateOf(button.repeatEnabled) }
    var enabled by remember { mutableStateOf(button.enabled) }
    var hidden by remember { mutableStateOf(button.hidden) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Button (${button.position + 1})", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = label,
                        onValueChange = { label = it },
                        label = { Text("Label") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_button_label_input")
                    )
                }

                item {
                    Text("Select Icon", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        WorkspaceIconLibrary.names.forEach { name ->
                            val isSel = iconName == name
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { iconName = name }
                                    .testTag("select_icon_$name"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = WorkspaceIconLibrary.getIcon(name),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                item {
                    Text("Button Color Customization", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        WorkspaceColorPalette.presetColors.forEach { hex ->
                            val color = WorkspaceColorPalette.parseColor(hex)
                            val isSel = colorHex == hex
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSel) 2.5.dp else 0.dp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        shape = CircleShape
                                    )
                                    .clickable { colorHex = if (isSel) null else hex }
                                    .testTag("color_swatch_$hex"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSel) {
                                    Icon(Icons.Rounded.Check, null, modifier = Modifier.size(14.dp), tint = Color.Black)
                                }
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = repeatEnabled,
                            onCheckedChange = { repeatEnabled = it },
                            modifier = Modifier.testTag("repeat_check")
                        )
                        Text("Auto-Repeat when held", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = enabled, onCheckedChange = { enabled = it })
                            Text("Active", style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = hidden, onCheckedChange = { hidden = it })
                            Text("Hidden", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        button.copy(
                            label = label.trim().ifBlank { button.label },
                            iconName = iconName,
                            colorHex = colorHex,
                            repeatEnabled = repeatEnabled,
                            enabled = enabled,
                            hidden = hidden
                        )
                    )
                },
                modifier = Modifier.testTag("save_btn_properties")
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
