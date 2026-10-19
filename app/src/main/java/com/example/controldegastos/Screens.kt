package com.example.controldegastos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.widthIn

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

// Se esta creando la pantalla de incio de sesion
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
//lo de aca arriba es para el "usuario" y la contraseña, es algo parecido a lo del inicio de jordan.

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
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onLoginSuccess() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Sesión")
        }
    }
}

// 2. DASHBOARD / LISTA DE GASTOS
@Composable
fun DashboardScreen(onNavigateToAdd: () -> Unit) {
    val gastosEjemplo = listOf(
        "Almuerzo Universitario - $3.50 (Alimentación - Efectivo)",
        "Pasaje de Bus - $0.35 (Transporte - Efectivo)",
        "Impresiones - $1.20 (Estudios - Tarjeta)"
    )

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
            Text(text = "Mis Gastos", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(gastosEjemplo) { gasto ->
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

// 3. REGISTRAR / AGREGAR GASTO
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarGastoScreen(onGastoGuardado: () -> Unit) {
    var valor by remember { mutableStateOf("") }

    // Lista de categorías predefinidas
    val listaCategorias = listOf("Alimentación", "Transporte", "Materiales Académicos", "Servicios", "Entretenimiento", "Compras Personales")
    var categoriaSeleccionada by remember { mutableStateOf(listaCategorias[0]) }
    var expandirCategoria by remember { mutableStateOf(false) }

    // Lista de métodos de pago predefinidos
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

        // Campo para ingresar el valor
        OutlinedTextField(
            value = valor,
            onValueChange = { valor = it },
            label = { Text("Valor ($)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Menú desplegable de Categoria
        ExposedDropdownMenuBox(
            expanded = expandirCategoria,
            onExpandedChange = { expandirCategoria = !expandirCategoria }
        ) {
            OutlinedTextField(
                value = categoriaSeleccionada,
                onValueChange = {},
                readOnly = true, // Evita que el usuario escriba
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

        // Menú desplegable de Metodo de pago
        ExposedDropdownMenuBox(
            expanded = expandirMetodoPago,
            onExpandedChange = { expandirMetodoPago = !expandirMetodoPago }
        ) {
            OutlinedTextField(
                value = metodoPagoSeleccionado,
                onValueChange = {},
                readOnly = true, // Evita que el usuario escriba
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

        // Botón para guardar el gasto
        Button(
            onClick = { onGastoGuardado() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Gasto")
        }
    }
}