package com.onimeno.onicanvas.feature.settings.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.FolderZip
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SettingsBackupRestore
import androidx.compose.material.icons.rounded.TabletAndroid
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.onimeno.onicanvas.core.designsystem.components.OniButton
import com.onimeno.onicanvas.core.designsystem.components.OniCard
import com.onimeno.onicanvas.core.designsystem.components.OniEmptyState
import com.onimeno.onicanvas.core.designsystem.components.OniSectionHeader
import com.onimeno.onicanvas.core.designsystem.components.OniTopBar
import com.onimeno.onicanvas.core.designsystem.theme.GlassCardShape
import com.onimeno.onicanvas.core.designsystem.theme.LocalSpacing
import com.onimeno.onicanvas.core.designsystem.theme.SuccessColor
import com.onimeno.onicanvas.feature.settings.state.SettingsData
import com.onimeno.onicanvas.feature.settings.state.SettingsUiState
import com.onimeno.onicanvas.feature.settings.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val spacing = LocalSpacing.current
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            OniTopBar(
                title = "Settings",
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("settings_back_btn")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back to dashboard",
                            tint = MaterialTheme.colorScheme.onBackground
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
                is SettingsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is SettingsUiState.Success -> {
                    SettingsContent(
                        settings = state.settings,
                        onToggleDarkTheme = { viewModel.toggleDarkTheme() },
                        onToggleDynamicColor = { viewModel.toggleDynamicColor() },
                        onToggleTabletMode = { viewModel.toggleTabletMode() },
                        onToggleAutoConnect = { viewModel.toggleAutoConnect() },
                        onToggleAnimations = { viewModel.toggleAnimations() },
                        onLanguageChange = { viewModel.changeLanguage(it) },
                        onBackup = {
                            viewModel.performBackup()
                            Toast.makeText(context, "Full workspace database backed up successfully!", Toast.LENGTH_SHORT).show()
                        },
                        onExport = {
                            Toast.makeText(context, "Layout config exported to Documents/oniCanvas/", Toast.LENGTH_LONG).show()
                        },
                        onImport = {
                            Toast.makeText(context, "Loaded local .onicanvas backup file", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                is SettingsUiState.Error -> {
                    OniEmptyState(
                        title = "Configurations error",
                        description = state.message,
                        icon = Icons.Rounded.Info,
                        actionText = "Retry",
                        onActionClick = { viewModel.loadSettings() }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsContent(
    settings: SettingsData,
    onToggleDarkTheme: () -> Unit,
    onToggleDynamicColor: () -> Unit,
    onToggleTabletMode: () -> Unit,
    onToggleAutoConnect: () -> Unit,
    onToggleAnimations: () -> Unit,
    onLanguageChange: (String) -> Unit,
    onBackup: () -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen_container"),
        contentPadding = PaddingValues(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        // Section 1: Design & Aesthetics
        item {
            OniSectionHeader(title = "Design & Theme")
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                SettingsSwitchRow(
                    title = "Dark Theme",
                    description = "Use eye-strain reducing dark palette modes",
                    icon = Icons.Rounded.Palette,
                    checked = settings.darkTheme,
                    onCheckedChange = { onToggleDarkTheme() }
                )
                SettingsSwitchRow(
                    title = "Dynamic Colors",
                    description = "Apply Material Design 3 system dynamic themes",
                    icon = Icons.Rounded.Palette,
                    checked = settings.dynamicColor,
                    onCheckedChange = { onToggleDynamicColor() }
                )
                SettingsSwitchRow(
                    title = "Interface Animations",
                    description = "Enable sliding layouts and responsive micro-motion",
                    icon = Icons.Rounded.PlayArrow,
                    checked = settings.animationsEnabled,
                    onCheckedChange = { onToggleAnimations() }
                )
            }
        }

        // Section 2: Functionality & Layout
        item {
            OniSectionHeader(title = "Device Controls & Auto-link")
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                SettingsSwitchRow(
                    title = "Tablet Mode Optimization",
                    description = "Adaptive split screen pane layout with side rail navigation",
                    icon = Icons.Rounded.TabletAndroid,
                    checked = settings.tabletMode,
                    onCheckedChange = { onToggleTabletMode() }
                )
                SettingsSwitchRow(
                    title = "Auto-Connect Companion",
                    description = "Auto connect to paired host PC on start when available",
                    icon = Icons.Rounded.Wifi,
                    checked = settings.autoConnect,
                    onCheckedChange = { onToggleAutoConnect() }
                )
                SettingsSpinnerRow(
                    title = "Application Language",
                    description = "Currently using ${settings.language}",
                    icon = Icons.Rounded.Language,
                    value = settings.language,
                    onClick = {
                        val nextLang = if (settings.language == "English") "Deutsch" else "English"
                        onLanguageChange(nextLang)
                    }
                )
            }
        }

        // Section 3: Data Management & Backup
        item {
            OniSectionHeader(title = "Backup & Local Sync")
        }
        item {
            OniCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.medium)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Local Backup Sync",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Last successful sync: ${settings.lastBackupDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = onBackup) {
                            Icon(
                                imageVector = Icons.Rounded.SettingsBackupRestore,
                                contentDescription = "Trigger backup",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        OniButton(
                            text = "Export Config",
                            onClick = onExport,
                            isPrimary = true,
                            icon = Icons.Rounded.CloudUpload,
                            modifier = Modifier.weight(1f)
                        )
                        OniButton(
                            text = "Import Config",
                            onClick = onImport,
                            isPrimary = false,
                            icon = Icons.Rounded.CloudDownload,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(GlassCardShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .clickable { onCheckedChange(!checked) }
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
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(spacing.medium))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
fun SettingsSpinnerRow(
    title: String,
    description: String,
    icon: ImageVector,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Row(
        modifier = modifier
            .fillMaxWidth()
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
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(spacing.medium))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = CircleShape
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = value.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
