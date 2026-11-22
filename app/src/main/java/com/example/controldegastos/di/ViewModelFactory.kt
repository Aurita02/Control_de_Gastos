package com.example.controldegastos.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.controldegastos.data.repository.GastosRepository
import com.example.controldegastos.ui.dashboard.DashboardViewModel

class ViewModelFactory(
    private val repository: GastosRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(
                DashboardViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}
