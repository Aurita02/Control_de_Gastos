package com.example.controldegastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    // Controldor de la navegación entre pantallas
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login.route
                    ) {
                        // 1. Ruta de Inicio de Sesión
                        composable(Screen.Login.route) {
                            LoginScreen(
                                onLoginSuccess = { username ->
                                    // Al iniciar sesión correctamente, navega al dashboard pasando el usuario
                                    navController.navigate("${Screen.Dashboard.route}/$username")
                                }
                            )
                        }

                        // 2. Ruta del Dashboard / Lista de Gastos (recibe el usuario por parámetro)
                        composable(
                            route = "${Screen.Dashboard.route}/{username}",
                            arguments = listOf(navArgument("username") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val username = backStackEntry.arguments?.getString("username") ?: ""
                            DashboardScreen(
                                username = username,
                                onNavigateToAdd = {
                                    // Navega a la pantalla para agregar un nuevo gasto
                                    navController.navigate(Screen.AgregarGasto.route)
                                }
                            )
                        }

                        // 3. Ruta de Registrar Gasto
                        composable(Screen.AgregarGasto.route) {
                            AgregarGastoScreen(
                                onGastoGuardado = {
                                    // Vuelve a la pantalla anterior (Dashboard) al guardar
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}