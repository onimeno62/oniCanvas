package com.onimeno.onicanvas.feature.color.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.onimeno.onicanvas.core.designsystem.components.OniStatus
import com.onimeno.onicanvas.core.designsystem.components.OniTopBar
import com.onimeno.onicanvas.core.designsystem.components.StatusIndicator
import com.onimeno.onicanvas.core.designsystem.theme.GlassCardShape
import com.onimeno.onicanvas.core.designsystem.theme.LocalSpacing
import com.onimeno.onicanvas.feature.color.state.ColorPickerMode
import com.onimeno.onicanvas.feature.color.viewmodel.ColorWorkflowViewModel

@Composable
fun ColorWorkflowScreen(
    viewModel: ColorWorkflowViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val spacing = LocalSpacing.current

    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            OniTopBar(
                title = "Color Studio",
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        StatusIndicator(
                            status = if (uiState.isConnected) OniStatus.SUCCESS else OniStatus.OFFLINE
                        )
                        Text(
                            text = if (uiState.isConnected) uiState.hostName ?: "Connected" else "Offline Surface",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val isTablet = maxWidth >= 720.dp

            if (isTablet) {
                // Adaptive 2-Column Split Layout for Tablets and Foldables
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column: Wheel & Preview Card
                    Column(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ColorWheel(
                            selectedColor = uiState.selectedColor,
                            onHsvChanged = { h, s, v -> viewModel.updateHsv(h, s, v) },
                            onColorCommitted = { viewModel.commitColor() },
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        ColorPreviewCard(
                            selectedColor = uiState.selectedColor,
                            previousColor = uiState.previousColor,
                            isConnected = uiState.isConnected,
                            onSwapPrevious = { viewModel.swapWithPreviousColor() },
                            onSaveToPalette = { viewModel.addColorToCurrentPalette() },
                            onCommitToHost = { viewModel.commitColor() }
                        )

                        HexInputSection(
                            hexText = uiState.hexInputText,
                            isValid = uiState.isHexInputValid,
                            onHexChanged = { viewModel.updateHexInput(it) },
                            onApplyHex = { viewModel.applyHexInput() }
                        )
                    }

                    // Right Column: Controls + Palettes + Recent
                    LazyColumn(
                        modifier = Modifier
                            .weight(1.3f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item {
                            ModeSelectorCard(
                                selectedMode = uiState.activeMode,
                                onModeSelected = { viewModel.setActiveMode(it) }
                            )
                        }

                        item {
                            when (uiState.activeMode) {
                                ColorPickerMode.WHEEL, ColorPickerMode.HSV -> {
                                    HsvControls(
                                        selectedColor = uiState.selectedColor,
                                        onHueChanged = { viewModel.updateHue(it) },
                                        onSaturationChanged = { viewModel.updateSaturation(it) },
                                        onValueChanged = { viewModel.updateValue(it) },
                                        onColorCommitted = { viewModel.commitColor() }
                                    )
                                }
                                ColorPickerMode.RGB -> {
                                    RgbControls(
                                        selectedColor = uiState.selectedColor,
                                        onRedChanged = { viewModel.updateRed(it) },
                                        onGreenChanged = { viewModel.updateGreen(it) },
                                        onBlueChanged = { viewModel.updateBlue(it) },
                                        onColorCommitted = { viewModel.commitColor() }
                                    )
                                }
                                ColorPickerMode.PALETTES -> Unit
                            }
                        }

                        item {
                            RecentColorsSection(
                                recentColors = uiState.recentColors,
                                selectedHex = uiState.selectedColor.hex,
                                onColorSelected = { viewModel.selectColor(it, recordRecent = true) },
                                onClearAll = { viewModel.openClearRecentDialog() }
                            )
                        }

                        item {
                            PaletteManagerSection(
                                palettes = uiState.palettes,
                                selectedPalette = uiState.selectedPalette,
                                selectedHex = uiState.selectedColor.hex,
                                dialogMode = uiState.dialogMode,
                                dialogInputText = uiState.dialogInputText,
                                onSelectPalette = { viewModel.selectPalette(it) },
                                onColorSelected = { viewModel.selectColor(it, recordRecent = true) },
                                onAddCurrentColor = { viewModel.addColorToCurrentPalette() },
                                onRemoveColor = { pId, hex -> viewModel.removeColorFromPalette(pId, hex) },
                                onReorderColor = { pId, from, to -> viewModel.reorderColorInPalette(pId, from, to) },
                                onOpenCreateDialog = { viewModel.openCreatePaletteDialog() },
                                onOpenRenameDialog = { viewModel.openRenamePaletteDialog(it) },
                                onOpenDeleteDialog = { viewModel.openDeletePaletteDialog(it) },
                                onDismissDialog = { viewModel.dismissDialog() },
                                onDialogInputChange = { viewModel.updateDialogInput(it) },
                                onConfirmDialog = { viewModel.confirmDialog() },
                                onSendPaletteToCompanion = { viewModel.sendCurrentPaletteToCompanion() }
                            )
                        }
                    }
                }
            } else {
                // Mobile Vertical Layout with Tabs
                var selectedTabIndex by remember { mutableIntStateOf(0) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp)
                ) {
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.testTag("tabrow_color_workflow")
                    ) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            text = { Text("Color Wheel", fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal) },
                            icon = { Icon(Icons.Rounded.ColorLens, contentDescription = null) },
                            modifier = Modifier.testTag("tab_color_wheel")
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            text = { Text("Sliders & Hex", fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal) },
                            icon = { Icon(Icons.Rounded.Tune, contentDescription = null) },
                            modifier = Modifier.testTag("tab_sliders")
                        )
                        Tab(
                            selected = selectedTabIndex == 2,
                            onClick = { selectedTabIndex = 2 },
                            text = { Text("Palettes", fontWeight = if (selectedTabIndex == 2) FontWeight.Bold else FontWeight.Normal) },
                            icon = { Icon(Icons.Rounded.Palette, contentDescription = null) },
                            modifier = Modifier.testTag("tab_palettes")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        when (selectedTabIndex) {
                            0 -> {
                                // Tab 0: Color Wheel & Quick Preview & Recents
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ColorWheel(
                                            selectedColor = uiState.selectedColor,
                                            onHsvChanged = { h, s, v -> viewModel.updateHsv(h, s, v) },
                                            onColorCommitted = { viewModel.commitColor() }
                                        )
                                    }
                                }

                                item {
                                    ColorPreviewCard(
                                        selectedColor = uiState.selectedColor,
                                        previousColor = uiState.previousColor,
                                        isConnected = uiState.isConnected,
                                        onSwapPrevious = { viewModel.swapWithPreviousColor() },
                                        onSaveToPalette = { viewModel.addColorToCurrentPalette() },
                                        onCommitToHost = { viewModel.commitColor() }
                                    )
                                }

                                item {
                                    HexInputSection(
                                        hexText = uiState.hexInputText,
                                        isValid = uiState.isHexInputValid,
                                        onHexChanged = { viewModel.updateHexInput(it) },
                                        onApplyHex = { viewModel.applyHexInput() }
                                    )
                                }

                                item {
                                    RecentColorsSection(
                                        recentColors = uiState.recentColors,
                                        selectedHex = uiState.selectedColor.hex,
                                        onColorSelected = { viewModel.selectColor(it, recordRecent = true) },
                                        onClearAll = { viewModel.openClearRecentDialog() }
                                    )
                                }
                            }
                            1 -> {
                                // Tab 1: Detailed Sliders (HSV & RGB) and Hex Input
                                item {
                                    ModeSelectorCard(
                                        selectedMode = uiState.activeMode,
                                        onModeSelected = { viewModel.setActiveMode(it) }
                                    )
                                }

                                item {
                                    ColorPreviewCard(
                                        selectedColor = uiState.selectedColor,
                                        previousColor = uiState.previousColor,
                                        isConnected = uiState.isConnected,
                                        onSwapPrevious = { viewModel.swapWithPreviousColor() },
                                        onSaveToPalette = { viewModel.addColorToCurrentPalette() },
                                        onCommitToHost = { viewModel.commitColor() }
                                    )
                                }

                                item {
                                    HexInputSection(
                                        hexText = uiState.hexInputText,
                                        isValid = uiState.isHexInputValid,
                                        onHexChanged = { viewModel.updateHexInput(it) },
                                        onApplyHex = { viewModel.applyHexInput() }
                                    )
                                }

                                item {
                                    if (uiState.activeMode == ColorPickerMode.RGB) {
                                        RgbControls(
                                            selectedColor = uiState.selectedColor,
                                            onRedChanged = { viewModel.updateRed(it) },
                                            onGreenChanged = { viewModel.updateGreen(it) },
                                            onBlueChanged = { viewModel.updateBlue(it) },
                                            onColorCommitted = { viewModel.commitColor() }
                                        )
                                    } else {
                                        HsvControls(
                                            selectedColor = uiState.selectedColor,
                                            onHueChanged = { viewModel.updateHue(it) },
                                            onSaturationChanged = { viewModel.updateSaturation(it) },
                                            onValueChanged = { viewModel.updateValue(it) },
                                            onColorCommitted = { viewModel.commitColor() }
                                        )
                                    }
                                }
                            }
                            2 -> {
                                // Tab 2: Palettes and History
                                item {
                                    RecentColorsSection(
                                        recentColors = uiState.recentColors,
                                        selectedHex = uiState.selectedColor.hex,
                                        onColorSelected = { viewModel.selectColor(it, recordRecent = true) },
                                        onClearAll = { viewModel.openClearRecentDialog() }
                                    )
                                }

                                item {
                                    PaletteManagerSection(
                                        palettes = uiState.palettes,
                                        selectedPalette = uiState.selectedPalette,
                                        selectedHex = uiState.selectedColor.hex,
                                        dialogMode = uiState.dialogMode,
                                        dialogInputText = uiState.dialogInputText,
                                        onSelectPalette = { viewModel.selectPalette(it) },
                                        onColorSelected = { viewModel.selectColor(it, recordRecent = true) },
                                        onAddCurrentColor = { viewModel.addColorToCurrentPalette() },
                                        onRemoveColor = { pId, hex -> viewModel.removeColorFromPalette(pId, hex) },
                                        onReorderColor = { pId, from, to -> viewModel.reorderColorInPalette(pId, from, to) },
                                        onOpenCreateDialog = { viewModel.openCreatePaletteDialog() },
                                        onOpenRenameDialog = { viewModel.openRenamePaletteDialog(it) },
                                        onOpenDeleteDialog = { viewModel.openDeletePaletteDialog(it) },
                                        onDismissDialog = { viewModel.dismissDialog() },
                                        onDialogInputChange = { viewModel.updateDialogInput(it) },
                                        onConfirmDialog = { viewModel.confirmDialog() },
                                        onSendPaletteToCompanion = { viewModel.sendCurrentPaletteToCompanion() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeSelectorCard(
    selectedMode: ColorPickerMode,
    onModeSelected: (ColorPickerMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = GlassCardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val modes = listOf(
                ColorPickerMode.HSV to "HSV Sliders",
                ColorPickerMode.RGB to "RGB Sliders"
            )
            modes.forEach { (mode, label) ->
                val isSelected = selectedMode == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                        )
                        .clickable { onModeSelected(mode) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
