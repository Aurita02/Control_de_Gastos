package com.example.controldegastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.controldegastos.di.AppContainer
import com.example.controldegastos.di.ViewModelFactory
import com.example.controldegastos.ui.dashboard.DashboardViewModel
import com.example.controldegastos.ui.theme.ControlDeGastosTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val appContainer = remember {
                AppContainer()
            }

            val dashboardFactory = remember(appContainer) {
                ViewModelFactory(
                    appContainer.gastosRepository
                )
            }

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

                        // 1. LOGIN
                        composable(Screen.Login.route) {

                            LoginScreen(
                                onLoginSuccess = { username ->

                                    navController.navigate(
                                        "${Screen.Dashboard.route}/$username"
                                    )
                                }
                            )
                        }

                        // 2. DASHBOARD
                        composable(
                            route = "${Screen.Dashboard.route}/{username}",
                            arguments = listOf(
                                navArgument("username") {
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->

                            val username =
                                backStackEntry.arguments
                                    ?.getString("username") ?: ""

                            val dashboardViewModel:
                                    DashboardViewModel = viewModel(
                                factory = dashboardFactory
                            )

                            val uiState = dashboardViewModel
                                .uiState
                                .collectAsStateWithLifecycle()

                            LaunchedEffect(
                                dashboardViewModel,
                                username
                            ) {
                                dashboardViewModel.cargarGastos(
                                    username
                                )
                            }

                            DashboardScreen(
                                username = username,
                                uiState = uiState.value,
                                onNavigateToAdd = {
                                    navController.navigate(
                                        Screen.AgregarGasto.route
                                    )
                                }
                            )
                        }

                        // 3. AGREGAR GASTO
                        composable(
                            Screen.AgregarGasto.route
                        ) {

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
