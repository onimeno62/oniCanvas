package com.onimeno.onicanvas.feature.dashboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Gesture
import androidx.compose.material.icons.rounded.Mouse
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.onimeno.onicanvas.core.designsystem.components.OniQuickActionButton
import com.onimeno.onicanvas.core.designsystem.theme.LocalSpacing
import com.onimeno.onicanvas.feature.dashboard.state.QuickActionItem
import com.onimeno.onicanvas.feature.dashboard.state.QuickActionType

@Composable
fun DashboardQuickActions(
    quickActions: List<QuickActionItem>,
    onActionClick: (QuickActionType) -> Unit,
    modifier: Modifier = Modifier,
    isTablet: Boolean = false
) {
    val spacing = LocalSpacing.current

    // Group the items into pairs to render a perfect 2x2 grid on phone, or larger on tablet
    val itemsToRender = quickActions.take(4) // Guarantee exactly 4 for 2x2 layout

    if (itemsToRender.size < 4) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        if (isTablet) {
            // 4-column layout on tablet
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                itemsToRender.forEach { action ->
                    val icon = getQuickActionIcon(action.iconName)
                    OniQuickActionButton(
                        title = action.title,
                        description = action.description,
                        icon = icon,
                        onClick = { onActionClick(action.actionType) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        } else {
            // 2x2 grid on mobile
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                listOf(itemsToRender[0], itemsToRender[1]).forEach { action ->
                    val icon = getQuickActionIcon(action.iconName)
                    OniQuickActionButton(
                        title = action.title,
                        description = action.description,
                        icon = icon,
                        onClick = { onActionClick(action.actionType) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                listOf(itemsToRender[2], itemsToRender[3]).forEach { action ->
                    val icon = getQuickActionIcon(action.iconName)
                    OniQuickActionButton(
                        title = action.title,
                        description = action.description,
                        icon = icon,
                        onClick = { onActionClick(action.actionType) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

private fun getQuickActionIcon(iconName: String) = when (iconName) {
    "wifi" -> Icons.Rounded.Wifi
    "apps" -> Icons.Rounded.Apps
    "gesture" -> Icons.Rounded.Gesture
    "track_changes" -> Icons.Rounded.TrackChanges
    "mouse" -> Icons.Rounded.Mouse
    else -> Icons.Rounded.Category
}
