package com.apollo.roboarm.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apollo.roboarm.data.models.RobotStatus
import com.apollo.roboarm.ui.theme.RoboArmTheme

/**
 * DLS 6.4 LineCard
 */
@Composable
fun LineCard(
    name: String,
    description: String,
    status: RobotStatus,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Settings
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = RoboArmTheme.colors.card),
        shape = RoundedCornerShape(14.dp), // radius-xl
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon plate
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(RoboArmTheme.colors.elevated, RoundedCornerShape(10.dp)), // radius-md
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = RoboArmTheme.colors.textSecondary
                )
            }

            // Text content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = RoboArmTheme.typography.titleMd,
                    color = RoboArmTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = description,
                    style = RoboArmTheme.typography.caption,
                    color = if (status == RobotStatus.CRITICAL) RoboArmTheme.colors.critical else RoboArmTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 3 dots: filled for the active status level, offline for others
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                StatusDot(
                    status = if (status == RobotStatus.CRITICAL) RobotStatus.CRITICAL else RobotStatus.OFFLINE,
                    size = 7.dp
                )
                StatusDot(
                    status = if (status == RobotStatus.WARNING) RobotStatus.WARNING else RobotStatus.OFFLINE,
                    size = 7.dp
                )
                StatusDot(
                    status = if (status == RobotStatus.OK) RobotStatus.OK else RobotStatus.OFFLINE,
                    size = 7.dp
                )
            }
        }
    }
}

/**
 * DLS 6.5 RobotCard
 */
@Composable
fun RobotCard(
    name: String,
    model: String,
    statusText: String,
    status: RobotStatus,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Settings
) {
    val borderColor = if (status != RobotStatus.OK) {
        getStatusColor(status).copy(alpha = 0.3f)
    } else {
        RoboArmTheme.colors.border
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = RoboArmTheme.colors.card),
        shape = RoundedCornerShape(14.dp), // radius-xl
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon plate
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(getStatusDimColor(status), RoundedCornerShape(10.dp)), // radius-md
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    tint = getStatusColor(status)
                )
            }

            // Text content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = RoboArmTheme.typography.titleMd,
                    color = RoboArmTheme.colors.textPrimary
                )
                Text(
                    text = model,
                    style = RoboArmTheme.typography.caption,
                    color = RoboArmTheme.colors.textTertiary
                )
                Text(
                    text = statusText,
                    style = RoboArmTheme.typography.caption,
                    color = if (status == RobotStatus.OK) RoboArmTheme.colors.ok else getStatusColor(status)
                )
            }

            // Right part: StatusPill + ChevronRight
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusPill(status = status, label = status.name)
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = RoboArmTheme.colors.textTertiary
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0E14)
@Composable
fun ListsPreview() {
    RoboArmTheme(darkTheme = true) {
        Column(modifier = Modifier.padding(16.dp)) {
            LineCard(
                name = "Сборочная линия A1",
                description = "4 робота, 2 в работе",
                status = RobotStatus.OK
            )
            Spacer(modifier = Modifier.height(8.dp))
            LineCard(
                name = "Покрасочный цех B2",
                description = "Критическая ошибка: Перегрев",
                status = RobotStatus.CRITICAL
            )
            Spacer(modifier = Modifier.height(16.dp))
            RobotCard(
                name = "Kuka KR-16",
                model = "Industrial Arm v2",
                statusText = "В работе",
                status = RobotStatus.OK
            )
            Spacer(modifier = Modifier.height(8.dp))
            RobotCard(
                name = "Fanuc M-20iA",
                model = "High Speed Picker",
                statusText = "Низкое давление",
                status = RobotStatus.WARNING
            )
        }
    }
}
