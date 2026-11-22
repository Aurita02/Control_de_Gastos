package com.example.controldegastos.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controldegastos.data.model.Gasto
import com.example.controldegastos.data.repository.GastosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val gastos: List<Gasto> = emptyList(),
    val totalGastos: Double = 0.0,
    val cargando: Boolean = true,
    val error: String? = null
)

class DashboardViewModel(
    private val repository: GastosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DashboardUiState()
    )

    val uiState: StateFlow<DashboardUiState> =
        _uiState.asStateFlow()

    fun cargarGastos(username: String) {

        viewModelScope.launch {

            _uiState.value = DashboardUiState(
                cargando = true
            )

            try {
                repository.obtenerGastos(username)
                    .collect { lista ->

                        _uiState.value = DashboardUiState(
                            gastos = lista,
                            totalGastos = lista.sumOf {
                                it.valor
                            },
                            cargando = false
                        )
                    }

            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) {
                    throw e
                }

                _uiState.value = DashboardUiState(
                    cargando = false,
                    error = e.message
                        ?: "Error al cargar los gastos"
                )
            }
        }
    }
}
