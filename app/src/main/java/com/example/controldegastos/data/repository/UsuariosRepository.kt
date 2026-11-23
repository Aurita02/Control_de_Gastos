package com.example.controldegastos.data.repository

import com.example.controldegastos.data.model.Usuario

interface UsuariosRepository {

    suspend fun autenticarUsuario(
        username: String,
        contrasena: String
    ): Usuario?
}