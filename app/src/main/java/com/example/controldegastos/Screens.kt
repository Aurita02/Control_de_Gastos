
package com.example.controldegastos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.controldegastos.data.model.Gasto
import com.example.controldegastos.ui.dashboard.DashboardUiState
import java.util.Locale

// =====================================================
// USUARIOS DE PRUEBA
// Se mantienen para mostrar el nombre en el Dashboard.
// La autenticación se realiza desde UsuariosRepository.
// =====================================================

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

// =====================================================
// 1. PANTALLA DE INICIO DE SESIÓN
// =====================================================

@Composable
fun LoginScreen(
    cargando: Boolean,
    error: String?,
    onIniciarSesion: (String, String) -> Unit
) {

    var username by rememberSaveable {
        mutableStateOf("")
    }

    var contrasena by rememberSaveable {
        mutableStateOf("")
    }

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
            },
            label = {
                Text("Usuario")
            },
            singleLine = true,
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = {
                contrasena = it
            },
            label = {
                Text("Contraseña")
            },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        )

        if (error != null) {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onIniciarSesion(
                    username,
                    contrasena
                )
            },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = if (cargando) {
                    "Iniciando sesión..."
                } else {
                    "Iniciar Sesión"
                }
            )
        }
    }
}

// =====================================================
// 2. DASHBOARD CON ARQUITECTURA MVVM
// =====================================================

@Composable
fun DashboardScreen(
    username: String,
    uiState: DashboardUiState,
    onNavigateToAdd: () -> Unit
) {

    val usuario = usuariosRegistrados.find {
        it.username.equals(
            username,
            ignoreCase = true
        )
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
                        text = "No hay gastos registrados para este usuario."
                    )
                }

                else -> {

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
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

// =====================================================
// TARJETA DE GASTO
// =====================================================

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

// =====================================================
// 3. PANTALLA PARA REGISTRAR GASTOS
// =====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarGastoScreen(
    guardando: Boolean,
    error: String?,
    onGuardarGasto: (
        descripcion: String,
        valor: String,
        categoria: String,
        metodoPago: String
    ) -> Unit
) {

    var descripcion by rememberSaveable {
        mutableStateOf("")
    }

    var valor by rememberSaveable {
        mutableStateOf("")
    }

    var categoria by rememberSaveable {
        mutableStateOf("Alimentación")
    }

    var metodoPago by rememberSaveable {
        mutableStateOf("Efectivo")
    }

    var categoriaExpandida by remember {
        mutableStateOf(false)
    }

    var pagoExpandido by remember {
        mutableStateOf(false)
    }

    val categorias = listOf(
        "Alimentación",
        "Transporte",
        "Educación",
        "Salud",
        "Entretenimiento",
        "Servicios",
        "Compras",
        "Otros"
    )

    val metodosPago = listOf(
        "Efectivo",
        "Tarjeta de débito",
        "Tarjeta de crédito",
        "Transferencia"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Agregar gasto",
            style = MaterialTheme.typography.headlineMedium
        )

        // CAMPO DESCRIPCIÓN
        OutlinedTextField(
            value = descripcion,
            onValueChange = {
                descripcion = it
            },
            label = {
                Text("Descripción")
            },
            singleLine = true,
            enabled = !guardando,
            modifier = Modifier.fillMaxWidth()
        )

        // CAMPO VALOR
        OutlinedTextField(
            value = valor,
            onValueChange = {
                valor = it
            },
            label = {
                Text("Valor del gasto")
            },
            prefix = {
                Text("$")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            singleLine = true,
            enabled = !guardando,
            modifier = Modifier.fillMaxWidth()
        )

        // MENÚ DESPLEGABLE DE CATEGORÍAS
        ExposedDropdownMenuBox(
            expanded = categoriaExpandida,
            onExpandedChange = {
                if (!guardando) {
                    categoriaExpandida = !categoriaExpandida
                }
            }
        ) {

            OutlinedTextField(
                value = categoria,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text("Categoría")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = categoriaExpandida
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = !guardando
                    ),
                enabled = !guardando
            )

            ExposedDropdownMenu(
                expanded = categoriaExpandida,
                onDismissRequest = {
                    categoriaExpandida = false
                }
            ) {

                categorias.forEach { opcion ->

                    DropdownMenuItem(
                        text = {
                            Text(opcion)
                        },
                        onClick = {
                            categoria = opcion
                            categoriaExpandida = false
                        }
                    )
                }
            }
        }

        // MENÚ DESPLEGABLE DE MÉTODOS DE PAGO
        ExposedDropdownMenuBox(
            expanded = pagoExpandido,
            onExpandedChange = {
                if (!guardando) {
                    pagoExpandido = !pagoExpandido
                }
            }
        ) {

            OutlinedTextField(
                value = metodoPago,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text("Método de pago")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = pagoExpandido
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = !guardando
                    ),
                enabled = !guardando
            )

            ExposedDropdownMenu(
                expanded = pagoExpandido,
                onDismissRequest = {
                    pagoExpandido = false
                }
            ) {

                metodosPago.forEach { opcion ->

                    DropdownMenuItem(
                        text = {
                            Text(opcion)
                        },
                        onClick = {
                            metodoPago = opcion
                            pagoExpandido = false
                        }
                    )
                }
            }
        }

        // MOSTRAR ERRORES AL GUARDAR
        if (error != null) {

            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }

        // BOTÓN GUARDAR GASTO
        Button(
            onClick = {
                onGuardarGasto(
                    descripcion,
                    valor,
                    categoria,
                    metodoPago
                )
            },
            enabled = !guardando,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = if (guardando) {
                    "Guardando..."
                } else {
                    "Guardar gasto"
                }
            )
        }
    }
}
