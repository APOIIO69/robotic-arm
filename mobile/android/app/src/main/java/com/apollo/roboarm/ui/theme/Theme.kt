package com.apollo.roboarm.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DarkColors.blue,
    background = DarkColors.bg,
    surface = DarkColors.card,
    onBackground = DarkColors.textPrimary,
    onSurface = DarkColors.textPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = LightColors.blue,
    background = LightColors.bg,
    surface = LightColors.card,
    onBackground = LightColors.textPrimary,
    onSurface = LightColors.textPrimary
)

@Composable
fun RoboArmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.bg.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalRoboArmColors provides colors,
        LocalRoboArmTypography provides RoboArmTypography()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}

object RoboArmTheme {
    val colors: RoboArmColors
        @Composable
        get() = LocalRoboArmColors.current

    val typography: RoboArmTypography
        @Composable
        get() = LocalRoboArmTypography.current
}