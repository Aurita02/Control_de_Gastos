package com.example.controldegastos.di

import com.example.controldegastos.data.repository.FakeGastosRepository
import com.example.controldegastos.data.repository.FakeUsuariosRepository
import com.example.controldegastos.data.repository.GastosRepository
import com.example.controldegastos.data.repository.UsuariosRepository

class AppContainer {

    val gastosRepository: GastosRepository =
        FakeGastosRepository()

    val usuariosRepository: UsuariosRepository =
        FakeUsuariosRepository()
}
