package com.example.controldegastos


sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object AgregarGasto : Screen("agregar_gasto")
}