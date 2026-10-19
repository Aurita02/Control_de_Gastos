
package com.example.controldegastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.controldegastos.ui.theme.ControlDeGastosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ControlDeGastosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login.route
                    ) {
                        composable(Screen.Login.route) {
                            LoginScreen(
                                onLoginSuccess = { username ->
                                    navController.navigate(
                                        "${Screen.Dashboard.route}/$username"
                                    )
                                }
                            )
                        }

                        composable(
                            route = "${Screen.Dashboard.route}/{username}",
                            arguments = listOf(
                                navArgument("username") {
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            val username =
                                backStackEntry.arguments?.getString("username") ?: ""

                            DashboardScreen(
                                username = username,
                                onNavigateToAdd = {
                                    navController.navigate(
                                        Screen.AgregarGasto.route
                                    )
                                }
                            )
                        }

                        composable(Screen.AgregarGasto.route) {
                            AgregarGastoScreen(
                                onGastoGuardado = {
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
