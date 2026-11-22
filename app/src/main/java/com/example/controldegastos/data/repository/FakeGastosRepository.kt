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
            // GASTOS DE SOFIA
            Gasto(
                1L, "sofia", "Almuerzo Universitario",
                3.50, "Alimentación", "Efectivo"
            ),
            Gasto(
                2L, "sofia", "Pasaje de Bus",
                0.35, "Transporte", "Efectivo"
            ),
            Gasto(
                3L, "sofia", "Impresiones y Copias",
                1.20, "Materiales Académicos", "Efectivo"
            ),
            Gasto(
                4L, "sofia", "Café de la tarde",
                1.50, "Alimentación", "Tarjeta de Débito"
            ),

            // GASTOS DE AURA
            Gasto(
                5L, "aura", "Pago de Servicio de Luz",
                25.00, "Servicios", "Transferencia Bancaria"
            ),
            Gasto(
                6L, "aura", "Taxi al trabajo",
                4.00, "Transporte", "Efectivo"
            ),
            Gasto(
                7L, "aura", "Almuerzo Ejecutivo",
                5.50, "Alimentación", "Tarjeta de Crédito"
            ),
            Gasto(
                8L, "aura", "Compras Supermercado",
                45.80, "Compras Personales", "Tarjeta de Débito"
            ),

            // GASTOS DE ALEXIS
            Gasto(
                9L, "alexis", "Lámpara UV/LED para uñas",
                18.50, "Compras Personales", "Tarjeta de Débito"
            ),
            Gasto(
                10L, "alexis", "Almuerzo Universitario",
                3.00, "Alimentación", "Efectivo"
            ),
            Gasto(
                11L, "alexis", "Pasajes del mes",
                10.00, "Transporte", "Efectivo"
            ),
            Gasto(
                12L, "alexis", "Suscripción App Web",
                9.99, "Servicios", "Tarjeta de Crédito"
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
