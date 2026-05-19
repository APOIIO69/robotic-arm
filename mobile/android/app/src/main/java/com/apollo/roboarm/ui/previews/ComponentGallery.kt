package com.apollo.roboarm.ui.previews

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apollo.roboarm.data.models.RobotStatus
import com.apollo.roboarm.ui.components.*
import com.apollo.roboarm.ui.theme.RoboArmTheme

@Composable
fun ComponentGallery() {
    val colors = RoboArmTheme.colors
    val typography = RoboArmTheme.typography
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SectionTitle("Status Components")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusPill(status = RobotStatus.OK, label = "SYSTEM OK")
            StatusPill(status = RobotStatus.WARNING, label = "WARNING")
            StatusPill(status = RobotStatus.CRITICAL, label = "CRITICAL")
        }

        SectionTitle("Cards")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                title = "Temp",
                subtitle = "Average",
                value = "24°C",
                color = colors.ok,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Load",
                subtitle = "Peak",
                value = "92%",
                color = colors.critical,
                modifier = Modifier.weight(1f)
            )
        }

        SectionTitle("Sensor Tiles")
        SensorTile(
            label = "Joint 1 Position",
            value = "45.2",
            unit = "deg",
            status = RobotStatus.OK,
            progress = 0.45f
        )
        SensorTile(
            label = "Battery Level",
            value = "12.5",
            unit = "%",
            status = RobotStatus.CRITICAL,
            progress = 0.12f
        )

        SectionTitle("List Items")
        LineCard(
            name = "Assembly Line A",
            description = "Active, 4 robots",
            status = RobotStatus.OK,
            icon = Icons.Default.Settings
        )
        LineCard(
            name = "Maintenance Required",
            description = "Error in section 4",
            status = RobotStatus.CRITICAL,
            icon = Icons.Default.Settings
        )

        RobotCard(
            name = "RoboArm X1",
            model = "RX-2000",
            statusText = "Operating normally",
            status = RobotStatus.OK
        )
        RobotCard(
            name = "RoboArm X2",
            model = "RX-2000",
            statusText = "Emergency Stop Active",
            status = RobotStatus.CRITICAL
        )

        SectionTitle("Controls")
        EmergencyStopButton(isStopped = false, onClick = {})
        EmergencyStopButton(isStopped = true, onClick = {})

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionButton(
                text = "CALIBRATE",
                icon = Icons.Default.Settings,
                isActive = false,
                onClick = {}
            )
            ActionButton(
                text = "CALIBRATING",
                icon = Icons.Default.Settings,
                isActive = true,
                onClick = {}
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SectionTitle(title: String) {
    val colors = RoboArmTheme.colors
    val typography = RoboArmTheme.typography
    Column {
        Text(
            text = title,
            style = typography.titleMd,
            color = colors.textTertiary
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = colors.border)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0E14)
@Composable
fun ComponentGalleryPreview() {
    RoboArmTheme(darkTheme = true) {
        Surface(color = RoboArmTheme.colors.bg) {
            ComponentGallery()
        }
    }
}
