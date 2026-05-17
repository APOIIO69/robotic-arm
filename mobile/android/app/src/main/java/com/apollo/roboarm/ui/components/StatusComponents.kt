package com.apollo.roboarm.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.apollo.roboarm.data.models.RobotStatus
import com.apollo.roboarm.ui.theme.RoboArmColors
import com.apollo.roboarm.ui.theme.RoboArmTheme

@Composable
fun getStatusColor(status: RobotStatus): Color {
    return getStatusColor(status, RoboArmTheme.colors)
}

@Composable
fun getStatusDimColor(status: RobotStatus): Color {
    return getStatusDimColor(status, RoboArmTheme.colors)
}

fun getStatusColor(status: RobotStatus, colors: RoboArmColors): Color = when (status) {
    RobotStatus.OK -> colors.ok
    RobotStatus.WARNING -> colors.warning
    RobotStatus.CRITICAL -> colors.critical
    RobotStatus.OFFLINE -> colors.offline
}

fun getStatusDimColor(status: RobotStatus, colors: RoboArmColors): Color = when (status) {
    RobotStatus.OK -> colors.okDim
    RobotStatus.WARNING -> colors.warningDim
    RobotStatus.CRITICAL -> colors.criticalDim
    RobotStatus.OFFLINE -> Color.Transparent
}

@Composable
fun StatusDot(
    status: RobotStatus,
    modifier: Modifier = Modifier,
    size: Dp = 8.dp
) {
    val color = getStatusColor(status)
    Box(
        modifier = modifier
            .size(size)
            .background(color, CircleShape)
    )
}

@Composable
fun StatusPill(status: RobotStatus, label: String, modifier: Modifier = Modifier) {
    Surface(
        color = getStatusDimColor(status),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, getStatusColor(status).copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusDot(status, size = 6.dp)
            Box(modifier = Modifier.size(6.dp)) // spacing
            Text(
                text = label,
                color = getStatusColor(status),
                style = RoboArmTheme.typography.caption
            )
        }
    }
}
