package com.onimeno.onicanvas.feature.color.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.onimeno.onicanvas.core.designsystem.theme.GlassCardShape
import com.onimeno.onicanvas.feature.color.model.ColorConversion
import com.onimeno.onicanvas.feature.color.model.ColorModel
import com.onimeno.onicanvas.feature.color.model.ColorPalette
import com.onimeno.onicanvas.feature.color.state.PaletteDialogMode

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PaletteManagerSection(
    palettes: List<ColorPalette>,
    selectedPalette: ColorPalette?,
    selectedHex: String,
    dialogMode: PaletteDialogMode,
    dialogInputText: String,
    onSelectPalette: (String) -> Unit,
    onColorSelected: (ColorModel) -> Unit,
    onAddCurrentColor: () -> Unit,
    onRemoveColor: (paletteId: String, hex: String) -> Unit,
    onReorderColor: (paletteId: String, fromIndex: Int, toIndex: Int) -> Unit,
    onOpenCreateDialog: () -> Unit,
    onOpenRenameDialog: (String) -> Unit,
    onOpenDeleteDialog: (String) -> Unit,
    onDismissDialog: () -> Unit,
    onDialogInputChange: (String) -> Unit,
    onConfirmDialog: () -> Unit,
    onSendPaletteToCompanion: () -> Unit,
    modifier: Modifier = Modifier
) {
    var editingColorsMode by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_palette_manager"),
        shape = GlassCardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header & action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PALETTE MANAGER",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = { editingColorsMode = !editingColorsMode },
                        modifier = Modifier.testTag("btn_toggle_edit_palette")
                    ) {
                        Text(if (editingColorsMode) "Done" else "Edit")
                    }

                    IconButton(
                        onClick = onOpenCreateDialog,
                        modifier = Modifier.testTag("btn_new_palette")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Create new palette",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Palette selection chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                items(palettes, key = { it.id }) { palette ->
                    val isSelected = palette.id == selectedPalette?.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectPalette(palette.id) },
                        label = { Text(palette.name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        modifier = Modifier.testTag("chip_palette_${palette.id}"),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            if (selectedPalette != null) {
                // Active palette management toolbar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selectedPalette.name} (${selectedPalette.colors.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onSendPaletteToCompanion,
                            modifier = Modifier.testTag("btn_sync_palette")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Send,
                                contentDescription = "Send palette to host companion",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = { onOpenRenameDialog(selectedPalette.id) },
                            modifier = Modifier.testTag("btn_rename_palette")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = "Rename palette",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        if (!selectedPalette.isDefault) {
                            IconButton(
                                onClick = { onOpenDeleteDialog(selectedPalette.id) },
                                modifier = Modifier.testTag("btn_delete_palette")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Delete,
                                    contentDescription = "Delete palette",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Swatches grid in FlowRow
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("grid_palette_swatches"),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Swatches
                    selectedPalette.colors.forEachIndexed { index, hex ->
                        val isCurrent = hex.equals(selectedHex, ignoreCase = true)
                        val rgb = ColorConversion.hexToRgb(hex)
                        val composeColor = if (rgb != null) Color(rgb.r, rgb.g, rgb.b) else Color.Gray

                        Box(
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(composeColor)
                                    .border(
                                        width = if (isCurrent) 3.dp else 1.dp,
                                        color = if (isCurrent) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.25f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        val model = ColorConversion.fromHex(hex)
                                        onColorSelected(model)
                                    }
                                    .semantics {
                                        contentDescription = "Palette color $hex${if (isCurrent) ", currently selected" else ""}"
                                    }
                                    .testTag("swatch_palette_$hex")
                            )

                            if (editingColorsMode) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(20.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.Black.copy(alpha = 0.7f))
                                        .clickable { onRemoveColor(selectedPalette.id, hex) }
                                        .testTag("btn_remove_color_$hex"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = "Remove color $hex from palette",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Add color button tile
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                width = 1.5.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { onAddCurrentColor() }
                            .testTag("btn_add_to_palette_tile"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Add active color to palette",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    when (dialogMode) {
        PaletteDialogMode.CREATE -> {
            AlertDialog(
                onDismissRequest = onDismissDialog,
                title = { Text("Create New Palette") },
                text = {
                    OutlinedTextField(
                        value = dialogInputText,
                        onValueChange = onDialogInputChange,
                        label = { Text("Palette Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_palette_name")
                    )
                },
                confirmButton = {
                    Button(
                        onClick = onConfirmDialog,
                        enabled = dialogInputText.isNotBlank(),
                        modifier = Modifier.testTag("btn_confirm_dialog")
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissDialog) {
                        Text("Cancel")
                    }
                }
            )
        }
        PaletteDialogMode.RENAME -> {
            AlertDialog(
                onDismissRequest = onDismissDialog,
                title = { Text("Rename Palette") },
                text = {
                    OutlinedTextField(
                        value = dialogInputText,
                        onValueChange = onDialogInputChange,
                        label = { Text("Palette Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_palette_name")
                    )
                },
                confirmButton = {
                    Button(
                        onClick = onConfirmDialog,
                        enabled = dialogInputText.isNotBlank(),
                        modifier = Modifier.testTag("btn_confirm_dialog")
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissDialog) {
                        Text("Cancel")
                    }
                }
            )
        }
        PaletteDialogMode.DELETE_CONFIRM -> {
            AlertDialog(
                onDismissRequest = onDismissDialog,
                title = { Text("Delete Palette?") },
                text = { Text("Are you sure you want to delete '${selectedPalette?.name}'? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = onConfirmDialog,
                        modifier = Modifier.testTag("btn_confirm_dialog")
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissDialog) {
                        Text("Cancel")
                    }
                }
            )
        }
        PaletteDialogMode.CLEAR_RECENT_CONFIRM -> {
            AlertDialog(
                onDismissRequest = onDismissDialog,
                title = { Text("Clear Recent Colors?") },
                text = { Text("Are you sure you want to clear your recent color history?") },
                confirmButton = {
                    Button(
                        onClick = onConfirmDialog,
                        modifier = Modifier.testTag("btn_confirm_dialog")
                    ) {
                        Text("Clear All")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissDialog) {
                        Text("Cancel")
                    }
                }
            )
        }
        PaletteDialogMode.NONE -> Unit
    }
}
