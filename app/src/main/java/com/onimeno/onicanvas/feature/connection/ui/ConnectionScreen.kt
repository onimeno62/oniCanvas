package com.onimeno.onicanvas.feature.connection.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.onimeno.onicanvas.core.designsystem.components.OniButton
import com.onimeno.onicanvas.core.designsystem.components.OniCard
import com.onimeno.onicanvas.core.designsystem.components.OniEmptyState
import com.onimeno.onicanvas.core.designsystem.components.OniSectionHeader
import com.onimeno.onicanvas.core.designsystem.components.OniStatus
import com.onimeno.onicanvas.core.designsystem.components.OniStatusChip
import com.onimeno.onicanvas.core.designsystem.components.OniTopBar
import com.onimeno.onicanvas.core.designsystem.theme.GlassCardShape
import com.onimeno.onicanvas.core.designsystem.theme.LocalSpacing
import com.onimeno.onicanvas.core.designsystem.theme.SuccessColor
import com.onimeno.onicanvas.core.designsystem.theme.WarningColor
import com.onimeno.onicanvas.core.designsystem.theme.ErrorColor
import com.onimeno.onicanvas.feature.connection.state.ConnectionHost
import com.onimeno.onicanvas.feature.connection.state.ConnectionLog
import com.onimeno.onicanvas.feature.connection.state.ConnectionUiState
import com.onimeno.onicanvas.feature.connection.viewmodel.ConnectionViewModel

@Composable
fun ConnectionScreen(
    viewModel: ConnectionViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            OniTopBar(
                title = "Link Connection",
                actions = {
                    IconButton(
                        onClick = viewModel::scanNetwork,
                        modifier = Modifier.testTag("scan_network_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Scan local network",
                            tint = MaterialTheme.colorScheme.primary
                        )
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
                is ConnectionUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is ConnectionUiState.Success -> {
                    ConnectionContent(
                        state = state,
                        onConnect = viewModel::connectToHost,
                        onDisconnect = viewModel::disconnect,
                        onClearLogs = viewModel::clearLogs
                    )
                }
                is ConnectionUiState.Error -> {
                    OniEmptyState(
                        title = "Gateway offline",
                        description = state.message,
                        icon = Icons.Rounded.Wifi,
                        actionText = "Retry",
                        onActionClick = viewModel::scanNetwork
                    )
                }
            }
        }
    }
}

@Composable
fun ConnectionContent(
    state: ConnectionUiState.Success,
    onConnect: (String) -> Unit,
    onDisconnect: () -> Unit,
    onClearLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    LazyColumn(
        modifier = modifier.fillMaxSize().testTag("connection_screen_container"),
        contentPadding = PaddingValues(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        item {
            ActiveConnectionCard(
                state = state,
                onDisconnect = onDisconnect,
                onConnectDefault = { onConnect("studio_pc") }
            )
        }

        item { OniSectionHeader(title = "Discovered PCs (Local Network)") }

        if (state.discoveredHosts.isEmpty()) {
            item {
                Text(
                    text = "No other PC companion services found on this Wi-Fi network.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = spacing.small)
                )
            }
        } else {
            items(state.discoveredHosts, key = { it.id }) { host ->
                HostRowItem(
                    host = host,
                    isActive = state.activeHostName == host.name && state.status == OniStatus.SUCCESS,
                    onClick = { onConnect(host.id) }
                )
            }
        }

        item {
            OniSectionHeader(
                title = "Link Connection Logs",
                action = {
                    IconButton(onClick = onClearLogs, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Clear connection logs",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            )
        }

        item { LogTerminalConsole(logs = state.connectionLogs) }
    }
}

@Composable
fun ActiveConnectionCard(
    state: ConnectionUiState.Success,
    onDisconnect: () -> Unit,
    onConnectDefault: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val isConnected = state.status == OniStatus.SUCCESS

    OniCard(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.medium)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    Icon(
                        imageVector = if (isConnected) Icons.Rounded.Wifi else Icons.Rounded.PowerSettingsNew,
                        contentDescription = null,
                        tint = if (isConnected) SuccessColor else WarningColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Companion Status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                OniStatusChip(
                    status = state.status,
                    label = if (isConnected) "CONNECTED" else "DISCONNECTED"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("ACTIVE HOST", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    Text(state.activeHostName ?: "No host selected", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("IP ADDRESS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    Text(state.hostIp, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("TRANSPORT PROTOCOL", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Rounded.SignalCellularAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                        Text(state.transportType, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("INTERFACE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    Text("WLAN Websocket (8085)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (isConnected) {
                OniButton(
                    text = "Disconnect Link",
                    onClick = onDisconnect,
                    isPrimary = false,
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Rounded.PowerSettingsNew
                )
            } else {
                OniButton(
                    text = "Reconnect last active PC",
                    onClick = onConnectDefault,
                    isPrimary = true,
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Rounded.CheckCircle
                )
            }
        }
    }
}

@Composable
fun HostRowItem(
    host: ConnectionHost,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(GlassCardShape)
            .background(
                if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
            .border(
                width = 1.dp,
                color = if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else Color.Transparent,
                shape = GlassCardShape
            )
            .clickable(onClick = onClick)
            .padding(spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Computer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(spacing.medium))
            Column {
                Text(
                    text = host.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${host.ipAddress} • Available",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Box(
            modifier = Modifier
                .background(
                    color = if (isActive) SuccessColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (isActive) "CONNECTED" else "TAP TO PAIR",
                style = MaterialTheme.typography.labelSmall,
                color = if (isActive) SuccessColor else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LogTerminalConsole(
    logs: List<ConnectionLog>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(Color(0xFF0D0E12))
            .border(1.dp, Color(0xFF1E212B), MaterialTheme.shapes.medium)
            .padding(8.dp)
    ) {
        if (logs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No log inputs recorded",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontFamily = FontFamily.Monospace
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(logs) { log ->
                    val color = when (log.level) {
                        "SUCCESS" -> SuccessColor
                        "WARNING" -> WarningColor
                        "ERROR" -> ErrorColor
                        else -> Color(0xFF8E929E)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                        Text(
                            text = "[${log.timestamp}] ",
                            color = Color(0xFF4C505B),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = log.message,
                            color = color,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}
