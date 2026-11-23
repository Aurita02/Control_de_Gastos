package com.example.controldegastos.data.repository

import com.example.controldegastos.data.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FakeUsuariosRepository : UsuariosRepository {

    private val usuarios = listOf(
        Usuario(
            username = "sofia",
            contrasena = "17012007",
            nombre = "Sofia Rade"
        ),
        Usuario(
            username = "aura",
            contrasena = "02092004",
            nombre = "Aura Rade"
        ),
        Usuario(
            username = "alexis",
            contrasena = "12122024",
            nombre = "Jordan Alexis"
        )
    )

    override suspend fun autenticarUsuario(
        username: String,
        contrasena: String
    ): Usuario? = withContext(Dispatchers.Default) {

        usuarios.find { usuario ->
            usuario.username.equals(
                username.trim(),
                ignoreCase = true
            ) && usuario.contrasena == contrasena
        }
    }
}
