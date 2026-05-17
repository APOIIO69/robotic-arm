package com.apollo.roboarm.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apollo.roboarm.data.models.LineDto
import com.apollo.roboarm.data.models.RobotStatus
import com.apollo.roboarm.ui.components.LineCard
import com.apollo.roboarm.ui.components.StatCard
import com.apollo.roboarm.ui.theme.RoboArmTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToLine: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(HomeIntent.LoadLines)
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToLine -> onNavigateToLine(effect.lineId)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Панель управления",
                        style = RoboArmTheme.typography.titleLg,
                        color = RoboArmTheme.colors.textPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RoboArmTheme.colors.background
                )
            )
        },
        containerColor = RoboArmTheme.colors.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                DashboardSection(state.lines)
            }

            item {
                Text(
                    text = "Производственные линии",
                    style = RoboArmTheme.typography.titleMd,
                    color = RoboArmTheme.colors.textPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            if (state.isLoading && state.lines.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp)) {
                        CircularProgressIndicator(color = RoboArmTheme.colors.blue)
                    }
                }
            } else if (state.error != null && state.lines.isEmpty()) {
                item {
                    Text(
                        text = "Ошибка: ${state.error}",
                        color = RoboArmTheme.colors.critical,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            items(state.lines) { line ->
                LineCard(
                    name = line.name,
                    description = line.description,
                    status = mapStringToStatus(line.status),
                    modifier = Modifier.clickable {
                        viewModel.handleIntent(HomeIntent.SelectLine(line.id))
                    }
                )
            }
        }
    }
}

@Composable
fun DashboardSection(lines: List<LineDto>) {
    val totalRobots = lines.sumOf { it.robots_count }
    val criticalLines = lines.count { it.status.uppercase() == "CRITICAL" }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "Роботы",
            subtitle = "Всего в сети",
            value = totalRobots.toString().padStart(2, '0'),
            color = RoboArmTheme.colors.blue,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Ошибки",
            subtitle = "Критические",
            value = criticalLines.toString().padStart(2, '0'),
            color = if (criticalLines > 0) RoboArmTheme.colors.critical else RoboArmTheme.colors.ok,
            modifier = Modifier.weight(1f)
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

