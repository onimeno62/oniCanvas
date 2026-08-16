package com.onimeno.onicanvas.feature.color.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.onimeno.onicanvas.feature.color.data.ColorWorkflowRepository
import com.onimeno.onicanvas.feature.connection.data.ConnectionRepository

class ColorWorkflowViewModelFactory(
    private val colorWorkflowRepository: ColorWorkflowRepository,
    private val connectionRepository: ConnectionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ColorWorkflowViewModel::class.java)) {
            return ColorWorkflowViewModel(colorWorkflowRepository, connectionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
