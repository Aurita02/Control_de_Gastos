package com.example.controldegastos.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.controldegastos.data.repository.GastosRepository
import com.example.controldegastos.data.repository.UsuariosRepository
import com.example.controldegastos.ui.agregargasto.AgregarGastoViewModel
import com.example.controldegastos.ui.dashboard.DashboardViewModel
import com.example.controldegastos.ui.login.LoginViewModel

class ViewModelFactory(
    private val gastosRepository: GastosRepository,
    private val usuariosRepository: UsuariosRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(
                DashboardViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(gastosRepository) as T
        }

        if (modelClass.isAssignableFrom(
                AgregarGastoViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return AgregarGastoViewModel(gastosRepository) as T
        }

        if (modelClass.isAssignableFrom(
                LoginViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(usuariosRepository) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}
