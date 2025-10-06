package com.tuempresa.inventario.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.tuempresa.inventario.R

// 🎨 Colores base
val Gold = Color(0xFFD4AF37)
val DarkGold = Color(0xFFB8860B)
val Black = Color(0xFF0D0D0D)
val White = Color(0xFFF8F8F8)

// 🖋 Fuente elegante
val ElegantFont = FontFamily(
    Font(R.font.playfair_display_regular, FontWeight.Normal),
    Font(R.font.playfair_display_bold, FontWeight.Bold)
)

// 🌞 Paleta clara
private val LightColorScheme = lightColorScheme(
    primary = Gold,
    onPrimary = Black,
    primaryContainer = Color(0xFFFFE08A),
    secondary = DarkGold,
    onSecondary = Black,
    background = White,
    onBackground = Black,
    surface = White,
    onSurface = Black
)

// 🌙 Paleta oscura
private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = Black,
    primaryContainer = DarkGold,
    secondary = Gold,
    onSecondary = Black,
    background = Black,
    onBackground = White,
    surface = Color(0xFF1A1A1A),
    onSurface = White
)

@Composable
fun InventarioAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Colores dinámicos (Android 12+)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = ElegantFont),
            titleLarge = MaterialTheme.typography.titleLarge.copy(fontFamily = ElegantFont),
            labelLarge = MaterialTheme.typography.labelLarge.copy(fontFamily = ElegantFont)
        ),
        content = content
    )
}
