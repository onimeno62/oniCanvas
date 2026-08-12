package com.onimeno.onicanvas.feature.workspace.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.onimeno.onicanvas.feature.workspace.state.MacroAction
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceActionLibrary
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceIconLibrary
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceItem
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceTheme

/** Stateless customization surface used by the workspace editor. */
@Composable
fun WorkspaceCustomizationPanel(
    workspace: WorkspaceItem,
    onGridSizeChanged: (Int) -> Unit,
    onThemeSelected: (String) -> Unit,
    onIconSelected: (String) -> Unit,
    onAddButton: (String, String, MacroAction) -> Unit,
    onRemoveButton: (String, String) -> Unit,
    onMoveButton: (String, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val page = workspace.macroPages.minByOrNull { it.orderIndex }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            CustomizationCard(title = "Grid size", icon = Icons.Rounded.DragHandle) {
                Text("${workspace.gridSize} × ${workspace.gridSize}", style = MaterialTheme.typography.labelLarge)
                Slider(
                    value = workspace.gridSize.toFloat(),
                    onValueChange = { onGridSizeChanged(it.toInt()) },
                    valueRange = 2f..6f,
                    steps = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            CustomizationCard(title = "Workspace theme", icon = Icons.Rounded.Palette) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WorkspaceTheme.values().forEach { theme ->
                        ThemeChip(theme, selected = theme.key == workspace.customization.themeKey) {
                            onThemeSelected(theme.key)
                        }
                    }
                }
            }
        }

        item {
            CustomizationCard(title = "Workspace icon", icon = Icons.Rounded.Palette) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WorkspaceIconLibrary.names.forEach { iconName ->
                        Text(
                            iconName.take(3),
                            modifier = Modifier
                                .clickable { onIconSelected(iconName) }
                                .background(
                                    if (iconName == workspace.iconName) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.shapes.small
                                )
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }

        if (page != null) {
            item {
                CustomizationCard(title = "Macro buttons", icon = Icons.Rounded.DragHandle) {
                    page.buttons.sortedBy { it.position }.forEachIndexed { index, button ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${index + 1}", modifier = Modifier.padding(end = 8.dp))
                            Text(button.label, modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = { if (index > 0) onMoveButton(page.id, index, index - 1) },
                                enabled = index > 0
                            ) { Icon(Icons.Rounded.DragHandle, "Move up") }
                            IconButton(onClick = { onRemoveButton(page.id, button.id) }) {
                                Icon(Icons.Rounded.Delete, "Remove button")
                            }
                        }
                    }
                }
            }

            item {
                CustomizationCard(title = "Add control", icon = Icons.Rounded.Add) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        WorkspaceActionLibrary.actions.take(5).forEachIndexed { index, action ->
                            IconButton(onClick = {
                                onAddButton("Action ${index + 1}", WorkspaceIconLibrary.names[index], action)
                            }) {
                                Icon(Icons.Rounded.Add, "Add ${action::class.simpleName}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomizationCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
                Text(title, modifier = Modifier.padding(start = 8.dp), style = MaterialTheme.typography.titleMedium)
            }
            content()
        }
    }
}

@Composable
private fun ThemeChip(theme: WorkspaceTheme, selected: Boolean, onClick: () -> Unit) {
    Text(
        theme.displayName,
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(
                if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.shapes.small
            )
            .padding(horizontal = 10.dp, vertical = 7.dp),
        style = MaterialTheme.typography.labelSmall
    )
}
