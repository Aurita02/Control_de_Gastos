
package com.example.controldegastos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = VerdeClaro,
    onPrimary = Blanco,
    secondary = PurpleGrey80,
    onSecondary = TextoPrincipal,
    tertiary = Pink80,
    background = ColorOscuro,
    onBackground = Blanco,
    surface = SuperficieOscura,
    onSurface = Blanco,
    error = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = VerdePrincipal,
    onPrimary = Blanco,
    secondary = VerdeClaro,
    onSecondary = Blanco,
    tertiary = PurpleGrey40,
    background = FondoClaro,
    onBackground = TextoPrincipal,
    surface = Blanco,
    onSurface = TextoPrincipal,
    error = RojoGastos,
    outline = BordeTarjeta
)

@Composable
fun ControlDeGastosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
