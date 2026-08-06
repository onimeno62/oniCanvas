package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.MacroButton
import com.example.ui.components.MacroButtonView
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.PrimaryIndigo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MacrosScreen(
    buttons: List<MacroButton>,
    onTriggerAction: (String, String) -> Unit,
    onAddButton: (String, String, String, Int, Int, Int) -> Unit,
    onDeleteButton: (Int) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editMode by remember { mutableStateOf(false) }

    var labelInput by remember { mutableStateOf("") }
    var shortcutInput by remember { mutableStateOf("") }
    var colorInput by remember { mutableStateOf("#6366F1") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080710))
            .padding(16.dp)
    ) {
        // Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Macro Pad",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Clip Studio Paint Shortcuts",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Toggle Edit Mode
                TextButton(
                    onClick = { editMode = !editMode },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (editMode) Color(0xFFEF4444) else AccentCyan
                    )
                ) {
                    Text(text = if (editMode) "Done" else "Edit")
                }

                IconButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier
                        .background(PrimaryIndigo, RoundedCornerShape(8.dp))
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Custom Macro",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (buttons.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No shortcuts in this workspace",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                    ) {
                        Text(text = "Add Custom Shortcut")
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(buttons) { btn ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        MacroButtonView(
                            button = btn,
                            onClick = {
                                if (!editMode) {
                                    onTriggerAction(btn.label, btn.actionShortcut)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // If in editMode, display overlay to delete
                        if (editMode) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.TopEnd)
                                    .clip(RoundedCornerShape(bottomStart = 8.dp))
                                    .background(Color(0xFFEF4444))
                                    .clickable { onDeleteButton(btn.id) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Delete",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Custom Button Pop-up Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(text = "Add Custom Shortcut", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = labelInput,
                        onValueChange = { labelInput = it },
                        label = { Text(text = "Button Label (e.g., Eraser)") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1F1D36),
                            unfocusedContainerColor = Color(0xFF131124),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextField(
                        value = shortcutInput,
                        onValueChange = { shortcutInput = it },
                        label = { Text(text = "Keyboard Shortcut (e.g., Ctrl+Z)") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1F1D36),
                            unfocusedContainerColor = Color(0xFF131124),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Color palette selector
                    Text(text = "Pick Button Highlight Glow:", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    val highlightColors = listOf("#6366F1", "#8B5CF6", "#06B6D4", "#F59E0B", "#10B981", "#EF4444")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        highlightColors.forEach { col ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(android.graphics.Color.parseColor(col)))
                                    .border(
                                        width = if (colorInput == col) 2.dp else 0.dp,
                                        color = Color.White,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable { colorInput = col }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (labelInput.isNotEmpty() && shortcutInput.isNotEmpty()) {
                            // Find an available row & column simply (append to end)
                            val nextRow = buttons.size / 3
                            val nextCol = buttons.size % 3
                            onAddButton(labelInput, shortcutInput, colorInput, 0, nextRow, nextCol)
                            showAddDialog = false
                            labelInput = ""
                            shortcutInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                ) {
                    Text(text = "Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(text = "Cancel", color = Color.White)
                }
            },
            containerColor = Color(0xFF131124),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
        )
    }
}
