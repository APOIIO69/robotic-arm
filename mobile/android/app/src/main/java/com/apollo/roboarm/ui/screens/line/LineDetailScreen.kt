package com.apollo.roboarm.ui.screens.line

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apollo.roboarm.data.models.toRobotStatus
import com.apollo.roboarm.ui.components.NavIconButton
import com.apollo.roboarm.ui.components.RobotCard
import com.apollo.roboarm.ui.components.StatusPill
import com.apollo.roboarm.ui.theme.RoboArmTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineDetailScreen(
    lineId: Int,
    lineName: String,
    onBack: () -> Unit,
    onNavigateToRobot: (Int) -> Unit,
    viewModel: LineDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val colors = RoboArmTheme.colors
    val typography = RoboArmTheme.typography

    LaunchedEffect(lineId) {
        viewModel.handleIntent(LineDetailIntent.Load(lineId, lineName))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LineDetailEffect.NavigateToRobot -> onNavigateToRobot(effect.robotId)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.lineName.ifEmpty { lineName },
                        style = typography.titleLg,
                        color = colors.textPrimary
                    )
                },
                navigationIcon = {
                    NavIconButton(
                        icon = Icons.Default.ArrowBack,
                        onClick = onBack,
                        modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                    )
                },
                actions = {
                    if (state.robots.isNotEmpty()) {
                        StatusPill(
                            status = state.lineStatus,
                            label = state.lineStatus.name,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.bg)
            )
        },
        containerColor = colors.bg
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            FilterRow(
                selected = state.filter,
                onSelect = { viewModel.handleIntent(LineDetailIntent.SetFilter(it)) }
            )

            HorizontalDivider(color = colors.border)

            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colors.blue)
                    }
                }
                state.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Ошибка: ${state.error}",
                            color = colors.critical,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                else -> {
                    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                        if (state.filteredRobots.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Нет роботов по выбранному фильтру",
                                        color = colors.textSecondary,
                                        style = typography.body
                                    )
                                }
                            }
                        } else {
                            items(state.filteredRobots) { robot ->
                                RobotCard(
                                    name = robot.name,
                                    model = robot.model,
                                    statusText = robot.status.toStatusText(),
                                    status = robot.status.toRobotStatus(),
                                    modifier = Modifier.clickable {
                                        viewModel.handleIntent(LineDetailIntent.SelectRobot(robot.id))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterRow(selected: RobotFilter, onSelect: (RobotFilter) -> Unit) {
    val colors = RoboArmTheme.colors
    val tabs = listOf(
        RobotFilter.ALL      to "Все",
        RobotFilter.CRITICAL to "Критические",
        RobotFilter.WARNING  to "Предупреждения"
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tabs) { (filter, label) ->
            FilterChip(
                selected = selected == filter,
                onClick = { onSelect(filter) },
                label = { Text(text = label, style = RoboArmTheme.typography.bodySm) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colors.blue,
                    selectedLabelColor = androidx.compose.ui.graphics.Color.White,
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                    labelColor = colors.textSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected == filter,
                    borderColor = if (selected == filter) colors.blue else colors.border,
                    selectedBorderColor = colors.blue
                )
            )
        }
    }
}

private fun String.toStatusText(): String = when (this.lowercase()) {
    "ok"       -> "Работает штатно"
    "warning"  -> "Требует проверки"
    "critical" -> "Критическая ошибка"
    "offline"  -> "Не в сети"
    else       -> this
}
