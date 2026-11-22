package com.example.controldegastos.data.repository

import com.example.controldegastos.data.model.Gasto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class FakeGastosRepository : GastosRepository {

    private val mutex = Mutex()

    private val _gastos = MutableStateFlow(
        listOf(
            Gasto(
                1L, "aura",
                "Pago de Servicio de Luz",
                25.00, "Servicios",
                "Transferencia Bancaria"
            ),
            Gasto(
                2L, "aura",
                "Taxi al trabajo",
                4.00, "Transporte",
                "Efectivo"
            ),
            Gasto(
                3L, "aura",
                "Almuerzo Ejecutivo",
                5.50, "Alimentación",
                "Tarjeta de Crédito"
            ),
            Gasto(
                4L, "aura",
                "Compras Supermercado",
                45.80, "Compras Personales",
                "Tarjeta de Débito"
            )
        )
    )

    override fun obtenerGastos(
        username: String
    ): Flow<List<Gasto>> {

        return _gastos.map { lista ->
            lista.filter {
                it.username.equals(
                    username,
                    ignoreCase = true
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun agregarGasto(
        gasto: Gasto
    ) {
        withContext(Dispatchers.IO) {
            delay(500)

            mutex.withLock {
                val nuevoId =
                    (_gastos.value.maxOfOrNull {
                        it.id
                    } ?: 0L) + 1L

                _gastos.value =
                    _gastos.value + gasto.copy(
                        id = nuevoId
                    )
            }
        }
    }
}
