package com.onimeno.onicanvas.feature.workspace.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Gesture
import androidx.compose.material.icons.rounded.GridOn
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Redo
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ToggleOff
import androidx.compose.material.icons.rounded.ToggleOn
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.onimeno.onicanvas.core.designsystem.components.OniButton
import com.onimeno.onicanvas.core.designsystem.components.OniCard
import com.onimeno.onicanvas.core.designsystem.components.OniEmptyState
import com.onimeno.onicanvas.core.designsystem.components.OniSectionHeader
import com.onimeno.onicanvas.core.designsystem.components.OniTopBar
import com.onimeno.onicanvas.core.designsystem.theme.GlassCardShape
import com.onimeno.onicanvas.core.designsystem.theme.LocalSpacing
import com.onimeno.onicanvas.feature.workspace.state.ControlModule
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceEditorUiState
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceItem
import com.onimeno.onicanvas.feature.workspace.viewmodel.WorkspaceEditorViewModel

@Composable
fun WorkspaceEditorScreen(
    workspaceId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WorkspaceEditorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val spacing = LocalSpacing.current
    var showUnsavedWarning by remember { mutableStateOf(false) }

    // Load workspace data when screen is entered
    LaunchedEffect(workspaceId) {
        viewModel.loadWorkspace(workspaceId)
    }

    // Intercept back actions to warn if dirty
    val handleBack = {
        val state = uiState as? WorkspaceEditorUiState.Success
        if (state != null && state.isDirty) {
            showUnsavedWarning = true
        } else {
            onBackClick()
        }
    }

    BackHandler(onBack = handleBack)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            val titleText = when (val state = uiState) {
                is WorkspaceEditorUiState.Success -> "Edit: ${state.editingWorkspace.name}"
                else -> "Workspace Editor"
            }
            OniTopBar(
                title = titleText,
                navigationIcon = {
                    IconButton(onClick = handleBack, modifier = Modifier.testTag("editor_back_btn")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    val state = uiState as? WorkspaceEditorUiState.Success
                    if (state != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                        ) {
                            IconButton(
                                onClick = { viewModel.undo() },
                                enabled = state.canUndo,
                                modifier = Modifier.testTag("editor_undo_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Undo,
                                    contentDescription = "Undo",
                                    tint = if (state.canUndo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                )
                            }
                            IconButton(
                                onClick = { viewModel.redo() },
                                enabled = state.canRedo,
                                modifier = Modifier.testTag("editor_redo_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Redo,
                                    contentDescription = "Redo",
                                    tint = if (state.canRedo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                )
                            }
                            IconButton(
                                onClick = { viewModel.toggleFavorite() },
                                modifier = Modifier.testTag("editor_favorite_btn")
                            ) {
                                Icon(
                                    imageVector = if (state.editingWorkspace.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                    contentDescription = "Toggle favorite",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(
                                onClick = { viewModel.save() },
                                enabled = state.isDirty,
                                modifier = Modifier.testTag("editor_save_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Save,
                                    contentDescription = "Save workspace",
                                    tint = if (state.isDirty) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is WorkspaceEditorUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is WorkspaceEditorUiState.Success -> {
                    EditorLayout(
                        state = state,
                        viewModel = viewModel,
                        onBackClick = onBackClick
                    )
                }
                is WorkspaceEditorUiState.Error -> {
                    OniEmptyState(
                        title = "Editor Load Failed",
                        description = state.message,
                        icon = Icons.Rounded.Category,
                        actionText = "Go Back",
                        onActionClick = onBackClick
                    )
                }
            }

            // Dialogs
            val successState = uiState as? WorkspaceEditorUiState.Success
            if (successState != null) {
                if (successState.showRenameDialog) {
                    RenameDetailsDialog(
                        workspace = successState.editingWorkspace,
                        onDismiss = { viewModel.showRenameDialog(false) },
                        onConfirm = { name, app, desc ->
                            viewModel.updateDetails(name, app, desc)
                            viewModel.showRenameDialog(false)
                        }
                    )
                }

                if (successState.showSaveAsDialog) {
                    SaveAsDialog(
                        defaultName = "${successState.editingWorkspace.name} Copy",
                        onDismiss = { viewModel.showSaveAsDialog(false) },
                        onConfirm = { newName ->
                            viewModel.saveAs(newName)
                            viewModel.showSaveAsDialog(false)
                        }
                    )
                }

                if (successState.showDeleteConfirmDialog) {
                    DeleteConfirmDialog(
                        name = successState.editingWorkspace.name,
                        onDismiss = { viewModel.showDeleteConfirmDialog(false) },
                        onConfirm = {
                            viewModel.delete {
                                viewModel.showDeleteConfirmDialog(false)
                                onBackClick()
                            }
                        }
                    )
                }

                if (showUnsavedWarning) {
                    UnsavedWarningDialog(
                        onDismiss = { showUnsavedWarning = false },
                        onDiscard = {
                            viewModel.revert()
                            showUnsavedWarning = false
                            onBackClick()
                        },
                        onSave = {
                            viewModel.save()
                            showUnsavedWarning = false
                            onBackClick()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EditorLayout(
    state: WorkspaceEditorUiState.Success,
    viewModel: WorkspaceEditorViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 720.dp

        if (isWideScreen) {
            // Adaptive design: Side-by-side splitscreen on wide monitors/tablets
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                // Left Panel: Workspace Controls
                Column(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(spacing.medium)
                ) {
                    WorkspaceDetailsCard(
                        workspace = state.editingWorkspace,
                        isDirty = state.isDirty,
                        onEditClick = { viewModel.showRenameDialog(true) },
                        onSaveAsClick = { viewModel.showSaveAsDialog(true) },
                        onRevertClick = { viewModel.revert() },
                        onDuplicateClick = { viewModel.duplicate() },
                        onDeleteClick = { viewModel.showDeleteConfirmDialog(true) }
                    )

                    OniSectionHeader(title = "Control Modules Config")

                    ModulesConfigurationList(
                        enabledModules = state.editingWorkspace.enabledModules,
                        onToggleModule = { viewModel.toggleModule(it) },
                        onMoveUp = { viewModel.moveModuleUp(it) },
                        onMoveDown = { viewModel.moveModuleDown(it) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Right Panel: Live Visual Preview
                Column(
                    modifier = Modifier
                        .weight(0.8f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(spacing.medium)
                ) {
                    OniSectionHeader(title = "Live Companion Preview")
                    LivePreviewPanel(
                        enabledModules = state.editingWorkspace.enabledModules,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        } else {
            // Stacked design on compact/mobile phones
            var activeTab by remember { mutableStateOf(0) } // 0 = Config, 1 = Preview

            Column(modifier = Modifier.fillMaxSize()) {
                // Inline Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(vertical = spacing.small),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "CONFIGURATION",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (activeTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { activeTab = 0 }
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .testTag("tab_config")
                    )
                    Text(
                        text = "LIVE PREVIEW (${state.editingWorkspace.enabledModules.size})",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (activeTab == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { activeTab = 1 }
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .testTag("tab_preview")
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(spacing.medium)
                ) {
                    if (activeTab == 0) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(spacing.medium),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                WorkspaceDetailsCard(
                                    workspace = state.editingWorkspace,
                                    isDirty = state.isDirty,
                                    onEditClick = { viewModel.showRenameDialog(true) },
                                    onSaveAsClick = { viewModel.showSaveAsDialog(true) },
                                    onRevertClick = { viewModel.revert() },
                                    onDuplicateClick = { viewModel.duplicate() },
                                    onDeleteClick = { viewModel.showDeleteConfirmDialog(true) }
                                )
                            }
                            item {
                                OniSectionHeader(title = "Control Modules Config")
                            }
                            item {
                                // Nested column inside single-view scrolling layout for mobile compliance
                                Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                                    val inactiveModules = ControlModule.values().filter { !state.editingWorkspace.enabledModules.contains(it) }

                                    // Active list
                                    state.editingWorkspace.enabledModules.forEachIndexed { index, module ->
                                        ModuleConfigRowItem(
                                            module = module,
                                            isEnabled = true,
                                            isFirst = index == 0,
                                            isLast = index == state.editingWorkspace.enabledModules.lastIndex,
                                            onToggle = { viewModel.toggleModule(module) },
                                            onMoveUp = { viewModel.moveModuleUp(index) },
                                            onMoveDown = { viewModel.moveModuleDown(index) }
                                        )
                                    }

                                    if (inactiveModules.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(spacing.small))
                                        Text(
                                            text = "INACTIVE MODULES (TAP TO ENABLE)",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        inactiveModules.forEach { module ->
                                            ModuleConfigRowItem(
                                                module = module,
                                                isEnabled = false,
                                                isFirst = false,
                                                isLast = false,
                                                onToggle = { viewModel.toggleModule(module) },
                                                onMoveUp = {},
                                                onMoveDown = {}
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        LivePreviewPanel(
                            enabledModules = state.editingWorkspace.enabledModules,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WorkspaceDetailsCard(
    workspace: WorkspaceItem,
    isDirty: Boolean,
    onEditClick: () -> Unit,
    onSaveAsClick: () -> Unit,
    onRevertClick: () -> Unit,
    onDuplicateClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    OniCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("workspace_details_card")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = workspace.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (isDirty) {
                            Spacer(modifier = Modifier.width(spacing.small))
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                        shape = CircleShape
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "UNSAVED",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Text(
                        text = "Software: ${workspace.targetApp}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                IconButton(onClick = onEditClick, modifier = Modifier.testTag("edit_details_btn")) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Edit workspace details",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = workspace.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(spacing.extraSmall))

            // Grid of Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                OutlinedButton(
                    onClick = onSaveAsClick,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("details_save_as_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Rounded.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save As", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onDuplicateClick,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("details_duplicate_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Rounded.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Duplicate", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                OutlinedButton(
                    onClick = onRevertClick,
                    enabled = isDirty,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("details_revert_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Rounded.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Revert", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onDeleteClick,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("details_delete_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(imageVector = Icons.Rounded.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ModulesConfigurationList(
    enabledModules: List<ControlModule>,
    onToggleModule: (ControlModule) -> Unit,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val allModules = ControlModule.values()
    val inactiveModules = allModules.filter { !enabledModules.contains(it) }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .testTag("editor_modules_config_container"),
        verticalArrangement = Arrangement.spacedBy(spacing.small),
        contentPadding = PaddingValues(bottom = spacing.medium)
    ) {
        item {
            Text(
                text = "ACTIVE MODULES (ORDERABLE)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (enabledModules.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                ) {
                    Box(modifier = Modifier.padding(spacing.medium), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No modules active. Enable modules below.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            itemsIndexed(enabledModules, key = { _, module -> "active_${module.name}" }) { index, module ->
                ModuleConfigRowItem(
                    module = module,
                    isEnabled = true,
                    isFirst = index == 0,
                    isLast = index == enabledModules.lastIndex,
                    onToggle = { onToggleModule(module) },
                    onMoveUp = { onMoveUp(index) },
                    onMoveDown = { onMoveDown(index) }
                )
            }
        }

        if (inactiveModules.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(spacing.small))
                Text(
                    text = "AVAILABLE MODULES",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            itemsIndexed(inactiveModules, key = { _, module -> "inactive_${module.name}" }) { _, module ->
                ModuleConfigRowItem(
                    module = module,
                    isEnabled = false,
                    isFirst = false,
                    isLast = false,
                    onToggle = { onToggleModule(module) },
                    onMoveUp = {},
                    onMoveDown = {}
                )
            }
        }
    }
}

@Composable
fun ModuleConfigRowItem(
    module: ControlModule,
    isEnabled: Boolean,
    isFirst: Boolean,
    isLast: Boolean,
    onToggle: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val icon = when (module) {
        ControlModule.MACRO_PAD -> Icons.Rounded.Keyboard
        ControlModule.GESTURE_PAD -> Icons.Rounded.Gesture
        ControlModule.RADIAL_MENU -> Icons.Rounded.Category
        ControlModule.SHORTCUT_GRID -> Icons.Rounded.GridOn
        ControlModule.BRUSH_CONTROLS -> Icons.Rounded.Tune
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isEnabled) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
            )
            .border(
                width = 1.dp,
                color = if (isEnabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = spacing.medium, vertical = spacing.small)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = if (isEnabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(spacing.medium))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = module.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = module.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isEnabled) {
                    IconButton(onClick = onMoveUp, enabled = !isFirst, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowUpward,
                            contentDescription = "Move module up",
                            tint = if (!isFirst) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = onMoveDown, enabled = !isLast, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowDownward,
                            contentDescription = "Move module down",
                            tint = if (!isLast) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = onToggle, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = if (isEnabled) Icons.Rounded.ToggleOn else Icons.Rounded.ToggleOff,
                        contentDescription = "Toggle module status",
                        tint = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LivePreviewPanel(
    enabledModules: List<ControlModule>,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(spacing.medium)
            .testTag("preview_panel")
    ) {
        if (enabledModules.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.Palette,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(spacing.small))
                Text(
                    text = "Live Preview Screen Empty",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Enable control modules from the configuration panel to preview their layout.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = spacing.medium)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(spacing.medium),
                contentPadding = PaddingValues(vertical = spacing.small)
            ) {
                item {
                    Text(
                        text = "TABLET LANDSCAPE VIEWPORT",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                itemsIndexed(enabledModules) { _, module ->
                    PreviewModuleWidget(module = module)
                }
            }
        }
    }
}

@Composable
fun PreviewModuleWidget(
    module: ControlModule,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("preview_widget_${module.name}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(spacing.medium)) {
            // Widget Title Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                val icon = when (module) {
                    ControlModule.MACRO_PAD -> Icons.Rounded.Keyboard
                    ControlModule.GESTURE_PAD -> Icons.Rounded.Gesture
                    ControlModule.RADIAL_MENU -> Icons.Rounded.Category
                    ControlModule.SHORTCUT_GRID -> Icons.Rounded.GridOn
                    ControlModule.BRUSH_CONTROLS -> Icons.Rounded.Tune
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = module.displayName.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(spacing.small))

            // Widget Content Render based on modules
            when (module) {
                ControlModule.MACRO_PAD -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Undo", "Redo", "Brush+", "Brush-").forEach { label ->
                            var isClicked by remember { mutableStateOf(false) }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (isClicked) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                    )
                                    .clickable { isClicked = !isClicked }
                                    .border(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                ControlModule.GESTURE_PAD -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing.small)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Gesture,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Touch gesture pad. Swipe to rotate, Pinch to zoom.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                ControlModule.RADIAL_MENU -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Category,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(spacing.medium))
                        Column {
                            Text(
                                text = "Stylus Radial overlay active",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Tap and drag to access submenus",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                ControlModule.SHORTCUT_GRID -> {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("B", "E", "Z").forEach { key ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(28.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                        .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = key,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("Shift", "Ctrl", "Alt").forEach { key ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(28.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                        .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = key,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                }
                ControlModule.BRUSH_CONTROLS -> {
                    var brushSize by remember { mutableStateOf(45f) }
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Brush Size (${brushSize.toInt()}px)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Slider(
                            value = brushSize,
                            onValueChange = { brushSize = it },
                            valueRange = 1f..100f,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RenameDetailsDialog(
    workspace: WorkspaceItem,
    onDismiss: () -> Unit,
    onConfirm: (name: String, app: String, desc: String) -> Unit
) {
    var name by remember { mutableStateOf(workspace.name) }
    var targetApp by remember { mutableStateOf(workspace.targetApp) }
    var description by remember { mutableStateOf(workspace.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Workspace Details", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Workspace Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetApp,
                    onValueChange = { targetApp = it },
                    label = { Text("Target Software") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name, targetApp, description) },
                enabled = name.isNotBlank()
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SaveAsDialog(
    defaultName: String,
    onDismiss: () -> Unit,
    onConfirm: (newName: String) -> Unit
) {
    var name by remember { mutableStateOf(defaultName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save Workspace As...", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Create a copy of this workspace with a new name.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("New Workspace Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name) },
                enabled = name.isNotBlank()
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

@Composable
fun DeleteConfirmDialog(
    name: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Workspace?", fontWeight = FontWeight.Bold) },
        text = {
            Text("Are you sure you want to delete '$name'? This action is permanent and cannot be undone.")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun UnsavedWarningDialog(
    onDismiss: () -> Unit,
    onDiscard: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Unsaved Changes", fontWeight = FontWeight.Bold) },
        text = {
            Text("You have unsaved edits in this workspace. Would you like to save them before exiting?")
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDiscard,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Discard")
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                TextButton(onClick = onSave) {
                    Text("Save & Exit")
                }
            }
        }
    )
}
