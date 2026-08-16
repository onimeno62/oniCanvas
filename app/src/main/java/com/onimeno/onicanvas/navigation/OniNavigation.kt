package com.onimeno.onicanvas.navigation

import kotlinx.serialization.Serializable

@Serializable
object DashboardRoute

@Serializable
object WorkspaceRoute

@Serializable
data class WorkspaceEditorRoute(val workspaceId: String)

@Serializable
object ControlsRoute

@Serializable
object ColorRoute

@Serializable
object ProductivityRoute

@Serializable
object ConnectionRoute

@Serializable
object ProfilesRoute

@Serializable
object SettingsRoute

@Serializable
object AboutRoute
