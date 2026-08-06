package com.onimeno.onicanvas.feature.dashboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Battery0Bar
import androidx.compose.material.icons.rounded.Battery5Bar
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.BatteryFull
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.onimeno.onicanvas.core.designsystem.components.OniStatus
import com.onimeno.onicanvas.core.designsystem.components.OniStatusCard
import com.onimeno.onicanvas.core.designsystem.theme.LocalSpacing

@Composable
fun DashboardDeviceStatus(
    latencyMs: Int,
    batteryLevel: Int,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    val latencyStatus = when {
        latencyMs < 10 -> OniStatus.SUCCESS
        latencyMs < 20 -> OniStatus.INFO
        latencyMs < 40 -> OniStatus.WARNING
        else -> OniStatus.ERROR
    }

    val batteryStatus = when {
        batteryLevel > 30 -> OniStatus.SUCCESS
        batteryLevel > 15 -> OniStatus.WARNING
        else -> OniStatus.ERROR
    }

    val batteryIcon = when {
        batteryLevel > 80 -> Icons.Rounded.BatteryFull
        batteryLevel > 40 -> Icons.Rounded.BatteryChargingFull
        batteryLevel > 15 -> Icons.Rounded.Battery5Bar
        else -> Icons.Rounded.Battery0Bar
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        OniStatusCard(
            title = "Response Delay",
            value = "$latencyMs ms",
            icon = Icons.Rounded.Speed,
            status = latencyStatus,
            statusLabel = if (latencyMs < 15) "Excellent" else "Normal",
            modifier = Modifier.weight(1f)
        )

        OniStatusCard(
            title = "Device Battery",
            value = "$batteryLevel%",
            icon = batteryIcon,
            status = batteryStatus,
            statusLabel = if (batteryLevel > 15) "Good" else "Low",
            modifier = Modifier.weight(1f)
        )
    }
}
