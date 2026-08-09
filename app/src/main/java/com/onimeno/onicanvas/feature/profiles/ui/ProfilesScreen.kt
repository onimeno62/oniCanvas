package com.onimeno.onicanvas.feature.profiles.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.FolderSpecial
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.onimeno.onicanvas.OniCanvasApp
import androidx.compose.ui.platform.LocalContext
import com.onimeno.onicanvas.core.designsystem.components.OniButton
import com.onimeno.onicanvas.core.designsystem.components.OniCard
import com.onimeno.onicanvas.core.designsystem.components.OniEmptyState
import com.onimeno.onicanvas.core.designsystem.components.OniSectionHeader
import com.onimeno.onicanvas.core.designsystem.components.OniTopBar
import com.onimeno.onicanvas.core.designsystem.theme.GlassCardShape
import com.onimeno.onicanvas.core.designsystem.theme.LocalSpacing
import com.onimeno.onicanvas.core.designsystem.theme.SuccessColor
import com.onimeno.onicanvas.feature.profiles.state.AppProfile
import com.onimeno.onicanvas.feature.profiles.state.ProfilesUiState
import com.onimeno.onicanvas.feature.profiles.state.UserProfile
import com.onimeno.onicanvas.feature.profiles.viewmodel.ProfilesViewModel
import com.onimeno.onicanvas.feature.profiles.viewmodel.ProfilesViewModelFactory

@Composable
fun ProfilesScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as OniCanvasApp
    val viewModel: ProfilesViewModel = viewModel(
        factory = ProfilesViewModelFactory(app.container.profileRepository)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    var showCreateProfileDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            OniTopBar(
                title = "Profiles",
                actions = {
                    IconButton(
                        onClick = { showCreateProfileDialog = true },
                        modifier = Modifier.testTag("create_profile_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Create mapping profile",
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
                is ProfilesUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is ProfilesUiState.Success -> {
                    ProfilesContent(
                        state = state,
                        onSelectProfile = { viewModel.selectProfile(it) }
                    )
                }
                is ProfilesUiState.Error -> {
                    OniEmptyState(
                        title = "User profile offline",
                        description = state.message,
                        icon = Icons.Rounded.Person,
                        actionText = "Retry",
                        onActionClick = { viewModel.loadProfiles() }
                    )
                }
            }

            if (showCreateProfileDialog) {
                CreateProfileDialog(
                    onDismiss = { showCreateProfileDialog = false },
                    onConfirm = { name, app, desc ->
                        viewModel.addProfile(name, app, desc)
                        showCreateProfileDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun ProfilesContent(
    state: ProfilesUiState.Success,
    onSelectProfile: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profiles_screen_container"),
        contentPadding = PaddingValues(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        item { ArtistAccountCard(user = state.user) }
        item { OniSectionHeader(title = "App Mapping Layouts") }
        items(state.availableProfiles, key = { it.id }) { profile ->
            AppProfileRowItem(profile = profile, onSelect = { onSelectProfile(profile.id) })
        }
    }
}

@Composable
fun ArtistAccountCard(user: UserProfile, modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    OniCard(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(64.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Person, contentDescription = "User Avatar", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.width(spacing.medium))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(user.username, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Rounded.Star, contentDescription = "Pro level account", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                }
                Text(user.artistTier, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Rounded.Sync, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(12.dp))
                    Text("${user.syncCount} synced sessions", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AppProfileRowItem(profile: AppProfile, onSelect: () -> Unit, modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val icon = when (profile.targetApp) {
        "Clip Studio Paint" -> Icons.Rounded.Brush
        "Photoshop" -> Icons.Rounded.Palette
        "Krita" -> Icons.Rounded.FolderSpecial
        else -> Icons.Rounded.Category
    }
    Row(
        modifier = modifier.fillMaxWidth().clip(GlassCardShape)
            .background(if (profile.isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .border(1.dp, if (profile.isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else Color.Transparent, GlassCardShape)
            .clickable(onClick = onSelect).padding(spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.size(38.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(spacing.medium))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(profile.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    if (profile.isDefault) {
                        Spacer(modifier = Modifier.width(spacing.small))
                        Box(modifier = Modifier.background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f), CircleShape).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text("DEFAULT", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(profile.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(2.dp))
                Text("${profile.layoutCount} LAYOUT MAPPINGS INCLUDED", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
            }
        }
        Box(modifier = Modifier.background(if (profile.isActive) SuccessColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant, CircleShape).padding(horizontal = 8.dp, vertical = 4.dp)) {
            Text(if (profile.isActive) "ACTIVE" else "SELECT", style = MaterialTheme.typography.labelSmall, color = if (profile.isActive) SuccessColor else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CreateProfileDialog(onDismiss: () -> Unit, onConfirm: (name: String, app: String, desc: String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var appTarget by remember { mutableStateOf("Photoshop") }
    var description by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Mapping Profile", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Profile Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = appTarget, onValueChange = { appTarget = it }, label = { Text("Desktop Software App") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onConfirm(name, appTarget, description) }, enabled = name.isNotBlank()) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
