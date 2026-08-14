package com.onimeno.onicanvas.feature.controls.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onimeno.onicanvas.core.designsystem.components.OniStatus
import com.onimeno.onicanvas.feature.connection.data.ConnectionRepository
import com.onimeno.onicanvas.feature.controls.state.ControlsUiState
import com.onimeno.onicanvas.feature.controls.state.CreativeControlsConfig
import com.onimeno.onicanvas.feature.controls.state.CreativeControlsTransformHelper
import com.onimeno.onicanvas.feature.controls.state.GestureAction
import com.onimeno.onicanvas.feature.controls.state.GestureType
import com.onimeno.onicanvas.feature.workspace.data.WorkspaceRepository
import com.onimeno.onicanvas.feature.workspace.state.MacroAction
import com.onimeno.onicanvas.feature.workspace.state.MacroButton
import com.onimeno.onicanvas.feature.workspace.state.MacroPage
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ControlsViewModel(
    private val workspaceRepository: WorkspaceRepository,
    private val connectionRepository: ConnectionRepository
) : ViewModel() {

    private val _activePageId = MutableStateFlow<String>("")
    private val _zoomSliderValue = MutableStateFlow<Float>(1.0f)
    val zoomSliderValue: StateFlow<Float> = _zoomSliderValue.asStateFlow()

    private var repeatJob: Job? = null

    // Stream coalescing / throttling state
    private val minStreamIntervalMs = 16L
    private var lastPanDispatchTime = 0L
    private var pendingPanX = 0.0
    private var pendingPanY = 0.0

    private var lastZoomDispatchTime = 0L
    private var pendingZoomFactor = 1.0

    private var lastRotateDispatchTime = 0L
    private var pendingRotateAngle = 0.0

    val uiState: StateFlow<ControlsUiState> = combine(
        workspaceRepository.workspaces,
        workspaceRepository.activeWorkspace,
        connectionRepository.state,
        _activePageId,
        _zoomSliderValue
    ) { workspaces, activeWS, conn, pageId, zoomVal ->
        if (activeWS == null) {
            ControlsUiState.Loading
        } else {
            val resolvedPageId = if (pageId.isEmpty() || activeWS.macroPages.none { it.id == pageId }) {
                activeWS.macroPages.firstOrNull()?.id ?: ""
            } else {
                pageId
            }
            if (resolvedPageId != pageId) {
                _activePageId.value = resolvedPageId
            }

            ControlsUiState.Success(
                activeWorkspace = activeWS,
                availableWorkspaces = workspaces,
                activePageId = resolvedPageId,
                isConnected = conn.status == OniStatus.SUCCESS,
                connectionType = conn.transportType,
                activeHostName = conn.activeHostName,
                zoomSliderValue = zoomVal
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ControlsUiState.Loading
    )

    fun setActiveWorkspace(id: String) {
        viewModelScope.launch {
            workspaceRepository.setActiveWorkspaceId(id)
        }
    }

    fun updateGridSize(newSize: Int) {
        val currentWorkspace = getActiveWorkspaceItem() ?: return
        if (newSize !in 2..5) return

        val updatedPages = currentWorkspace.macroPages.map { page ->
            val maxButtons = newSize * newSize
            val currentButtons = page.buttons.toMutableList()

            val actionsList = listOf(
                Pair("Undo", MacroAction.Undo),
                Pair("Redo", MacroAction.Redo),
                Pair("Save", MacroAction.Save),
                Pair("Brush", MacroAction.Brush),
                Pair("Eraser", MacroAction.Eraser),
                Pair("Fill", MacroAction.Fill),
                Pair("Select", MacroAction.Selection),
                Pair("Transform", MacroAction.Transform),
                Pair("Copy", MacroAction.Copy),
                Pair("Paste", MacroAction.Paste)
            )

            val adjustedButtons = if (currentButtons.size > maxButtons) {
                currentButtons.filter { it.position < maxButtons }
            } else if (currentButtons.size < maxButtons) {
                val buttonsMap = currentButtons.associateBy { it.position }.toMutableMap()
                for (pos in 0 until maxButtons) {
                    if (!buttonsMap.containsKey(pos)) {
                        val actionPair = actionsList[pos % actionsList.size]
                        buttonsMap[pos] = MacroButton(
                            id = "${page.id}_btn_${pos}",
                            position = pos,
                            label = actionPair.first,
                            iconName = actionPair.first.lowercase(),
                            action = actionPair.second,
                            longPressAction = if (actionPair.second is MacroAction.Undo) MacroAction.Redo else null,
                            repeatEnabled = false,
                            enabled = true,
                            hidden = false
                        )
                    }
                }
                buttonsMap.values.sortedBy { it.position }
            } else {
                page.buttons
            }
            page.copy(buttons = adjustedButtons)
        }

        viewModelScope.launch {
            workspaceRepository.saveWorkspace(
                currentWorkspace.copy(gridSize = newSize, macroPages = updatedPages)
            )
        }
    }

    fun addPage(name: String) {
        val currentWorkspace = getActiveWorkspaceItem() ?: return
        val newPageId = "page_${System.currentTimeMillis()}"
        val size = currentWorkspace.gridSize
        val maxButtons = size * size

        val actionsList = listOf(
            Pair("Undo", MacroAction.Undo),
            Pair("Redo", MacroAction.Redo),
            Pair("Save", MacroAction.Save),
            Pair("Brush", MacroAction.Brush),
            Pair("Eraser", MacroAction.Eraser),
            Pair("Fill", MacroAction.Fill),
            Pair("Select", MacroAction.Selection),
            Pair("Transform", MacroAction.Transform),
            Pair("Copy", MacroAction.Copy),
            Pair("Paste", MacroAction.Paste)
        )

        val defaultButtons = (0 until maxButtons).map { pos ->
            val actionPair = actionsList[pos % actionsList.size]
            MacroButton(
                id = "${newPageId}_btn_${pos}",
                position = pos,
                label = actionPair.first,
                iconName = actionPair.first.lowercase(),
                action = actionPair.second,
                longPressAction = if (actionPair.second is MacroAction.Undo) MacroAction.Redo else null,
                repeatEnabled = false,
                enabled = true,
                hidden = false
            )
        }

        val newPage = MacroPage(
            id = newPageId,
            name = name,
            orderIndex = currentWorkspace.macroPages.size,
            buttons = defaultButtons
        )

        val updatedPages = currentWorkspace.macroPages + newPage
        viewModelScope.launch {
            workspaceRepository.saveWorkspace(
                currentWorkspace.copy(macroPages = updatedPages)
            )
            _activePageId.value = newPageId
        }
    }

    fun deletePage(pageId: String) {
        val currentWorkspace = getActiveWorkspaceItem() ?: return
        if (currentWorkspace.macroPages.size <= 1) return

        val updatedPages = currentWorkspace.macroPages
            .filter { it.id != pageId }
            .mapIndexed { index, page -> page.copy(orderIndex = index) }

        viewModelScope.launch {
            workspaceRepository.saveWorkspace(
                currentWorkspace.copy(macroPages = updatedPages)
            )
            if (_activePageId.value == pageId) {
                _activePageId.value = updatedPages.firstOrNull()?.id ?: ""
            }
        }
    }

    fun renamePage(pageId: String, newName: String) {
        val currentWorkspace = getActiveWorkspaceItem() ?: return
        val updatedPages = currentWorkspace.macroPages.map { page ->
            if (page.id == pageId) page.copy(name = newName) else page
        }

        viewModelScope.launch {
            workspaceRepository.saveWorkspace(
                currentWorkspace.copy(macroPages = updatedPages)
            )
        }
    }

    fun reorderPageUp(pageId: String) {
        val currentWorkspace = getActiveWorkspaceItem() ?: return
        val pages = currentWorkspace.macroPages
        val index = pages.indexOfFirst { it.id == pageId }
        if (index <= 0) return

        val updatedPages = pages.toMutableList()
        val temp = updatedPages[index]
        updatedPages[index] = updatedPages[index - 1].copy(orderIndex = index)
        updatedPages[index - 1] = temp.copy(orderIndex = index - 1)

        viewModelScope.launch {
            workspaceRepository.saveWorkspace(
                currentWorkspace.copy(macroPages = updatedPages.sortedBy { it.orderIndex })
            )
        }
    }

    fun reorderPageDown(pageId: String) {
        val currentWorkspace = getActiveWorkspaceItem() ?: return
        val pages = currentWorkspace.macroPages
        val index = pages.indexOfFirst { it.id == pageId }
        if (index == -1 || index >= pages.lastIndex) return

        val updatedPages = pages.toMutableList()
        val temp = updatedPages[index]
        updatedPages[index] = updatedPages[index + 1].copy(orderIndex = index)
        updatedPages[index + 1] = temp.copy(orderIndex = index + 1)

        viewModelScope.launch {
            workspaceRepository.saveWorkspace(
                currentWorkspace.copy(macroPages = updatedPages.sortedBy { it.orderIndex })
            )
        }
    }

    fun updateButton(pageId: String, updatedButton: MacroButton) {
        val currentWorkspace = getActiveWorkspaceItem() ?: return
        val updatedPages = currentWorkspace.macroPages.map { page ->
            if (page.id == pageId) {
                val updatedButtons = page.buttons.map { btn ->
                    if (btn.position == updatedButton.position) updatedButton else btn
                }
                page.copy(buttons = updatedButtons)
            } else {
                page
            }
        }

        viewModelScope.launch {
            workspaceRepository.saveWorkspace(
                currentWorkspace.copy(macroPages = updatedPages)
            )
        }
    }

    fun selectPage(pageId: String) {
        _activePageId.value = pageId
    }

    fun triggerAction(action: MacroAction) {
        viewModelScope.launch {
            sendActionCommand(action)
        }
    }

    fun startRepeatAction(action: MacroAction) {
        stopRepeatAction()
        repeatJob = viewModelScope.launch {
            while (true) {
                sendActionCommand(action)
                delay(100)
            }
        }
    }

    fun stopRepeatAction() {
        repeatJob?.cancel()
        repeatJob = null
    }

    private suspend fun sendActionCommand(action: MacroAction) {
        when (action) {
            MacroAction.Undo -> connectionRepository.commandService.undo()
            MacroAction.Redo -> connectionRepository.commandService.redo()
            MacroAction.Save -> connectionRepository.commandService.save()
            MacroAction.Brush -> connectionRepository.commandService.brush()
            MacroAction.Eraser -> connectionRepository.commandService.eraser()
            MacroAction.Fill -> connectionRepository.commandService.fill()
            MacroAction.Selection -> connectionRepository.commandService.selection()
            MacroAction.Transform -> connectionRepository.commandService.transform()
            MacroAction.Copy -> connectionRepository.commandService.copy()
            MacroAction.Paste -> connectionRepository.commandService.paste()
            is MacroAction.CustomShortcut -> {
                val allKeys = action.modifiers + action.keys
                connectionRepository.commandService.shortcut(allKeys)
            }
        }
    }

    private fun getActiveWorkspaceItem(): WorkspaceItem? {
        val successState = uiState.value as? ControlsUiState.Success
        return successState?.activeWorkspace
    }

    // Creative Controls & Canvas Commands
    fun onOneFingerPan(deltaX: Double, deltaY: Double) {
        handlePanGesture(GestureType.ONE_FINGER_PAN, deltaX, deltaY)
    }

    fun onTwoFingerPan(deltaX: Double, deltaY: Double) {
        handlePanGesture(GestureType.TWO_FINGER_PAN, deltaX, deltaY)
    }

    // Legacy fallback
    fun onPan(deltaX: Double, deltaY: Double) {
        onOneFingerPan(deltaX, deltaY)
    }

    private fun handlePanGesture(gestureType: GestureType, deltaX: Double, deltaY: Double) {
        val state = uiState.value as? ControlsUiState.Success ?: return
        if (!state.isConnected) {
            pendingPanX = 0.0
            pendingPanY = 0.0
            return
        }
        val config = state.activeWorkspace.creativeControlsConfig
        val binding = CreativeControlsTransformHelper.resolveBinding(config, gestureType) ?: return
        if (binding.action != GestureAction.PAN) return

        val (effDx, effDy) = CreativeControlsTransformHelper.calculatePanDelta(deltaX, deltaY, config, binding)
        if (effDx == 0.0 && effDy == 0.0) return

        pendingPanX += effDx
        pendingPanY += effDy
        val now = System.currentTimeMillis()
        if (now - lastPanDispatchTime >= minStreamIntervalMs) {
            flushPan()
        }
    }

    fun flushPan() {
        val state = uiState.value as? ControlsUiState.Success
        if (state == null || !state.isConnected) {
            pendingPanX = 0.0
            pendingPanY = 0.0
            return
        }
        val dx = pendingPanX
        val dy = pendingPanY
        pendingPanX = 0.0
        pendingPanY = 0.0
        lastPanDispatchTime = System.currentTimeMillis()
        if (dx != 0.0 || dy != 0.0) {
            viewModelScope.launch {
                connectionRepository.commandService.pan(dx, dy)
            }
        }
    }

    fun onPinchZoom(zoomFactor: Double) {
        val state = uiState.value as? ControlsUiState.Success ?: return
        if (!state.isConnected) {
            pendingZoomFactor = 1.0
            return
        }
        val config = state.activeWorkspace.creativeControlsConfig
        val binding = CreativeControlsTransformHelper.resolveBinding(config, GestureType.PINCH_ZOOM) ?: return
        if (binding.action != GestureAction.ZOOM) return

        val effectiveFactor = CreativeControlsTransformHelper.calculateZoomAmount(zoomFactor, config, binding)
        if (effectiveFactor == 1.0) return

        pendingZoomFactor *= effectiveFactor
        val now = System.currentTimeMillis()
        if (now - lastZoomDispatchTime >= minStreamIntervalMs) {
            flushZoom()
        }
    }

    // Legacy fallback
    fun onZoom(amount: Double) {
        onPinchZoom(amount)
    }

    fun flushZoom() {
        val state = uiState.value as? ControlsUiState.Success
        if (state == null || !state.isConnected) {
            pendingZoomFactor = 1.0
            return
        }
        val factor = pendingZoomFactor
        pendingZoomFactor = 1.0
        lastZoomDispatchTime = System.currentTimeMillis()
        if (factor != 1.0) {
            viewModelScope.launch {
                connectionRepository.commandService.zoom(factor)
            }
        }
    }

    fun onRotate(angleDegrees: Double) {
        val state = uiState.value as? ControlsUiState.Success ?: return
        if (!state.isConnected) {
            pendingRotateAngle = 0.0
            return
        }
        val config = state.activeWorkspace.creativeControlsConfig
        val binding = CreativeControlsTransformHelper.resolveBinding(config, GestureType.ROTATE_CANVAS) ?: return
        if (binding.action != GestureAction.ROTATE) return

        val effectiveAngle = CreativeControlsTransformHelper.calculateRotationAngle(angleDegrees, config, binding)
        if (effectiveAngle == 0.0) return

        pendingRotateAngle += effectiveAngle
        val now = System.currentTimeMillis()
        if (now - lastRotateDispatchTime >= minStreamIntervalMs) {
            flushRotate()
        }
    }

    fun flushRotate() {
        val state = uiState.value as? ControlsUiState.Success
        if (state == null || !state.isConnected) {
            pendingRotateAngle = 0.0
            return
        }
        val angle = pendingRotateAngle
        pendingRotateAngle = 0.0
        lastRotateDispatchTime = System.currentTimeMillis()
        if (angle != 0.0) {
            viewModelScope.launch {
                connectionRepository.commandService.rotate(angle)
            }
        }
    }

    fun onGestureEnd() {
        flushPan()
        flushZoom()
        flushRotate()
    }

    fun onTwoFingerTap() {
        val state = uiState.value as? ControlsUiState.Success ?: return
        if (!state.isConnected) return
        val config = state.activeWorkspace.creativeControlsConfig
        val binding = CreativeControlsTransformHelper.resolveBinding(config, GestureType.TWO_FINGER_TAP_UNDO) ?: return
        executeGestureAction(binding.action)
    }

    fun onThreeFingerTap() {
        val state = uiState.value as? ControlsUiState.Success ?: return
        if (!state.isConnected) return
        val config = state.activeWorkspace.creativeControlsConfig
        val binding = CreativeControlsTransformHelper.resolveBinding(config, GestureType.THREE_FINGER_TAP_REDO) ?: return
        executeGestureAction(binding.action)
    }

    fun onTapUndo() {
        onTwoFingerTap()
    }

    fun onTapRedo() {
        onThreeFingerTap()
    }

    private fun executeGestureAction(action: GestureAction) {
        viewModelScope.launch {
            when (action) {
                GestureAction.UNDO -> connectionRepository.commandService.undo()
                GestureAction.REDO -> connectionRepository.commandService.redo()
                GestureAction.PAN -> { /* Discrete trigger not applicable */ }
                GestureAction.ZOOM -> connectionRepository.commandService.resetZoom()
                GestureAction.ROTATE -> connectionRepository.commandService.resetRotation()
                GestureAction.NONE -> { /* Do nothing */ }
            }
        }
    }

    fun setZoomSliderValue(newValue: Float) {
        val state = uiState.value as? ControlsUiState.Success ?: return
        val clamped = newValue.coerceIn(0.2f, 4.0f)
        val current = _zoomSliderValue.value
        _zoomSliderValue.value = clamped
        if (state.isConnected && current > 0f) {
            val factor = (clamped / current).toDouble()
            onPinchZoom(factor)
            flushZoom()
        }
    }

    fun zoomIn() {
        val state = uiState.value as? ControlsUiState.Success ?: return
        if (!state.isConnected) return
        _zoomSliderValue.value = (_zoomSliderValue.value * 1.25f).coerceAtMost(4.0f)
        viewModelScope.launch { connectionRepository.commandService.zoomIn() }
    }

    fun zoomOut() {
        val state = uiState.value as? ControlsUiState.Success ?: return
        if (!state.isConnected) return
        _zoomSliderValue.value = (_zoomSliderValue.value * 0.8f).coerceAtLeast(0.2f)
        viewModelScope.launch { connectionRepository.commandService.zoomOut() }
    }

    fun resetZoom() {
        val state = uiState.value as? ControlsUiState.Success ?: return
        if (!state.isConnected) return
        _zoomSliderValue.value = 1.0f
        viewModelScope.launch { connectionRepository.commandService.resetZoom() }
    }

    fun fitCanvas() {
        val state = uiState.value as? ControlsUiState.Success ?: return
        if (!state.isConnected) return
        _zoomSliderValue.value = 1.0f
        viewModelScope.launch { connectionRepository.commandService.fitCanvas() }
    }

    fun resetView() {
        val state = uiState.value as? ControlsUiState.Success ?: return
        if (!state.isConnected) return
        _zoomSliderValue.value = 1.0f
        viewModelScope.launch { connectionRepository.commandService.resetView() }
    }

    fun rotateLeft() {
        val state = uiState.value as? ControlsUiState.Success ?: return
        if (!state.isConnected) return
        viewModelScope.launch { connectionRepository.commandService.rotateLeft() }
    }

    fun rotateRight() {
        val state = uiState.value as? ControlsUiState.Success ?: return
        if (!state.isConnected) return
        viewModelScope.launch { connectionRepository.commandService.rotateRight() }
    }

    fun resetRotation() {
        val state = uiState.value as? ControlsUiState.Success ?: return
        if (!state.isConnected) return
        viewModelScope.launch { connectionRepository.commandService.resetRotation() }
    }

    fun flipHorizontal() {
        val state = uiState.value as? ControlsUiState.Success ?: return
        if (!state.isConnected) return
        viewModelScope.launch { connectionRepository.commandService.flipHorizontal() }
    }

    fun flipVertical() {
        val state = uiState.value as? ControlsUiState.Success ?: return
        if (!state.isConnected) return
        viewModelScope.launch { connectionRepository.commandService.flipVertical() }
    }

    fun updateCreativeControlsConfig(newConfig: CreativeControlsConfig) {
        val currentWorkspace = getActiveWorkspaceItem() ?: return
        viewModelScope.launch {
            workspaceRepository.saveWorkspace(
                currentWorkspace.copy(creativeControlsConfig = newConfig.normalized())
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopRepeatAction()
    }
}

