package com.apollo.roboarm.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apollo.roboarm.ui.theme.RoboArmTheme

/**
 * DLS 6.12 EmergencyStop
 */
@Composable
fun EmergencyStopButton(
    isStopped: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isStopped) RoboArmTheme.colors.elevated else RoboArmTheme.colors.criticalDim
    val contentColor = if (isStopped) RoboArmTheme.colors.textSecondary else RoboArmTheme.colors.critical
    val borderColor = if (isStopped) RoboArmTheme.colors.border else RoboArmTheme.colors.critical
    val icon = if (isStopped) Icons.Default.PlayArrow else Icons.Default.Clear
    val text = if (isStopped) "ВОЗОБНОВИТЬ РАБОТУ" else "АВАРИЙНАЯ ОСТАНОВКА"

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(14.dp), // radius-xl
        border = BorderStroke(2.dp, borderColor),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        contentPadding = PaddingValues(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = RoboArmTheme.typography.titleMd.copy(
                    letterSpacing = 0.04.sp
                )
            )
        }
    }
}

/**
 * DLS 6.11 ActionButton
 */
@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false
) {
    val containerColor = if (isActive) RoboArmTheme.colors.blueDim else RoboArmTheme.colors.elevated
    val contentColor = if (isActive) RoboArmTheme.colors.blue else RoboArmTheme.colors.textSecondary
    val borderColor = if (isActive) RoboArmTheme.colors.blue.copy(alpha = 0.5f) else RoboArmTheme.colors.border

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(10.dp), // radius-md
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = text,
                style = RoboArmTheme.typography.bodySm.copy(fontSize = 12.sp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0E14)
@Composable
fun ControlsPreview() {
    RoboArmTheme(darkTheme = true) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            EmergencyStopButton(isStopped = false, onClick = {})
            EmergencyStopButton(isStopped = true, onClick = {})
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton(
                    text = "Torch",
                    icon = Icons.Default.Settings,
                    onClick = {},
                    isActive = false
                )
                ActionButton(
                    text = "Torch",
                    icon = Icons.Default.Settings,
                    onClick = {},
                    isActive = true
                )
            }
        }
    }
}
