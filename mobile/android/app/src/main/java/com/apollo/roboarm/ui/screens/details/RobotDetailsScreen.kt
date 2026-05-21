package com.apollo.roboarm.ui.screens.details

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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.apollo.roboarm.data.models.SensorDto
import com.apollo.roboarm.data.models.toRobotStatus
import com.apollo.roboarm.ui.components.EmergencyStopButton
import com.apollo.roboarm.ui.components.NavIconButton
import com.apollo.roboarm.ui.components.SensorTile
import com.apollo.roboarm.ui.theme.RoboArmTheme
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.bg)
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
                divider = { HorizontalDivider(color = colors.border) }
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
                value = "%.3f".format(sensor.value),
                unit = sensor.unit,
                status = sensor.status.toRobotStatus(),
                progress = sensor.computeProgress()
            )
        }
    }
}

private fun SensorDto.computeProgress(): Float? {
    val max = normal_max ?: return null
    val min = normal_min ?: 0f
    val range = max - min
    if (range <= 0f) return null
    return ((value - min) / range).coerceIn(0f, 1f)
}

@Composable
fun ControlTab(
    state: RobotDetailsState,
    onUpdateAxis: (Int, Float) -> Unit,
    onToggleEmergencyStop: () -> Unit
) {
    val colors = RoboArmTheme.colors
    val typography = RoboArmTheme.typography

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
            style = typography.titleMd,
            color = colors.textPrimary
        )

        val positionSensors = state.sensors.filter { it.type == "position" }
        val axisCount = positionSensors.size.coerceAtLeast(2)

        repeat(axisCount) { index ->
            val currentValue = state.currentAngles.getOrElse(index) { 0f }
            val sensor = positionSensors.getOrNull(index)
            val minRad = sensor?.normal_min ?: -3.14f
            val maxRad = sensor?.normal_max ?: 3.14f

            AxisSlider(
                label = sensor?.label ?: "Ось ${index + 1}",
                value = currentValue,
                valueRange = minRad..maxRad,
                onValueChange = { onUpdateAxis(index, it) },
                enabled = !state.isEmergencyStopped
            )
        }
    }
}

@Composable
fun AxisSlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    enabled: Boolean = true
) {
    val colors = RoboArmTheme.colors
    val typography = RoboArmTheme.typography
    val degrees = Math.toDegrees(value.toDouble()).roundToInt()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.4f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = typography.bodySm,
                color = colors.textSecondary
            )
            Text(
                text = "$degrees°",
                style = typography.monoMd,
                color = colors.blue
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            enabled = enabled,
            colors = SliderDefaults.colors(
                thumbColor = colors.blue,
                activeTrackColor = colors.blue,
                inactiveTrackColor = colors.elevated,
                disabledThumbColor = colors.textTertiary,
                disabledActiveTrackColor = colors.textTertiary
            )
        )
    }
}
