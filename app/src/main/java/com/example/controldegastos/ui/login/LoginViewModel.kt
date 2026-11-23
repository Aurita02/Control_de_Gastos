package com.example.controldegastos.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controldegastos.data.repository.UsuariosRepository
import kotlinx.coroutines.CancellationException
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

class LoginViewModel(
    private val usuariosRepository: UsuariosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> =
        _uiState.asStateFlow()

    fun iniciarSesion(
        username: String,
        contrasena: String
    ) {

        if (_uiState.value.cargando) return

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

            try {

                val usuario =
                    usuariosRepository.autenticarUsuario(
                        username = username,
                        contrasena = contrasena
                    )

                if (usuario != null) {

                    _uiState.value = LoginUiState(
                        loginExitoso = true,
                        username = usuario.username
                    )

                } else {

                    _uiState.value = LoginUiState(
                        error = "Usuario o contraseña incorrectos"
                    )
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                _uiState.value = LoginUiState(
                    error = "No se pudo iniciar sesión"
                )
            }
        }
    }
}
