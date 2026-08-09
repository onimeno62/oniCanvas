package com.onimeno.onicanvas.feature.dashboard.data

import com.onimeno.onicanvas.core.designsystem.components.OniStatus
import com.onimeno.onicanvas.feature.connection.data.ConnectionRepository
import com.onimeno.onicanvas.feature.dashboard.state.QuickActionItem
import com.onimeno.onicanvas.feature.dashboard.state.QuickActionType
import com.onimeno.onicanvas.feature.dashboard.state.WorkspaceSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
    private val connectionRepository: ConnectionRepository
) : DashboardRepository {
    override fun getDashboardData(): Flow<DashboardData> =
        connectionRepository.state.map { connection ->
            DashboardData(
                deviceName = connection.activeHostName ?: "No device connected",
                connectionType = connection.transportType,
                connectionStatus = connection.status,
                activeSoftware = "Clip Studio Paint",
                activeWorkspace = "Manga / Illustration Layout",
                batteryLevel = 88,
                latencyMs = 8,
                recentWorkspaces = recentWorkspaces,
                quickActions = quickActions
            )
        }

    private companion object {
        val recentWorkspaces = listOf(
            WorkspaceSummary(
                id = "illust_layout",
                name = "Illustration Master",
                description = "Optimized for painting & detailing",
                iconName = "brush",
                buttonCount = 16
            ),
            WorkspaceSummary(
                id = "manga_layout",
                name = "Manga Page Setup",
                description = "Fast panel and line-art macros",
                iconName = "book",
                buttonCount = 12
            ),
            WorkspaceSummary(
                id = "sculpt_3d",
                name = "3D Sculpt Companion",
                description = "Camera rotation & brush size mapping",
                iconName = "cube",
                buttonCount = 20
            ),
            WorkspaceSummary(
                id = "sketch_pad",
                name = "Quick Sketch Layout",
                description = "Minimalist pad for raw concepts",
                iconName = "edit",
                buttonCount = 8
            )
        )

        val quickActions = listOf(
            QuickActionItem(
                title = "Connect System",
                description = "Manage Companion Connection",
                iconName = "wifi",
                actionType = QuickActionType.CONNECT
            ),
            QuickActionItem(
                title = "Macro Pad",
                description = "Trigger Keyboard Actions",
                iconName = "apps",
                actionType = QuickActionType.MACRO_PAD
            ),
            QuickActionItem(
                title = "Gesture Pad",
                description = "Pan, Zoom, & Rotate Canvas",
                iconName = "gesture",
                actionType = QuickActionType.GESTURE_PAD
            ),
            QuickActionItem(
                title = "Radial Menu",
                description = "Thumb-friendly Action Ring",
                iconName = "track_changes",
                actionType = QuickActionType.RADIAL_MENU
            ),
            QuickActionItem(
                title = "Touchpad Mode",
                description = "Wireless Desktop Precision Mouse",
                iconName = "mouse",
                actionType = QuickActionType.TOUCHPAD
            )
        )
    }
}

class FakeDashboardRepository : DashboardRepository {
    private val delegate = RealDashboardRepository(ConnectionRepository())

    override fun getDashboardData(): Flow<DashboardData> = delegate.getDashboardData()
}
