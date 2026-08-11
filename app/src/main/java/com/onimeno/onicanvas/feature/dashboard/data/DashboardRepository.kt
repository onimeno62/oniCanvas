package com.onimeno.onicanvas.feature.dashboard.data

import com.onimeno.onicanvas.core.designsystem.components.OniStatus
import com.onimeno.onicanvas.feature.connection.data.ConnectionRepository
import com.onimeno.onicanvas.feature.dashboard.state.QuickActionItem
import com.onimeno.onicanvas.feature.dashboard.state.QuickActionType
import com.onimeno.onicanvas.feature.dashboard.state.WorkspaceSummary
import com.onimeno.onicanvas.feature.workspace.data.WorkspaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

data class DashboardData(
    val deviceName: String,
    val connectionType: String,
    val connectionStatus: OniStatus,
    val activeSoftware: String,
    val activeWorkspace: String,
    val batteryLevel: Int,
    val latencyMs: Int,
    val recentWorkspaces: List<WorkspaceSummary>,
    val quickActions: List<QuickActionItem>
)

interface DashboardRepository {
    fun getDashboardData(): Flow<DashboardData>
}

class RealDashboardRepository(
    private val connectionRepository: ConnectionRepository,
    private val workspaceRepository: WorkspaceRepository
) : DashboardRepository {
    override fun getDashboardData(): Flow<DashboardData> = combine(
        connectionRepository.state,
        workspaceRepository.workspaces,
        workspaceRepository.activeWorkspace
    ) { connection, workspacesList, activeWS ->
        val summaries = workspacesList.map { ws ->
            WorkspaceSummary(
                id = ws.id,
                name = ws.name,
                description = ws.description,
                iconName = ws.iconName,
                buttonCount = ws.gridSize * ws.gridSize
            )
        }.take(4)

        DashboardData(
            deviceName = connection.activeHostName ?: "No device connected",
            connectionType = connection.transportType,
            connectionStatus = connection.status,
            activeSoftware = activeWS?.targetApp ?: "Clip Studio Paint",
            activeWorkspace = activeWS?.name ?: "No Active Workspace",
            batteryLevel = 88,
            latencyMs = connection.latencyMs ?: 0,
            recentWorkspaces = summaries,
            quickActions = quickActions
        )
    }

    companion object {
        val quickActions = listOf(
            QuickActionItem("Connect System", "Manage Companion Connection", "wifi", QuickActionType.CONNECT),
            QuickActionItem("Macro Pad", "Trigger Keyboard Actions", "apps", QuickActionType.MACRO_PAD),
            QuickActionItem("Gesture Pad", "Pan, Zoom, & Rotate Canvas", "gesture", QuickActionType.GESTURE_PAD),
            QuickActionItem("Radial Menu", "Thumb-friendly Action Ring", "track_changes", QuickActionType.RADIAL_MENU),
            QuickActionItem("Touchpad Mode", "Wireless Desktop Precision Mouse", "mouse", QuickActionType.TOUCHPAD)
        )
    }
}

class FakeDashboardRepository : DashboardRepository {
    override fun getDashboardData(): Flow<DashboardData> = flowOf(
        DashboardData(
            deviceName = "No device connected",
            connectionType = "WIFI",
            connectionStatus = OniStatus.OFFLINE,
            activeSoftware = "Clip Studio Paint",
            activeWorkspace = "Manga / Illustration Layout",
            batteryLevel = 88,
            latencyMs = 0,
            recentWorkspaces = emptyList(),
            quickActions = RealDashboardRepository.quickActions
        )
    )
}
