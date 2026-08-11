package com.onimeno.onicanvas.feature.productivity.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onimeno.onicanvas.core.designsystem.components.OniStatus
import com.onimeno.onicanvas.feature.connection.data.ConnectionRepository
import com.onimeno.onicanvas.feature.productivity.state.ProductivityAction
import com.onimeno.onicanvas.feature.productivity.state.ProductivityTool
import com.onimeno.onicanvas.feature.productivity.state.ProductivityUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductivityViewModel(
    private val connectionRepository: ConnectionRepository
) : ViewModel() {
    val uiState: StateFlow<ProductivityUiState> = connectionRepository.state
        .map { snapshot ->
            ProductivityUiState(
                isConnected = snapshot.status == OniStatus.SUCCESS,
                connectionStatus = snapshot.status
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            ProductivityUiState()
        )

    fun selectTool(tool: ProductivityTool) {
        // Tool selection is currently UI-local; this method is reserved for analytics/state restoration.
    }

    fun perform(action: ProductivityAction) {
        if (!uiState.value.isConnected) return
        viewModelScope.launch {
            connectionRepository.commandService.send(action.wireAction)
        }
    }

    fun mouseMove(deltaX: Double, deltaY: Double) {
        if (!uiState.value.isConnected || (deltaX == 0.0 && deltaY == 0.0)) return
        viewModelScope.launch {
            connectionRepository.commandService.mouseMove(deltaX, deltaY)
        }
    }

    fun mouseButton(button: String, pressed: Boolean) {
        if (!uiState.value.isConnected) return
        viewModelScope.launch {
            connectionRepository.commandService.mouseButton(button, pressed)
        }
    }

    fun scroll(deltaX: Double, deltaY: Double) {
        if (!uiState.value.isConnected || (deltaX == 0.0 && deltaY == 0.0)) return
        viewModelScope.launch {
            connectionRepository.commandService.scroll(deltaX, deltaY)
        }
    }

    fun brushPreset(index: Int) {
        if (!uiState.value.isConnected) return
        viewModelScope.launch {
            connectionRepository.commandService.brushPreset(index)
        }
    }
}
