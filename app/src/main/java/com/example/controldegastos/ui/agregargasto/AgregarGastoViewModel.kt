package com.example.controldegastos.ui.agregargasto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controldegastos.data.model.Gasto
import com.example.controldegastos.data.repository.GastosRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AgregarGastoUiState(
    val guardando: Boolean = false,
    val guardadoExitosamente: Boolean = false,
    val error: String? = null
)

class AgregarGastoViewModel(
    private val repository: GastosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AgregarGastoUiState())

    val uiState: StateFlow<AgregarGastoUiState> =
        _uiState.asStateFlow()

    fun guardarGasto(
        username: String,
        descripcion: String,
        valor: String,
        categoria: String,
        metodoPago: String
    ) {
        if (_uiState.value.guardando) return

        val valorNumerico = valor.toDoubleOrNull()

        if (descripcion.isBlank()) {
            _uiState.update {
                it.copy(error = "Ingrese una descripción")
            }
            return
        }

        if (valorNumerico == null || valorNumerico <= 0.0 ||
            !valorNumerico.isFinite()
        ) {
            _uiState.update {
                it.copy(error = "Ingrese un valor válido")
            }
            return
        }

        viewModelScope.launch {
            _uiState.value = AgregarGastoUiState(
                guardando = true
            )

            try {
                val nuevoGasto = Gasto(
                    id = 0L,
                    username = username,
                    descripcion = descripcion.trim(),
                    valor = valorNumerico,
                    categoria = categoria,
                    metodoPago = metodoPago
                )

                repository.agregarGasto(nuevoGasto)

                _uiState.value = AgregarGastoUiState(
                    guardadoExitosamente = true
                )

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = AgregarGastoUiState(
                    error = e.message
                        ?: "No se pudo guardar el gasto"
                )
            }
        }
    }
}
