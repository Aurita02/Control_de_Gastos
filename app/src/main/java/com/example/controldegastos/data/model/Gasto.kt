package com.example.controldegastos.data.model

data class Gasto(
    val id: Long,
    val username: String,
    val descripcion: String,
    val valor: Double,
    val categoria: String,
    val metodoPago: String
)
