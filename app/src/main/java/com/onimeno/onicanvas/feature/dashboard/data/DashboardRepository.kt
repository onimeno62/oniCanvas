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
                latencyMs = connection.latencyMs ?: 0,
                recentWorkspaces = recentWorkspaces,
                quickActions = quickActions
            )
        }

    private companion object {
        val recentWorkspaces = listOf(
            WorkspaceSummary("illust_layout", "Illustration Master", "Optimized for painting & detailing", "brush", 16),
            WorkspaceSummary("manga_layout", "Manga Page Setup", "Fast panel and line-art macros", "book", 12),
            WorkspaceSummary("sculpt_3d", "3D Sculpt Companion", "Camera rotation & brush size mapping", "cube", 20),
            WorkspaceSummary("sketch_pad", "Quick Sketch Layout", "Minimalist pad for raw concepts", "edit", 8)
        )

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
    private val delegate = RealDashboardRepository(ConnectionRepository())

    override fun getDashboardData(): Flow<DashboardData> = delegate.getDashboardData()
}
