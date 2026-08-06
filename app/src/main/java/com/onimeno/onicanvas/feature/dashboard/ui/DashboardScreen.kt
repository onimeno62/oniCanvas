package com.onimeno.onicanvas.feature.dashboard.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.onimeno.onicanvas.R
import com.onimeno.onicanvas.core.designsystem.components.OniEmptyState
import com.onimeno.onicanvas.core.designsystem.components.OniSectionHeader
import com.onimeno.onicanvas.core.designsystem.components.OniTopBar
import com.onimeno.onicanvas.core.designsystem.theme.GlassCardShape
import com.onimeno.onicanvas.core.designsystem.theme.LocalSpacing
import com.onimeno.onicanvas.feature.dashboard.state.DashboardUiState
import com.onimeno.onicanvas.feature.dashboard.state.QuickActionType
import com.onimeno.onicanvas.feature.dashboard.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToConnection: () -> Unit,
    onNavigateToControls: () -> Unit,
    onNavigateToWorkspace: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val spacing = LocalSpacing.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            OniTopBar(
                title = stringResource(R.string.app_name),
                actions = {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onNavigateToAbout) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = "About",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            val isTablet = maxWidth >= 600.dp

            AnimatedVisibility(
                visible = uiState is DashboardUiState.Loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp
                    )
                }
            }

            AnimatedVisibility(
                visible = uiState is DashboardUiState.Success,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val successState = uiState as? DashboardUiState.Success
                if (successState != null) {
                    DashboardContent(
                        state = successState,
                        isTablet = isTablet,
                        onNavigateToConnection = onNavigateToConnection,
                        onNavigateToControls = onNavigateToControls,
                        onNavigateToWorkspace = onNavigateToWorkspace,
                        onRefresh = { viewModel.loadDashboardData() }
                    )
                }
            }

            AnimatedVisibility(
                visible = uiState is DashboardUiState.Error,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val errorState = uiState as? DashboardUiState.Error
                OniEmptyState(
                    title = "Failed to load dashboard",
                    description = errorState?.message ?: "An unexpected error occurred",
                    icon = Icons.Rounded.Refresh,
                    actionText = "Retry",
                    onActionClick = { viewModel.loadDashboardData() }
                )
            }
        }
    }
}

@Composable
fun DashboardContent(
    state: DashboardUiState.Success,
    isTablet: Boolean,
    onNavigateToConnection: () -> Unit,
    onNavigateToControls: () -> Unit,
    onNavigateToWorkspace: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    if (isTablet) {
        // Tablet Split Screen Layout
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            // Left Pane: Diagnostics & Connection
            LazyColumn(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                item {
                    OniSectionHeader(
                        title = "System Status",
                        action = {
                            IconButton(onClick = onRefresh) {
                                Icon(
                                    imageVector = Icons.Rounded.Refresh,
                                    contentDescription = "Refresh data",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }
                item {
                    DashboardConnectionCard(
                        deviceName = state.deviceName,
                        connectionType = state.connectionType,
                        status = state.connectionStatus,
                        onConfigureClick = onNavigateToConnection
                    )
                }
                item {
                    DashboardWorkspaceCard(
                        activeWorkspace = state.activeWorkspace,
                        activeSoftware = state.activeSoftware,
                        onConfigureWorkspace = onNavigateToWorkspace
                    )
                }
                item {
                    DashboardDeviceStatus(
                        latencyMs = state.latencyMs,
                        batteryLevel = state.batteryLevel
                    )
                }
            }

            // Right Pane: Controls & Recents
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                item {
                    OniSectionHeader(title = "Quick Controls")
                }
                item {
                    DashboardQuickActions(
                        quickActions = state.quickActions,
                        isTablet = true,
                        onActionClick = { actionType ->
                            when (actionType) {
                                QuickActionType.CONNECT -> onNavigateToConnection()
                                else -> onNavigateToControls()
                            }
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(spacing.small))
                }
                item {
                    OniSectionHeader(
                        title = "Recent Workspaces",
                        action = {
                            Text(
                                text = "VIEW ALL",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.clickable(onClick = onNavigateToWorkspace)
                            )
                        }
                    )
                }
                items(state.recentWorkspaces) { workspace ->
                    RecentWorkspaceRow(
                        workspace = workspace,
                        onClick = onNavigateToWorkspace
                    )
                }
            }
        }
    } else {
        // Phone Layout
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .testTag("dashboard_scroll_content"),
            contentPadding = PaddingValues(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            item {
                OniSectionHeader(
                    title = "System Status",
                    action = {
                        IconButton(onClick = onRefresh, modifier = Modifier.size(24.dp)) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "Refresh connection status",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }
            item {
                DashboardConnectionCard(
                    deviceName = state.deviceName,
                    connectionType = state.connectionType,
                    status = state.connectionStatus,
                    onConfigureClick = onNavigateToConnection
                )
            }
            item {
                DashboardWorkspaceCard(
                    activeWorkspace = state.activeWorkspace,
                    activeSoftware = state.activeSoftware,
                    onConfigureWorkspace = onNavigateToWorkspace
                )
            }
            item {
                DashboardDeviceStatus(
                    latencyMs = state.latencyMs,
                    batteryLevel = state.batteryLevel
                )
            }
            item {
                Spacer(modifier = Modifier.height(spacing.extraSmall))
            }
            item {
                OniSectionHeader(title = "Quick Controls")
            }
            item {
                DashboardQuickActions(
                    quickActions = state.quickActions,
                    isTablet = false,
                    onActionClick = { actionType ->
                        when (actionType) {
                            QuickActionType.CONNECT -> onNavigateToConnection()
                            else -> onNavigateToControls()
                        }
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(spacing.extraSmall))
            }
            item {
                OniSectionHeader(
                    title = "Recent Workspaces",
                    action = {
                        Text(
                            text = "VIEW ALL",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.clickable(onClick = onNavigateToWorkspace)
                        )
                    }
                )
            }
            items(state.recentWorkspaces) { workspace ->
                RecentWorkspaceRow(
                    workspace = workspace,
                    onClick = onNavigateToWorkspace
                )
            }
        }
    }
}

@Composable
fun RecentWorkspaceRow(
    workspace: com.onimeno.onicanvas.feature.dashboard.state.WorkspaceSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val workspaceIcon = when (workspace.iconName) {
        "brush" -> Icons.Rounded.Brush
        "book" -> Icons.Rounded.Book
        else -> Icons.Rounded.Category
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = spacing.extraSmall)
            .clip(GlassCardShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .clickable(onClick = onClick)
            .padding(spacing.medium),
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
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = workspaceIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(spacing.medium))
            Column {
                Text(
                    text = workspace.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = workspace.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "${workspace.buttonCount} MACROS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
