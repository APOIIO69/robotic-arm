package com.apollo.roboarm.ui.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.apollo.roboarm.data.models.RobotStatus
import com.apollo.roboarm.data.models.SensorDto
import com.apollo.roboarm.ui.components.EmergencyStopButton
import com.apollo.roboarm.ui.components.SensorTile
import com.apollo.roboarm.ui.theme.RoboArmTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RobotDetailsScreen(
    robotId: Int,
    onBack: () -> Unit,
    viewModel: RobotDetailsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val colors = RoboArmTheme.colors
    val typography = RoboArmTheme.typography

    LaunchedEffect(robotId) {
        viewModel.handleIntent(RobotDetailsIntent.LoadTelemetry(robotId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = state.robot?.name ?: "Робот #$robotId",
                            style = typography.titleMd,
                            color = colors.textPrimary
                        )
                        state.robot?.let {
                            Text(
                                text = it.model,
                                style = typography.caption,
                                color = colors.textTertiary
                            )
                        }
                    }
                },
                navigationIcon = {
                    NavIconButton(
                        icon = Icons.Default.ArrowBack,
                        onClick = onBack,
                        modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                    )
                },
                actions = {
                    NavIconButton(
                        icon = Icons.Default.Settings,
                        onClick = { /* TODO */ },
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.bg
                )
            )
        },
        containerColor = colors.bg
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = colors.bg,
                contentColor = colors.blue,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = colors.blue
                    )
                },
                divider = {
                    HorizontalDivider(color = colors.border)
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Телеметрия",
                            style = typography.titleMd,
                            color = if (selectedTab == 0) colors.textPrimary else colors.textSecondary
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Управление",
                            style = typography.titleMd,
                            color = if (selectedTab == 1) colors.textPrimary else colors.textSecondary
                        )
                    }
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (selectedTab == 0) {
                    TelemetryTab(state.sensors)
                } else {
                    ControlTab(
                        state = state,
                        onUpdateAxis = { index, value ->
                            viewModel.handleIntent(RobotDetailsIntent.UpdateAxis(robotId, index, value))
                        },
                        onToggleEmergencyStop = {
                            viewModel.handleIntent(RobotDetailsIntent.ToggleEmergencyStop)
                        }
                    )
                }

                if (state.isLoading && state.sensors.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = colors.blue
                    )
                }

                state.error?.let { error ->
                    Text(
                        text = error,
                        color = colors.critical,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TelemetryTab(sensors: List<SensorDto>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(sensors) { sensor ->
            SensorTile(
                label = sensor.label,
                value = sensor.value.toString(),
                unit = sensor.unit,
                status = mapStringToStatus(sensor.status),
                progress = null // TODO: Calculate progress if applicable
            )
        }
    }
}

@Composable
fun ControlTab(
    state: RobotDetailsState,
    onUpdateAxis: (Int, Float) -> Unit,
    onToggleEmergencyStop: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        EmergencyStopButton(
            isStopped = state.isEmergencyStopped,
            onClick = onToggleEmergencyStop
        )

        Text(
            text = "Оси манипулятора",
            style = RoboArmTheme.typography.titleMd,
            color = RoboArmTheme.colors.textPrimary
        )

        // Axis Sliders Placeholders
        repeat(6) { index ->
            AxisSlider(
                label = "Ось ${index + 1}",
                value = 0f, // TODO: Get from state
                onValueChange = { onUpdateAxis(index, it) }
            )
        }
    }
}

@Composable
fun AxisSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = RoboArmTheme.typography.bodySm,
                color = RoboArmTheme.colors.textSecondary
            )
            Text(
                text = "${value.toInt()}°",
                style = RoboArmTheme.typography.monoMd,
                color = RoboArmTheme.colors.blue
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = -180f..180f,
            colors = SliderDefaults.colors(
                thumbColor = RoboArmTheme.colors.blue,
                activeTrackColor = RoboArmTheme.colors.blue,
                inactiveTrackColor = RoboArmTheme.colors.elevated
            )
        )
    }
}

@Composable
fun NavIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(34.dp)
            .background(RoboArmTheme.colors.elevated, RoundedCornerShape(10.dp))
            .border(1.dp, RoboArmTheme.colors.border, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = RoboArmTheme.colors.textSecondary
        )
    }
}

private fun mapStringToStatus(status: String): RobotStatus {
    return try {
        RobotStatus.valueOf(status.uppercase())
    } catch (e: Exception) {
        RobotStatus.OK
    }
}
