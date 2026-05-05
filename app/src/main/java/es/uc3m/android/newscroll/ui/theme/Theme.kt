package es.uc3m.android.newscroll.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color


// 🔹 DARK THEME (sobrio, no negro puro)
private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,

    background = Color(0xFF0F0F10),
    surface = Color(0xFF1C1C1E),

    onSurface = Color.White,
    onSurfaceVariant = Color(0xFF9CA3AF),

    secondary = Secondary,
    outline = Outline
)

// 🔹 LIGHT THEME (clean, moderno)
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,

    background = Background,
    surface = Surface,

    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,

    secondary = Secondary,
    outline = Outline
)

@Composable
fun NewscrollTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // ❌ desactivado para mantener identidad visual
    content: @Composable () -> Unit
) {

    val colorScheme = when {
        // 🔴 Eliminamos dynamic color (Material You rompe diseño consistente)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography, // 👈 importante (lo definiremos luego)
        shapes = Shapes,            // 👈 consistencia global
        content = content
    )
}