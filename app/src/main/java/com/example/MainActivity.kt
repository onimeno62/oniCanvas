package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MacrosScreen
import com.example.ui.screens.PaletteScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TouchpadScreen
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.viewmodel.OniViewModel
import com.example.ui.viewmodel.OniViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = application as OniApplication
            val viewModel: OniViewModel = viewModel(
                factory = OniViewModelFactory(
                    app.repository,
                    app.settingsStore,
                    app.connectionService
                )
            )

            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

            MyApplicationTheme(
                darkTheme = themeMode == "dark"
            ) {
                MainContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: OniViewModel) {
    var selectedTab by remember { mutableStateOf("Home") }

    val activeWorkspace by viewModel.activeWorkspace.collectAsStateWithLifecycle()
    val workspaces by viewModel.workspaces.collectAsStateWithLifecycle()
    val buttons by viewModel.buttons.collectAsStateWithLifecycle()
    val favoriteBrushes by viewModel.favoriteBrushes.collectAsStateWithLifecycle()
    val recentColors by viewModel.recentColors.collectAsStateWithLifecycle()

    val status by viewModel.status.collectAsStateWithLifecycle()
    val latency by viewModel.latency.collectAsStateWithLifecycle()
    val cpu by viewModel.cpuUsage.collectAsStateWithLifecycle()
    val ram by viewModel.ramUsage.collectAsStateWithLifecycle()
    val battery by viewModel.batteryLevel.collectAsStateWithLifecycle()
    val logs by viewModel.protocolLogs.collectAsStateWithLifecycle()

    val ip by viewModel.ipAddress.collectAsStateWithLifecycle()
    val port by viewModel.port.collectAsStateWithLifecycle()
    val haptics by viewModel.hapticsEnabled.collectAsStateWithLifecycle()
    val voice by viewModel.voiceEnabled.collectAsStateWithLifecycle()
    val theme by viewModel.themeMode.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080710)),
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(24.dp)
                    ),
                containerColor = Color(0xFF131124),
                tonalElevation = 8.dp
            ) {
                val menuItems = listOf(
                    Triple("Home", Icons.Default.Dashboard, "home_tab"),
                    Triple("Macros", Icons.Default.GridOn, "macros_tab"),
                    Triple("Touchpad", Icons.Default.Mouse, "touchpad_tab"),
                    Triple("Palette", Icons.Default.Palette, "palette_tab"),
                    Triple("Settings", Icons.Default.Settings, "settings_tab")
                )

                menuItems.forEach { (name, icon, tag) ->
                    val isSelected = selectedTab == name
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = name },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = name,
                                modifier = Modifier.testTag(tag)
                            )
                        },
                        label = {
                            Text(
                                text = name,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentCyan,
                            selectedTextColor = AccentCyan,
                            unselectedIconColor = Color.White.copy(alpha = 0.4f),
                            unselectedTextColor = Color.White.copy(alpha = 0.4f),
                            indicatorColor = Color(0xFF1F1D36)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF080710))
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                "Home" -> HomeScreen(
                    activeWorkspace = activeWorkspace,
                    workspaces = workspaces,
                    status = status,
                    latency = latency,
                    cpu = cpu,
                    ram = ram,
                    battery = battery,
                    onWorkspaceSelected = { viewModel.changeWorkspace(it) },
                    onTriggerAction = { label, key -> viewModel.triggerShortcut(label, key) },
                    onNavigateToSettings = { selectedTab = "Settings" }
                )
                "Macros" -> MacrosScreen(
                    buttons = buttons,
                    onTriggerAction = { label, key -> viewModel.triggerShortcut(label, key) },
                    onAddButton = { label, key, color, page, row, col ->
                        viewModel.addMacroButton(label, key, color, page, row, col)
                    },
                    onDeleteButton = { viewModel.deleteMacroButton(it) }
                )
                "Touchpad" -> TouchpadScreen(
                    onTriggerAction = { label, key -> viewModel.triggerCanvasAction(label, mapOf("keys" to listOf(key))) }
                )
                "Palette" -> PaletteScreen(
                    recentColors = recentColors,
                    favoriteBrushes = favoriteBrushes,
                    onTriggerAction = { label, op -> viewModel.triggerCanvasAction(label, mapOf("op" to op)) },
                    onColorSelected = { viewModel.selectColor(it) }
                )
                "Settings" -> SettingsScreen(
                    ip = ip,
                    port = port,
                    haptics = haptics,
                    voice = voice,
                    theme = theme,
                    status = status,
                    logs = logs,
                    onSaveSettings = { host, p, hap, voc, th ->
                        viewModel.saveSettings(host, p, hap, voc, th)
                    },
                    onToggleConnection = { viewModel.toggleConnection() }
                )
            }
        }
    }
}
