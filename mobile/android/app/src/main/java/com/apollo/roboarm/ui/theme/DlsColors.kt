package com.apollo.roboarm.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.staticCompositionLocalOf

data class RoboArmColors(
    val bg: Color,
    val card: Color,
    val elevated: Color,
    val input: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val border: Color,
    // Accents
    val blue: Color = Color(0xFF4F87FF),
    val ok: Color = Color(0xFF32D74B),
    val warning: Color = Color(0xFFFF9F0A),
    val critical: Color = Color(0xFFFF3B30)
)

val DarkColors = RoboArmColors(
    bg = Color(0xFF0D0E14),
    card = Color(0xFF161820),
    elevated = Color(0xFF1E2028),
    input = Color(0xFF1A1C24),
    textPrimary = Color(0xFFEEEEF3),
    textSecondary = Color(0xFF7B7D8E),
    textTertiary = Color(0xFF4D4F62),
    border = Color(0xFFFFFFFF).copy(alpha = 0.08f)
)

val LightColors = RoboArmColors(
    bg = Color(0xFFF1F2F8),
    card = Color(0xFFFFFFFF),
    elevated = Color(0xFFE8E9F2),
    input = Color(0xFFE4E5EE),
    textPrimary = Color(0xFF13141C),
    textSecondary = Color(0xFF585A6E),
    textTertiary = Color(0xFF8D8FA3),
    border = Color(0x12000000)
)

val LocalRoboArmColors = staticCompositionLocalOf { DarkColors }
