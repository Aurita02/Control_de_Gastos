package com.example.controldegastos.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val cargando: Boolean = false,
    val loginExitoso: Boolean = false,
    val username: String = "",
    val error: String? = null
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> =
        _uiState.asStateFlow()

    fun iniciarSesion(
        username: String,
        contrasena: String
    ) {
        if (username.isBlank() || contrasena.isBlank()) {
            _uiState.value = LoginUiState(
                error = "Complete todos los campos"
            )
            return
        }

        viewModelScope.launch {

            _uiState.value = LoginUiState(
                cargando = true
            )

            // Usuarios de prueba de la aplicación
            val usuarios = mapOf(
                "sofia" to "17012007",
                "aura" to "02092004",
                "alexis" to "12122024"
            )

            val usuario = username.trim().lowercase()

            if (usuarios[usuario] == contrasena) {
                _uiState.value = LoginUiState(
                    loginExitoso = true,
                    username = usuario
                )
            } else {
                _uiState.value = LoginUiState(
                    error = "Usuario o contraseña incorrectos"
                )
            }
        }
    }
}
