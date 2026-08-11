package com.onimeno.onicanvas.feature.productivity.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.onimeno.onicanvas.feature.connection.data.ConnectionRepository

class ProductivityViewModelFactory(
    private val connectionRepository: ConnectionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(ProductivityViewModel::class.java))
        return ProductivityViewModel(connectionRepository) as T
    }
}
