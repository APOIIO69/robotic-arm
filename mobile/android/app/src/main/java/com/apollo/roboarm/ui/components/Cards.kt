package com.apollo.roboarm.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.apollo.roboarm.data.models.RobotStatus
import com.apollo.roboarm.ui.theme.RoboArmTheme

/**
 * StatCard (Dashboard) according to DLS 6.3
 */
@Composable
fun StatCard(
    title: String,
    subtitle: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = RoboArmTheme.colors.card),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, RoboArmTheme.colors.border),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .padding(top = 14.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = RoboArmTheme.typography.titleMd,
                color = RoboArmTheme.colors.textPrimary
            )
            Text(
                text = subtitle,
                style = RoboArmTheme.typography.bodySm,
                color = RoboArmTheme.colors.textSecondary
            )
            Text(
                text = value,
                style = RoboArmTheme.typography.monoMd,
                color = color
            )
        }
    }
}

/**
 * SensorTile (Telemetry) according to DLS 6.6
 */
@Composable
fun SensorTile(
    label: String,
    value: String,
    unit: String,
    status: RobotStatus,
    progress: Float? = null,
    modifier: Modifier = Modifier
) {
    val containerColor = if (status != RobotStatus.OK) {
        getStatusDimColor(status)
    } else {
        RoboArmTheme.colors.card
    }

    val borderColor = if (status != RobotStatus.OK) {
        getStatusColor(status).copy(alpha = 0.3f)
    } else {
        RoboArmTheme.colors.border
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = RoboArmTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = label,
                        style = RoboArmTheme.typography.caption,
                        color = RoboArmTheme.colors.textSecondary
                    )
                }
                StatusDot(status = status, size = 7.dp)
            }

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = RoboArmTheme.typography.monoLg,
                    color = if (status != RobotStatus.OK) getStatusColor(status) else RoboArmTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    style = RoboArmTheme.typography.caption,
                    color = RoboArmTheme.colors.textTertiary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            if (progress != null) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = getStatusColor(status),
                    trackColor = RoboArmTheme.colors.elevated,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF0D0E14)
@Composable
fun CardsPreview() {
    RoboArmTheme(darkTheme = true) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    title = "Активные",
                    subtitle = "Роботы в сети",
                    value = "12",
                    color = RoboArmTheme.colors.ok,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Ошибки",
                    subtitle = "Критические",
                    value = "02",
                    color = RoboArmTheme.colors.critical,
                    modifier = Modifier.weight(1f)
                )
            }

            SensorTile(
                label = "Temperature",
                value = "45.2",
                unit = "°C",
                status = RobotStatus.OK,
                progress = 0.45f,
                modifier = Modifier.fillMaxWidth()
            )

            SensorTile(
                label = "Load",
                value = "88.0",
                unit = "%",
                status = RobotStatus.WARNING,
                progress = 0.88f,
                modifier = Modifier.fillMaxWidth()
            )

            SensorTile(
                label = "Pressure",
                value = "12.4",
                unit = "bar",
                status = RobotStatus.CRITICAL,
                progress = 0.95f,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

