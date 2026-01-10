package com.example.belajarkmp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.polije.sipeperpolije.theme.md_theme_dark_background
import com.polije.sipeperpolije.theme.md_theme_dark_onBackground
import com.polije.sipeperpolije.theme.md_theme_dark_onPrimary
import com.polije.sipeperpolije.theme.md_theme_dark_onSurface
import com.polije.sipeperpolije.theme.md_theme_dark_onSurfaceVariant
import com.polije.sipeperpolije.theme.md_theme_dark_primary
import com.polije.sipeperpolije.theme.md_theme_dark_surface
import com.polije.sipeperpolije.theme.md_theme_dark_surfaceVariant
import com.polije.sipeperpolije.theme.md_theme_light_background
import com.polije.sipeperpolije.theme.md_theme_light_onBackground
import com.polije.sipeperpolije.theme.md_theme_light_onPrimary
import com.polije.sipeperpolije.theme.md_theme_light_onSurface
import com.polije.sipeperpolije.theme.md_theme_light_onSurfaceVariant
import com.polije.sipeperpolije.theme.md_theme_light_primary
import com.polije.sipeperpolije.theme.md_theme_light_surface
import com.polije.sipeperpolije.theme.md_theme_light_surfaceVariant

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    background = md_theme_dark_background,
    surface = md_theme_dark_surface,
    onBackground = md_theme_dark_onBackground,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant
)

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    background = md_theme_light_background,
    surface = md_theme_light_surface,
    onBackground = md_theme_light_onBackground,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant
)

@Composable
fun MainTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}
