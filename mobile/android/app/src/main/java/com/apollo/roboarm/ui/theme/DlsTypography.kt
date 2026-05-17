package com.apollo.roboarm.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.staticCompositionLocalOf

data class RoboArmTypography(
    val titleLg: TextStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold),
    val monoMd: TextStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Bold),
    val body: TextStyle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal)
)

val LocalRoboArmTypography = staticCompositionLocalOf { RoboArmTypography() }
