package com.onimeno.onicanvas.feature.workspace.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.FolderSpecial
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Publish
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.onimeno.onicanvas.OniCanvasApp
import com.onimeno.onicanvas.core.designsystem.components.OniCard
import com.onimeno.onicanvas.core.designsystem.components.OniEmptyState
import com.onimeno.onicanvas.core.designsystem.components.OniSectionHeader
import com.onimeno.onicanvas.core.designsystem.components.OniTopBar
import com.onimeno.onicanvas.core.designsystem.theme.LocalSpacing
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceItem
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceUiState
import com.onimeno.onicanvas.feature.workspace.viewmodel.WorkspaceViewModel
import com.onimeno.onicanvas.feature.workspace.viewmodel.WorkspaceViewModelFactory

@Composable
fun WorkspaceScreen(
    onWorkspaceClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WorkspaceViewModel = viewModel(
        factory = WorkspaceViewModelFactory(
            (LocalContext.current.applicationContext as OniCanvasApp).container.workspaceRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    var showCreateDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            OniTopBar(
                title = "Workspaces",
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                    ) {
                        IconButton(onClick = { showImportDialog = true }, modifier = Modifier.testTag("import_workspace_btn")) {
                            Icon(Icons.Rounded.Publish, contentDescription = "Import Workspace", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { showCreateDialog = true }, modifier = Modifier.testTag("create_workspace_btn")) {
                            Icon(Icons.Rounded.Add, contentDescription = "Create Workspace", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(innerPadding)
        ) {
            when (val state = uiState) {
                WorkspaceUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
                is WorkspaceUiState.Success -> WorkspaceListContent(
                    state = state,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    onToggleFavoritesOnly = viewModel::toggleFavoritesOnly,
                    onToggleFavorite = viewModel::toggleFavorite,
                    onWorkspaceClick = onWorkspaceClick
                )
                is WorkspaceUiState.Error -> OniEmptyState(
                    title = "Something went wrong",
                    description = state.message,
                    icon = Icons.Rounded.Category,
                    actionText = "Retry",
                    onActionClick = viewModel::loadWorkspaces
                )
            }
            if (showCreateDialog) CreateWorkspaceDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { name, app -> viewModel.createWorkspace(name, app); showCreateDialog = false }
            )
            if (showImportDialog) ImportWorkspaceDialog(
                onDismiss = { showImportDialog = false },
                onConfirm = { name, app -> viewModel.importWorkspace(name, app); showImportDialog = false }
            )
        }
    }
}

@Composable
fun WorkspaceListContent(
    state: WorkspaceUiState.Success,
    onSearchQueryChange: (String) -> Unit,
    onToggleFavoritesOnly: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onWorkspaceClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val focusManager = LocalFocusManager.current
    Column(modifier = modifier.fillMaxSize().testTag("workspace_screen_container")) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.medium, vertical = spacing.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search layouts...") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Rounded.Clear, contentDescription = "Clear search")
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                modifier = Modifier.weight(1f).testTag("workspace_search_input")
            )
            IconButton(
                onClick = onToggleFavoritesOnly,
                modifier = Modifier.background(
                    if (state.showFavoritesOnly) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                    CircleShape
                ).testTag("workspace_favorite_filter_toggle")
            ) {
                Icon(
                    if (state.showFavoritesOnly) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = "Filter favorites",
                    tint = if (state.showFavoritesOnly) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (state.workspaces.isEmpty()) {
            Spacer(Modifier.weight(1f))
            OniEmptyState("No workspaces found", "Try adjusting your search query or clear the favorites filter.", Icons.Rounded.Category)
            Spacer(Modifier.weight(1.3f))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(spacing.medium),
                verticalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                item { OniSectionHeader(if (state.showFavoritesOnly) "Favorite Layouts" else "All Layouts") }
                items(state.workspaces, key = { it.id }) { workspace ->
                    WorkspaceRowItem(
                        workspace = workspace,
                        onToggleFavorite = { onToggleFavorite(workspace.id) },
                        onClick = { onWorkspaceClick(workspace.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun WorkspaceRowItem(
    workspace: WorkspaceItem,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val icon = when (workspace.iconName) {
        "brush" -> Icons.Rounded.Brush
        "book" -> Icons.Rounded.Book
        "palette" -> Icons.Rounded.Palette
        "folder_special" -> Icons.Rounded.FolderSpecial
        else -> Icons.Rounded.Category
    }
    OniCard(modifier = modifier.fillMaxWidth().testTag("workspace_item_${workspace.id}"), onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(Modifier.size(44.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape), Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(spacing.medium))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(workspace.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.width(spacing.small))
                        Text(workspace.targetApp.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(workspace.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text("${workspace.buttonCount} MACROS • Active ${workspace.lastUsed}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(if (workspace.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder, contentDescription = "Toggle favorite", tint = if (workspace.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun CreateWorkspaceDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var targetApp by remember { mutableStateOf("Clip Studio Paint") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Workspace", fontWeight = FontWeight.Bold) },
        text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(name, { name = it }, label = { Text("Workspace Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(targetApp, { targetApp = it }, label = { Text("Target Software") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        } },
        confirmButton = { TextButton(onClick = { if (name.isNotBlank()) onConfirm(name, targetApp) }, enabled = name.isNotBlank()) { Text("Create") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun ImportWorkspaceDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var name by remember { mutableStateOf("Photoshop Detailing") }
    var targetApp by remember { mutableStateOf("Photoshop") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Import Workspace", fontWeight = FontWeight.Bold) },
        text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Select a file to import from. Standard .onicanvas formats will automatically map shortcuts.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedTextField(name, { name = it }, label = { Text("Workspace Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(targetApp, { targetApp = it }, label = { Text("Target Software") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        } },
        confirmButton = { TextButton(onClick = { if (name.isNotBlank()) onConfirm(name, targetApp) }, enabled = name.isNotBlank()) { Text("Import") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
