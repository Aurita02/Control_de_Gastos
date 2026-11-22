package com.example.controldegastos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.controldegastos.data.model.Gasto
import com.example.controldegastos.ui.dashboard.DashboardUiState
import java.util.Locale

// USUARIOS DE PRUEBA
data class Usuario(
    val username: String,
    val contrasena: String,
    val nombre: String
)

val usuariosRegistrados = listOf(
    Usuario("sofia", "17012007", "Sofia Rade"),
    Usuario("aura", "02092004", "Aura Rade"),
    Usuario("alexis", "12122024", "Jordan Alexis")
)

// 1. PANTALLA DE INICIO DE SESIÓN
@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Control de Gastos",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                mensajeError = ""
            },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = {
                contrasena = it
                mensajeError = ""
            },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (mensajeError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val usuarioEncontrado =
                    usuariosRegistrados.find {
                        it.username.equals(
                            username.trim(),
                            ignoreCase = true
                        ) && it.contrasena == contrasena
                    }

                if (usuarioEncontrado != null) {
                    onLoginSuccess(usuarioEncontrado.username)
                } else {
                    mensajeError =
                        "Usuario o contraseña incorrectos"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Sesión")
        }
    }
}

// 2. DASHBOARD CON ARQUITECTURA MVVM
@Composable
fun DashboardScreen(
    username: String,
    uiState: DashboardUiState,
    onNavigateToAdd: () -> Unit
) {
    val usuario = usuariosRegistrados.find {
        it.username.equals(username, ignoreCase = true)
    }

    val nombreUsuario = usuario?.nombre ?: "Usuario"

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd
            ) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Gastos de $nombreUsuario",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!uiState.cargando && uiState.error == null) {
                Text(
                    text = "Total de gastos: $${
                        String.format(
                            Locale.US,
                            "%.2f",
                            uiState.totalGastos
                        )
                    }",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            when {
                uiState.cargando -> {
                    CircularProgressIndicator()
                }

                uiState.error != null -> {
                    Text(
                        text = uiState.error,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                uiState.gastos.isEmpty() -> {
                    Text(
                        "No hay gastos registrados para este usuario."
                    )
                }

                else -> {
                    LazyColumn(
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = uiState.gastos,
                            key = { it.id }
                        ) { gasto ->
                            GastoCard(gasto)
                        }
                    }
                }
            }
        }
    }
}

// TARJETA DE GASTO
@Composable
fun GastoCard(gasto: Gasto) {
    val valorFormateado = String.format(
        Locale.US,
        "%.2f",
        gasto.valor
    )

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "${gasto.descripcion} - $$valorFormateado " +
                    "(${gasto.categoria} - ${gasto.metodoPago})",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

// 3. PANTALLA REGISTRAR GASTO
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarGastoScreen(
    onGastoGuardado: () -> Unit
) {
    var valor by remember { mutableStateOf("") }

    val listaCategorias = listOf(
        "Alimentación",
        "Transporte",
        "Materiales Académicos",
        "Servicios",
        "Entretenimiento",
        "Compras Personales"
    )

    var categoriaSeleccionada by remember {
        mutableStateOf(listaCategorias[0])
    }

    var expandirCategoria by remember {
        mutableStateOf(false)
    }

    val listaMetodosPago = listOf(
        "Efectivo",
        "Tarjeta de Débito",
        "Tarjeta de Crédito",
        "Transferencia Bancaria"
    )

    var metodoPagoSeleccionado by remember {
        mutableStateOf(listaMetodosPago[0])
    }

    var expandirMetodoPago by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Registrar Nuevo Gasto",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = valor,
            onValueChange = { valor = it },
            label = { Text("Valor ($)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expandirCategoria,
            onExpandedChange = {
                expandirCategoria = !expandirCategoria
            }
        ) {
            OutlinedTextField(
                value = categoriaSeleccionada,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expandirCategoria
                    )
                },
                colors = ExposedDropdownMenuDefaults
                    .outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandirCategoria,
                onDismissRequest = {
                    expandirCategoria = false
                }
            ) {
                listaCategorias.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            categoriaSeleccionada = item
                            expandirCategoria = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expandirMetodoPago,
            onExpandedChange = {
                expandirMetodoPago = !expandirMetodoPago
            }
        ) {
            OutlinedTextField(
                value = metodoPagoSeleccionado,
                onValueChange = {},
                readOnly = true,
                label = { Text("Método de Pago") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expandirMetodoPago
                    )
                },
                colors = ExposedDropdownMenuDefaults
                    .outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandirMetodoPago,
                onDismissRequest = {
                    expandirMetodoPago = false
                }
            ) {
                listaMetodosPago.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            metodoPagoSeleccionado = item
                            expandirMetodoPago = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onGastoGuardado,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Gasto")
        }
    }
}
