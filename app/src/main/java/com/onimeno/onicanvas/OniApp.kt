package com.onimeno.onicanvas

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.FolderSpecial
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.onimeno.onicanvas.feature.about.ui.AboutScreen
import com.onimeno.onicanvas.feature.connection.ui.ConnectionScreen
import com.onimeno.onicanvas.feature.controls.ui.ControlsScreen
import com.onimeno.onicanvas.feature.dashboard.ui.DashboardScreen
import com.onimeno.onicanvas.feature.dashboard.viewmodel.DashboardViewModel
import com.onimeno.onicanvas.feature.profiles.ui.ProfilesScreen
import com.onimeno.onicanvas.feature.settings.ui.SettingsScreen
import com.onimeno.onicanvas.feature.workspace.ui.WorkspaceScreen
import com.onimeno.onicanvas.feature.workspace.ui.WorkspaceEditorScreen
import com.onimeno.onicanvas.navigation.AboutRoute
import com.onimeno.onicanvas.navigation.ConnectionRoute
import com.onimeno.onicanvas.navigation.ControlsRoute
import com.onimeno.onicanvas.navigation.DashboardRoute
import com.onimeno.onicanvas.navigation.ProfilesRoute
import com.onimeno.onicanvas.navigation.SettingsRoute
import com.onimeno.onicanvas.navigation.WorkspaceRoute
import com.onimeno.onicanvas.navigation.WorkspaceEditorRoute

// Bottom Bar Destination Configuration
data class BottomNavItem<T : Any>(
    val route: T,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun OniApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Five main hubs as defined in docs/05_Navigation_and_Screens.md
    val bottomNavItems = listOf(
        BottomNavItem(
            route = DashboardRoute,
            label = "Dashboard",
            selectedIcon = Icons.Rounded.Dashboard,
            unselectedIcon = Icons.Rounded.Dashboard
        ),
        BottomNavItem(
            route = WorkspaceRoute,
            label = "Workspaces",
            selectedIcon = Icons.Rounded.Storage,
            unselectedIcon = Icons.Rounded.Storage
        ),
        BottomNavItem(
            route = ControlsRoute,
            label = "Controls",
            selectedIcon = Icons.Rounded.TrackChanges,
            unselectedIcon = Icons.Rounded.TrackChanges
        ),
        BottomNavItem(
            route = ConnectionRoute,
            label = "Connect",
            selectedIcon = Icons.Rounded.Wifi,
            unselectedIcon = Icons.Rounded.Wifi
        ),
        BottomNavItem(
            route = ProfilesRoute,
            label = "Profiles",
            selectedIcon = Icons.Rounded.FolderSpecial,
            unselectedIcon = Icons.Rounded.FolderSpecial
        )
    )

    // Determine if bottom bar should be visible based on whether the current destination is a main tab
    val showBottomBar = bottomNavItems.any { item ->
        currentDestination?.hasRoute(item.route::class) == true
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentDestination?.hasRoute(item.route::class) == true
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.selectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = DashboardRoute
            ) {
                composable<DashboardRoute> {
                    val viewModel: DashboardViewModel = viewModel()
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToConnection = { navController.navigate(ConnectionRoute) },
                        onNavigateToControls = { navController.navigate(ControlsRoute) },
                        onNavigateToWorkspace = { navController.navigate(WorkspaceRoute) },
                        onNavigateToSettings = { navController.navigate(SettingsRoute) },
                        onNavigateToAbout = { navController.navigate(AboutRoute) }
                    )
                }
                
                composable<WorkspaceRoute> {
                    WorkspaceScreen(onWorkspaceClick = { id ->
                        navController.navigate(WorkspaceEditorRoute(id))
                    })
                }

                composable<WorkspaceEditorRoute> { backStackEntry ->
                    val route: WorkspaceEditorRoute = backStackEntry.toRoute()
                    WorkspaceEditorScreen(
                        workspaceId = route.workspaceId,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable<ControlsRoute> {
                    ControlsScreen()
                }

                composable<ConnectionRoute> {
                    ConnectionScreen()
                }

                composable<ProfilesRoute> {
                    ProfilesScreen()
                }

                composable<SettingsRoute> {
                    SettingsScreen(onBackClick = { navController.popBackStack() })
                }

                composable<AboutRoute> {
                    AboutScreen(onBackClick = { navController.popBackStack() })
                }
            }
        }
    }
}
