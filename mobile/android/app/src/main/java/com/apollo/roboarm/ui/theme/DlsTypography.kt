package com.apollo.roboarm.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.staticCompositionLocalOf

data class RoboArmTypography(
    val titleLg: TextStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold),
    val titleMd: TextStyle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
    val label: TextStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.08.sp),
    val body: TextStyle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
    val bodySm: TextStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
    val caption: TextStyle = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium),
    val micro: TextStyle = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal),
    val monoLg: TextStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 22.sp, fontWeight = FontWeight.Bold),
    val monoMd: TextStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Bold)
)

val LocalRoboArmTypography = staticCompositionLocalOf { RoboArmTypography() }
