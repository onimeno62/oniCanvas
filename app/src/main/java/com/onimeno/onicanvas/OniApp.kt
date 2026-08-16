package com.onimeno.onicanvas

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.FolderSpecial
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Palette
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
import com.onimeno.onicanvas.core.designsystem.components.AdaptiveNavigationItem
import com.onimeno.onicanvas.core.designsystem.components.AdaptiveNavigationScaffold
import com.onimeno.onicanvas.feature.about.ui.AboutScreen
import com.onimeno.onicanvas.feature.color.ui.ColorWorkflowScreen
import com.onimeno.onicanvas.feature.color.viewmodel.ColorWorkflowViewModel
import com.onimeno.onicanvas.feature.color.viewmodel.ColorWorkflowViewModelFactory
import com.onimeno.onicanvas.feature.connection.ui.ConnectionScreen
import com.onimeno.onicanvas.feature.connection.viewmodel.ConnectionViewModel
import com.onimeno.onicanvas.feature.connection.viewmodel.ConnectionViewModelFactory
import com.onimeno.onicanvas.feature.controls.ui.ControlsScreen
import com.onimeno.onicanvas.feature.controls.viewmodel.ControlsViewModel
import com.onimeno.onicanvas.feature.controls.viewmodel.ControlsViewModelFactory
import com.onimeno.onicanvas.feature.dashboard.ui.DashboardScreen
import com.onimeno.onicanvas.feature.dashboard.viewmodel.DashboardViewModel
import com.onimeno.onicanvas.feature.dashboard.viewmodel.DashboardViewModelFactory
import com.onimeno.onicanvas.feature.profiles.ui.ProfilesScreen
import com.onimeno.onicanvas.feature.productivity.ui.ProductivityScreen
import com.onimeno.onicanvas.feature.productivity.viewmodel.ProductivityViewModel
import com.onimeno.onicanvas.feature.productivity.viewmodel.ProductivityViewModelFactory
import com.onimeno.onicanvas.feature.settings.ui.SettingsScreen
import com.onimeno.onicanvas.feature.workspace.ui.WorkspaceEditorRoute as WorkspaceEditorRouteScreen
import com.onimeno.onicanvas.feature.workspace.ui.WorkspaceScreen
import com.onimeno.onicanvas.navigation.AboutRoute
import com.onimeno.onicanvas.navigation.ColorRoute
import com.onimeno.onicanvas.navigation.ConnectionRoute
import com.onimeno.onicanvas.navigation.ControlsRoute
import com.onimeno.onicanvas.navigation.DashboardRoute
import com.onimeno.onicanvas.navigation.ProfilesRoute
import com.onimeno.onicanvas.navigation.ProductivityRoute
import com.onimeno.onicanvas.navigation.SettingsRoute
import com.onimeno.onicanvas.navigation.WorkspaceEditorRoute
import com.onimeno.onicanvas.navigation.WorkspaceRoute

data class BottomNavItem<T : Any>(val route: T, val label: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector, val testTag: String)

@Composable
fun OniApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomNavItems = listOf(
        AdaptiveNavigationItem(DashboardRoute, "Dashboard", Icons.Rounded.Dashboard, Icons.Rounded.Dashboard, "nav_dashboard"),
        AdaptiveNavigationItem(WorkspaceRoute, "Workspaces", Icons.Rounded.Storage, Icons.Rounded.Storage, "nav_workspaces"),
        AdaptiveNavigationItem(ControlsRoute, "Controls", Icons.Rounded.TrackChanges, Icons.Rounded.TrackChanges, "nav_controls"),
        AdaptiveNavigationItem(ColorRoute, "Color", Icons.Rounded.Palette, Icons.Rounded.Palette, "nav_color"),
        AdaptiveNavigationItem(ProductivityRoute, "Productivity", Icons.Rounded.Layers, Icons.Rounded.Layers, "nav_productivity"),
        AdaptiveNavigationItem(ConnectionRoute, "Connect", Icons.Rounded.Wifi, Icons.Rounded.Wifi, "nav_connect"),
        AdaptiveNavigationItem(ProfilesRoute, "Profiles", Icons.Rounded.FolderSpecial, Icons.Rounded.FolderSpecial, "nav_profiles")
    )
    val showNavigation = bottomNavItems.any { item -> currentDestination?.hasRoute(item.route::class) == true }

    AdaptiveNavigationScaffold(
        navigationItems = bottomNavItems,
        isItemSelected = { item -> currentDestination?.hasRoute(item.route::class) == true },
        onItemSelected = { item ->
            navController.navigate(item.route) {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        },
        showNavigation = showNavigation
    ) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            NavHost(navController = navController, startDestination = DashboardRoute) {
                composable<DashboardRoute> {
                    val app = LocalContext.current.applicationContext as OniCanvasApp
                    val vm: DashboardViewModel = viewModel(factory = DashboardViewModelFactory(app.container.dashboardRepository))
                    DashboardScreen(
                        viewModel = vm,
                        onNavigateToConnection = { navController.navigate(ConnectionRoute) },
                        onNavigateToControls = { navController.navigate(ControlsRoute) },
                        onNavigateToWorkspace = { navController.navigate(WorkspaceRoute) },
                        onNavigateToSettings = { navController.navigate(SettingsRoute) },
                        onNavigateToAbout = { navController.navigate(AboutRoute) }
                    )
                }
                composable<WorkspaceRoute> { WorkspaceScreen(onWorkspaceClick = { id -> navController.navigate(WorkspaceEditorRoute(id)) }) }
                composable<WorkspaceEditorRoute> { entry ->
                    val route: WorkspaceEditorRoute = entry.toRoute()
                    WorkspaceEditorRouteScreen(workspaceId = route.workspaceId, onBackClick = { navController.popBackStack() })
                }
                composable<ControlsRoute> {
                    val app = LocalContext.current.applicationContext as OniCanvasApp
                    val vm: ControlsViewModel = viewModel(factory = ControlsViewModelFactory(app.container.workspaceRepository, app.container.connectionRepository))
                    ControlsScreen(viewModel = vm)
                }
                composable<ColorRoute> {
                    val app = LocalContext.current.applicationContext as OniCanvasApp
                    val vm: ColorWorkflowViewModel = viewModel(factory = ColorWorkflowViewModelFactory(app.container.colorWorkflowRepository, app.container.connectionRepository))
                    ColorWorkflowScreen(viewModel = vm)
                }
                composable<ProductivityRoute> {
                    val app = LocalContext.current.applicationContext as OniCanvasApp
                    val vm: ProductivityViewModel = viewModel(factory = ProductivityViewModelFactory(app.container.connectionRepository))
                    ProductivityScreen(viewModel = vm)
                }
                composable<ConnectionRoute> {
                    val app = LocalContext.current.applicationContext as OniCanvasApp
                    val vm: ConnectionViewModel = viewModel(factory = ConnectionViewModelFactory(app.container.connectionRepository))
                    ConnectionScreen(viewModel = vm)
                }
                composable<ProfilesRoute> { ProfilesScreen() }
                composable<SettingsRoute> { SettingsScreen(onBackClick = { navController.popBackStack() }) }
                composable<AboutRoute> { AboutScreen(onBackClick = { navController.popBackStack() }) }
            }
        }
    }
}
