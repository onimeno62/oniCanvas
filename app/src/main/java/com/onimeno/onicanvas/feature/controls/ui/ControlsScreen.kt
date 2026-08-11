package com.onimeno.onicanvas.feature.controls.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.ArrowLeft
import androidx.compose.material.icons.rounded.ArrowRight
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FormatColorFill
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Redo
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SmartButton
import androidx.compose.material.icons.rounded.Transform
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.onimeno.onicanvas.core.designsystem.components.OniButton
import com.onimeno.onicanvas.core.designsystem.components.OniCard
import com.onimeno.onicanvas.core.designsystem.components.OniEmptyState
import com.onimeno.onicanvas.core.designsystem.components.OniTopBar
import com.onimeno.onicanvas.core.designsystem.theme.LocalSpacing
import com.onimeno.onicanvas.feature.controls.state.ControlsUiState
import com.onimeno.onicanvas.feature.controls.viewmodel.ControlsViewModel
import com.onimeno.onicanvas.feature.workspace.state.MacroAction
import com.onimeno.onicanvas.feature.workspace.state.MacroButton
import com.onimeno.onicanvas.feature.workspace.state.MacroPage
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlsScreen(
    modifier: Modifier = Modifier,
    viewModel: ControlsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isEditMode by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            OniTopBar(
                title = if (isEditMode) "Configure Macro Pad" else "Macro Pad",
                actions = {
                    IconButton(
                        onClick = { isEditMode = !isEditMode },
                        modifier = Modifier.testTag("toggle_edit_mode")
                    ) {
                        Icon(
                            imageVector = if (isEditMode) Icons.Rounded.Save else Icons.Rounded.Edit,
                            contentDescription = if (isEditMode) "Save Configuration" else "Edit Configuration",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                ControlsUiState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
                is ControlsUiState.Success -> {
                    MacroPadContent(
                        state = state,
                        isEditMode = isEditMode,
                        viewModel = viewModel
                    )
                }
                is ControlsUiState.Error -> {
                    OniEmptyState(
                        title = "Failed to load layouts",
                        description = state.message,
                        icon = Icons.Rounded.Category,
                        actionText = "Retry",
                        onActionClick = {}
                    )
                }
            }
        }
    }
}

@Composable
fun MacroPadContent(
    state: ControlsUiState.Success,
    isEditMode: Boolean,
    viewModel: ControlsViewModel,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val activeWS = state.activeWorkspace
    val activePage = activeWS.macroPages.find { it.id == state.activePageId } ?: activeWS.macroPages.firstOrNull()

    var showAddPageDialog by remember { mutableStateOf(false) }
    var pageToRename by remember { mutableStateOf<MacroPage?>(null) }
    var buttonToCustomize by remember { mutableStateOf<MacroButton?>(null) }
    var showWorkspaceDropdown by remember { mutableStateOf(false) }
    var selectedMode by remember { mutableStateOf("MACRO_PAD") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("controls_screen_container"),
        contentPadding = PaddingValues(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        // Connection Info Banner
        item {
            ConnectionBanner(
                isConnected = state.isConnected,
                connectionType = state.connectionType,
                hostName = state.activeHostName
            )
        }

        // Mode Switcher Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = { selectedMode = "MACRO_PAD" },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("controls_mode_macro_pad"),
                    colors = if (selectedMode == "MACRO_PAD") {
                        androidx.compose.material3.ButtonDefaults.buttonColors()
                    } else {
                        androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Transparent
                        )
                    }
                ) {
                    Text("Macro Pad")
                }

                Button(
                    onClick = { selectedMode = "CREATIVE_CONTROLS" },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("controls_mode_creative"),
                    colors = if (selectedMode == "CREATIVE_CONTROLS") {
                        androidx.compose.material3.ButtonDefaults.buttonColors()
                    } else {
                        androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Transparent
                        )
                    }
                ) {
                    Text("Creative Controls")
                }
            }
        }

        if (selectedMode == "CREATIVE_CONTROLS") {
            item {
                CreativeControlsSection(
                    state = state,
                    viewModel = viewModel
                )
            }
        } else {
            // Workspace Selection Row
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "ACTIVE WORKSPACE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedCard(
                        onClick = { showWorkspaceDropdown = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("workspace_selector_card"),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = activeWS.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = activeWS.targetApp,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = "Select Workspace")
                        }
                    }

                    DropdownMenu(
                        expanded = showWorkspaceDropdown,
                        onDismissRequest = { showWorkspaceDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        state.availableWorkspaces.forEach { ws ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(ws.name, fontWeight = FontWeight.Bold)
                                        Text(ws.targetApp, style = MaterialTheme.typography.bodySmall)
                                    }
                                },
                                onClick = {
                                    viewModel.setActiveWorkspace(ws.id)
                                    showWorkspaceDropdown = false
                                },
                                modifier = Modifier.testTag("ws_item_${ws.id}")
                            )
                        }
                    }
                }
            }
        }

        // Grid Size Selection (Only shown in Edit Mode)
        if (isEditMode) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "GRID LAYOUT COMPLEXITY",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(2, 3, 4, 5).forEach { size ->
                            val isSelected = activeWS.gridSize == size
                            Card(
                                onClick = { viewModel.updateGridSize(size) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("grid_size_$size"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${size}x${size}",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Page Navigation and Actions
        if (activePage != null) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PAGES & TABS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        if (isEditMode) {
                            IconButton(
                                onClick = { showAddPageDialog = true },
                                modifier = Modifier
                                    .size(24.dp)
                                    .testTag("add_page_btn")
                            ) {
                                Icon(Icons.Rounded.Add, contentDescription = "Add Page")
                            }
                        }
                    }

                    // Pages tabs row
                    ScrollableTabRow(
                        selectedTabIndex = activeWS.macroPages.indexOfFirst { it.id == activePage.id }.coerceAtLeast(0),
                        edgePadding = 0.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        activeWS.macroPages.forEach { page ->
                            Tab(
                                selected = page.id == activePage.id,
                                onClick = { viewModel.selectPage(page.id) },
                                modifier = Modifier.testTag("tab_${page.id}"),
                                text = {
                                    Text(
                                        text = page.name,
                                        fontWeight = if (page.id == activePage.id) FontWeight.Bold else FontWeight.Normal,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            )
                        }
                    }

                    // Reorder, Delete, Rename (only in edit mode)
                    if (isEditMode) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Page Actions: ",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.width(4.dp))
                                IconButton(
                                    onClick = { pageToRename = activePage },
                                    modifier = Modifier.size(32.dp).testTag("rename_page_btn")
                                ) {
                                    Icon(
                                        Icons.Rounded.Edit,
                                        contentDescription = "Rename current page",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.deletePage(activePage.id) },
                                    enabled = activeWS.macroPages.size > 1,
                                    modifier = Modifier.size(32.dp).testTag("delete_page_btn")
                                ) {
                                    Icon(
                                        Icons.Rounded.Delete,
                                        contentDescription = "Delete current page",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Row {
                                IconButton(
                                    onClick = { viewModel.reorderPageUp(activePage.id) },
                                    modifier = Modifier.size(32.dp).testTag("move_page_up_btn")
                                ) {
                                    Icon(
                                        Icons.Rounded.ArrowLeft,
                                        contentDescription = "Move page left",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.reorderPageDown(activePage.id) },
                                    modifier = Modifier.size(32.dp).testTag("move_page_down_btn")
                                ) {
                                    Icon(
                                        Icons.Rounded.ArrowRight,
                                        contentDescription = "Move page right",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Interactive Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "GRID SURFACE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    ButtonGrid(
                        page = activePage,
                        gridSize = activeWS.gridSize,
                        isEditMode = isEditMode,
                        isConnected = state.isConnected,
                        onButtonTap = { btn ->
                            if (isEditMode) {
                                buttonToCustomize = btn
                            } else if (state.isConnected) {
                                viewModel.triggerAction(btn.action)
                            }
                        },
                        onButtonPressStart = { btn ->
                            if (!isEditMode && state.isConnected && btn.repeatEnabled) {
                                viewModel.startRepeatAction(btn.action)
                            }
                        },
                        onButtonPressEnd = { btn ->
                            if (!isEditMode && state.isConnected && btn.repeatEnabled) {
                                viewModel.stopRepeatAction()
                            }
                        },
                        onButtonLongPress = { btn ->
                            if (!isEditMode && state.isConnected && btn.longPressAction != null) {
                                viewModel.triggerAction(btn.longPressAction)
                            }
                        }
                    )
                }
            }
        }
    }
    }

    // Add Page Dialog
    if (showAddPageDialog) {
        var newPageName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddPageDialog = false },
            title = { Text("Add Page", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newPageName,
                    onValueChange = { newPageName = it },
                    label = { Text("Page Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_page_input")
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newPageName.isNotBlank()) {
                            viewModel.addPage(newPageName)
                        }
                        showAddPageDialog = false
                    },
                    modifier = Modifier.testTag("add_page_confirm")
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPageDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Rename Page Dialog
    pageToRename?.let { page ->
        var newName by remember { mutableStateOf(page.name) }
        AlertDialog(
            onDismissRequest = { pageToRename = null },
            title = { Text("Rename Page", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("New Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("rename_page_input")
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newName.isNotBlank()) {
                            viewModel.renamePage(page.id, newName)
                        }
                        pageToRename = null
                    },
                    modifier = Modifier.testTag("rename_page_confirm")
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { pageToRename = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Customization Dialog
    buttonToCustomize?.let { btn ->
        if (activePage != null) {
            ButtonCustomizerDialog(
                button = btn,
                onDismiss = { buttonToCustomize = null },
                onSave = { updated ->
                    viewModel.updateButton(activePage.id, updated)
                    buttonToCustomize = null
                }
            )
        }
    }
}

@Composable
fun ConnectionBanner(
    isConnected: Boolean,
    connectionType: String,
    hostName: String?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("connection_banner"),
        colors = CardDefaults.cardColors(
            containerColor = if (isConnected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
            }
        ),
        border = BorderStroke(
            1.dp,
            if (isConnected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isConnected) Icons.Rounded.Wifi else Icons.Rounded.WifiOff,
                contentDescription = null,
                tint = if (isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = if (isConnected) "Connected to Windows Host" else "Disconnected from Companion",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isConnected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                )
                if (isConnected) {
                    Text(
                        text = "Host: ${hostName ?: "Unknown"} • Mode: $connectionType",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                } else {
                    Text(
                        text = "Launch companion & link over local WiFi/USB",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun ButtonGrid(
    page: MacroPage,
    gridSize: Int,
    isEditMode: Boolean,
    isConnected: Boolean,
    onButtonTap: (MacroButton) -> Unit,
    onButtonPressStart: (MacroButton) -> Unit,
    onButtonPressEnd: (MacroButton) -> Unit,
    onButtonLongPress: (MacroButton) -> Unit,
    modifier: Modifier = Modifier
) {
    val maxButtons = gridSize * gridSize
    val sortedButtons = (0 until maxButtons).map { pos ->
        page.buttons.find { it.position == pos } ?: MacroButton(
            id = "${page.id}_btn_${pos}",
            position = pos,
            label = "Empty",
            iconName = "smart_button",
            action = MacroAction.Undo,
            enabled = false
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(gridSize),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .testTag("macro_grid"),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sortedButtons, key = { it.id }) { btn ->
            MacroGridButton(
                button = btn,
                isEditMode = isEditMode,
                isConnected = isConnected,
                onTap = { onButtonTap(btn) },
                onPressStart = { onButtonPressStart(btn) },
                onPressEnd = { onButtonPressEnd(btn) },
                onLongPress = { onButtonLongPress(btn) }
            )
        }
    }
}

@Composable
fun MacroGridButton(
    button: MacroButton,
    isEditMode: Boolean,
    isConnected: Boolean,
    onTap: () -> Unit,
    onPressStart: () -> Unit,
    onPressEnd: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAvailable = button.enabled && !button.hidden
    val visualAlpha = if (isAvailable) 1f else 0.4f
    val isClickable = isEditMode || (isConnected && isAvailable)

    val cardBorder = if (button.hidden) {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    } else {
        null
    }

    val cardColor = when {
        button.hidden -> MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
        !button.enabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
    }

    Card(
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .alpha(visualAlpha)
            .border(
                width = if (isEditMode) 1.dp else 0.dp,
                color = if (isEditMode) MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .pointerInput(isClickable) {
                if (isClickable) {
                    detectTapGestures(
                        onLongPress = { if (!isEditMode) onLongPress() },
                        onTap = { onTap() },
                        onPress = {
                            try {
                                onPressStart()
                                awaitRelease()
                            } finally {
                                onPressEnd()
                            }
                        }
                    )
                }
            }
            .testTag("macro_button_${button.position}"),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = cardBorder
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(4.dp)
            ) {
                Icon(
                    imageVector = getMacroIcon(button.iconName),
                    contentDescription = null,
                    tint = if (isAvailable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = button.label,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isAvailable) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isEditMode) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Icon(
                        Icons.Rounded.Edit,
                        contentDescription = "Edit button",
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ButtonCustomizerDialog(
    button: MacroButton,
    onDismiss: () -> Unit,
    onSave: (MacroButton) -> Unit
) {
    var label by remember { mutableStateOf(button.label) }
    var iconName by remember { mutableStateOf(button.iconName) }
    var actionType by remember { mutableStateOf(getActionTypeString(button.action)) }
    var shortcutKeysText by remember { mutableStateOf(getShortcutKeysString(button.action)) }
    var longPressActionType by remember { mutableStateOf(button.longPressAction?.let { getActionTypeString(it) } ?: "None") }
    var repeatEnabled by remember { mutableStateOf(button.repeatEnabled) }
    var enabled by remember { mutableStateOf(button.enabled) }
    var hidden by remember { mutableStateOf(button.hidden) }

    val actionOptions = listOf("Undo", "Redo", "Save", "Brush", "Eraser", "Fill", "Selection", "Transform", "Copy", "Paste", "CustomShortcut")
    val longPressOptions = listOf("None", "Undo", "Redo", "Save", "Brush", "Eraser", "Fill", "Selection", "Transform", "Copy", "Paste")
    val iconOptions = listOf("undo", "redo", "save", "brush", "eraser", "fill", "select", "transform", "copy", "paste", "shortcut")

    var showActionMenu by remember { mutableStateOf(false) }
    var showLongPressMenu by remember { mutableStateOf(false) }
    var showIconMenu by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("customizer_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Customize Button ${button.position + 1}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Label Text
                item {
                    OutlinedTextField(
                        value = label,
                        onValueChange = { label = it },
                        label = { Text("Label") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("customizer_label")
                    )
                }

                // Icon Selection
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Button Icon", style = MaterialTheme.typography.labelSmall)
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedCard(
                                onClick = { showIconMenu = true },
                                modifier = Modifier.fillMaxWidth().testTag("customizer_icon_selector")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(getMacroIcon(iconName), null, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(iconName.uppercase())
                                    Spacer(Modifier.weight(1f))
                                    Icon(Icons.Rounded.KeyboardArrowDown, null)
                                }
                            }
                            DropdownMenu(
                                expanded = showIconMenu,
                                onDismissRequest = { showIconMenu = false }
                            ) {
                                iconOptions.forEach { name ->
                                    DropdownMenuItem(
                                        leadingIcon = { Icon(getMacroIcon(name), null, modifier = Modifier.size(16.dp)) },
                                        text = { Text(name.uppercase()) },
                                        onClick = {
                                            iconName = name
                                            showIconMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Primary Action Selection
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Trigger Action", style = MaterialTheme.typography.labelSmall)
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedCard(
                                onClick = { showActionMenu = true },
                                modifier = Modifier.fillMaxWidth().testTag("customizer_action_selector")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(actionType)
                                    Spacer(Modifier.weight(1f))
                                    Icon(Icons.Rounded.KeyboardArrowDown, null)
                                }
                            }
                            DropdownMenu(
                                expanded = showActionMenu,
                                onDismissRequest = { showActionMenu = false }
                            ) {
                                actionOptions.forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text(opt) },
                                        onClick = {
                                            actionType = opt
                                            showActionMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Custom Shortcut Textbox
                if (actionType == "CustomShortcut") {
                    item {
                        OutlinedTextField(
                            value = shortcutKeysText,
                            onValueChange = { shortcutKeysText = it },
                            label = { Text("Keys (comma separated, e.g. Ctrl,Alt,Z)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("customizer_shortcut_input")
                        )
                    }
                }

                // Long Press Action
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Long Press Action", style = MaterialTheme.typography.labelSmall)
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedCard(
                                onClick = { showLongPressMenu = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(longPressActionType)
                                    Spacer(Modifier.weight(1f))
                                    Icon(Icons.Rounded.KeyboardArrowDown, null)
                                }
                            }
                            DropdownMenu(
                                expanded = showLongPressMenu,
                                onDismissRequest = { showLongPressMenu = false }
                            ) {
                                longPressOptions.forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text(opt) },
                                        onClick = {
                                            longPressActionType = opt
                                            showLongPressMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Repeat held action check
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = repeatEnabled,
                            onCheckedChange = { repeatEnabled = it },
                            modifier = Modifier.testTag("customizer_repeat_check")
                        )
                        Text("Auto-Repeat command when held down", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                // Enabled and Hidden checkboxes
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

                // Confirm and Cancel buttons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val primAction = parseActionString(actionType, shortcutKeysText)
                                val longAction = if (longPressActionType == "None") null else parseActionString(longPressActionType, "")
                                val updated = button.copy(
                                    label = label,
                                    iconName = iconName,
                                    action = primAction,
                                    longPressAction = longAction,
                                    repeatEnabled = repeatEnabled,
                                    enabled = enabled,
                                    hidden = hidden
                                )
                                onSave(updated)
                            },
                            modifier = Modifier.testTag("customizer_save_btn")
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

// Helpers
fun getMacroIcon(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "undo" -> Icons.Rounded.Undo
        "redo" -> Icons.Rounded.Redo
        "save" -> Icons.Rounded.Save
        "brush" -> Icons.Rounded.Brush
        "eraser" -> Icons.Rounded.CleaningServices
        "fill" -> Icons.Rounded.FormatColorFill
        "select" -> Icons.Rounded.SelectAll
        "transform" -> Icons.Rounded.Transform
        "copy" -> Icons.Rounded.ContentCopy
        "paste" -> Icons.Rounded.ContentPaste
        "shortcut" -> Icons.Rounded.Keyboard
        else -> Icons.Rounded.SmartButton
    }
}

fun getActionTypeString(action: MacroAction): String {
    return when (action) {
        MacroAction.Undo -> "Undo"
        MacroAction.Redo -> "Redo"
        MacroAction.Save -> "Save"
        MacroAction.Brush -> "Brush"
        MacroAction.Eraser -> "Eraser"
        MacroAction.Fill -> "Fill"
        MacroAction.Selection -> "Selection"
        MacroAction.Transform -> "Transform"
        MacroAction.Copy -> "Copy"
        MacroAction.Paste -> "Paste"
        is MacroAction.CustomShortcut -> "CustomShortcut"
    }
}

fun getShortcutKeysString(action: MacroAction): String {
    return when (action) {
        is MacroAction.CustomShortcut -> (action.modifiers + action.keys).joinToString(",")
        else -> ""
    }
}

fun parseActionString(type: String, keysText: String): MacroAction {
    return when (type) {
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
        else -> {
            val tokens = keysText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            val modifiers = tokens.filter { it.lowercase() in listOf("ctrl", "alt", "shift", "meta") }
            val keys = tokens.filter { it.lowercase() notIn listOf("ctrl", "alt", "shift", "meta") }
            MacroAction.CustomShortcut(keys = keys, modifiers = modifiers)
        }
    }
}

private infix fun String.notIn(list: List<String>): Boolean = !list.contains(this)
