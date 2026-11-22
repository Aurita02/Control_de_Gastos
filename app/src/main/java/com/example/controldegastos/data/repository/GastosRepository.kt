package com.example.controldegastos.data.repository

import com.example.controldegastos.data.model.Gasto
import kotlinx.coroutines.flow.Flow

interface GastosRepository {

    fun obtenerGastos(username: String): Flow<List<Gasto>>

    suspend fun agregarGasto(gasto: Gasto)
}
