package com.tidylauncher.autoorganizer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightScheme = lightColorScheme(
    primary = Color(0xFF204B57),
    onPrimary = Color.White,
    secondary = Color(0xFFE08A45),
    background = Color(0xFFFFF8F1),
    surface = Color.White,
    onSurface = Color(0xFF1F2933),
)

private val DarkScheme = darkColorScheme(
    primary = Color(0xFF8FD1E0),
    secondary = Color(0xFFF7B267),
    background = Color(0xFF121416),
    surface = Color(0xFF1D2329),
    onSurface = Color(0xFFF1F5F9),
)

@Composable
fun TidyLauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkScheme else LightScheme,
        content = content,
    )
}

