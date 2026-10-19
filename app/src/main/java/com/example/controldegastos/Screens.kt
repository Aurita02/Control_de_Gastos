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

// BASE DE DATOS FICTICIA CON USUARIOS SIMPLES Y SUS GASTOS
data class Usuario(val username: String, val contrasena: String, val nombre: String)

val usuariosRegistrados = listOf(
    Usuario("sofia", "17012007", "Sofia Rade"),
    Usuario("aura", "02092004", "Aura Rade"),
    Usuario("alexis", "12122024", "Jordan Alexis")
)

val gastosPorUsuario = mapOf(
    "sofia" to listOf(
        "Almuerzo Universitario - $3.50 (Alimentación - Efectivo)",
        "Pasaje de Bus - $0.35 (Transporte - Efectivo)",
        "Impresiones y Copias - $1.20 (Materiales Académicos - Efectivo)",
        "Café de la tarde - $1.50 (Alimentación - Tarjeta de Débito)"
    ),
    "aura" to listOf(
        "Pago de Servicio de Luz - $25.00 (Servicios - Transferencia Bancaria)",
        "Taxi al trabajo - $4.00 (Transporte - Efectivo)",
        "Almuerzo Ejecutivo - $5.50 (Alimentación - Tarjeta de Crédito)",
        "Compras Supermercado - $45.80 (Compras Personales - Tarjeta de Débito)"
    ),
    "alexis" to listOf(
        "Lámpara UV/LED para uñas - $18.50 (Compras Personales - Tarjeta de Débito)",
        "Almuerzo Universitario - $3.00 (Alimentación - Efectivo)",
        "Pasajes del mes - $10.00 (Transporte - Efectivo)",
        "Suscripción App Web - $9.99 (Servicios - Tarjeta de Crédito)"
    )
)

// 1. PANTALLA DE INICIO DE SESIÓN
@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) {
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
        Text(text = "Control de Gastos", style = MaterialTheme.typography.headlineLarge)
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
            Text(text = mensajeError, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val usuarioEncontrado = usuariosRegistrados.find {
                    it.username.equals(username.trim(), ignoreCase = true) && it.contrasena == contrasena
                }

                if (usuarioEncontrado != null) {
                    onLoginSuccess(usuarioEncontrado.username)
                } else {
                    mensajeError = "Usuario o contraseña incorrectos"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Sesión")
        }
    }
}

// 2. DASHBOARD / LISTA DE GASTOS
@Composable
fun DashboardScreen(username: String, onNavigateToAdd: () -> Unit) {
    val usuario = usuariosRegistrados.find { it.username == username }
    val nombreUsuario = usuario?.nombre ?: "Usuario"
    val gastosList = gastosPorUsuario[username] ?: emptyList()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToAdd() }) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "Gastos de $nombreUsuario", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if (gastosList.isEmpty()) {
                Text("No hay gastos registrados para este usuario.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(gastosList) { gasto ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = gasto,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

// 3. REGISTRAR / AGREGAR GASTO
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarGastoScreen(onGastoGuardado: () -> Unit) {
    var valor by remember { mutableStateOf("") }

    val listaCategorias = listOf("Alimentación", "Transporte", "Materiales Académicos", "Servicios", "Entretenimiento", "Compras Personales")
    var categoriaSeleccionada by remember { mutableStateOf(listaCategorias[0]) }
    var expandirCategoria by remember { mutableStateOf(false) }

    val listaMetodosPago = listOf("Efectivo", "Tarjeta de Débito", "Tarjeta de Crédito", "Transferencia Bancaria")
    var metodoPagoSeleccionado by remember { mutableStateOf(listaMetodosPago[0]) }
    var expandirMetodoPago by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Text(text = "Registrar Nuevo Gasto", style = MaterialTheme.typography.headlineMedium)
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
            onExpandedChange = { expandirCategoria = !expandirCategoria }
        ) {
            OutlinedTextField(
                value = categoriaSeleccionada,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirCategoria) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandirCategoria,
                onDismissRequest = { expandirCategoria = false }
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
            onExpandedChange = { expandirMetodoPago = !expandirMetodoPago }
        ) {
            OutlinedTextField(
                value = metodoPagoSeleccionado,
                onValueChange = {},
                readOnly = true,
                label = { Text("Método de Pago") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirMetodoPago) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandirMetodoPago,
                onDismissRequest = { expandirMetodoPago = false }
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
            onClick = { onGastoGuardado() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Gasto")
        }
    }
}