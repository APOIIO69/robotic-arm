package com.apollo.roboarm.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apollo.roboarm.data.models.LineDto
import com.apollo.roboarm.data.models.toRobotStatus
import com.apollo.roboarm.ui.components.LineCard
import com.apollo.roboarm.ui.components.StatCard
import com.apollo.roboarm.ui.theme.RoboArmColors
import com.apollo.roboarm.ui.theme.RoboArmTheme
import com.apollo.roboarm.ui.theme.RoboArmTypography
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToLine: (Int, String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val colors = RoboArmTheme.colors
    val typography = RoboArmTheme.typography

    LaunchedEffect(Unit) {
        viewModel.handleIntent(HomeIntent.LoadLines)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToLine -> onNavigateToLine(effect.lineId, effect.lineName)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Панель управления",
                        style = typography.titleLg,
                        color = colors.textPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.bg)
            )
        },
        containerColor = colors.bg
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                DashboardSection(state.lines, colors, typography)
            }

            item {
                FilterTabs(
                    selected = state.filter,
                    onSelect = { viewModel.handleIntent(HomeIntent.SetFilter(it)) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            item {
                Text(
                    text = "Производственные линии",
                    style = typography.titleMd,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            if (state.isLoading && state.lines.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colors.blue)
                    }
                }
            } else if (state.error != null && state.lines.isEmpty()) {
                item {
                    Text(
                        text = "Ошибка: ${state.error}",
                        color = colors.critical,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else if (state.filteredLines.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Нет линий по выбранному фильтру",
                            color = colors.textSecondary,
                            style = typography.body
                        )
                    }
                }
            }

            items(state.filteredLines) { line ->
                LineCard(
                    name = line.name,
                    description = line.description,
                    status = line.status.toRobotStatus(),
                    modifier = Modifier.clickable {
                        viewModel.handleIntent(HomeIntent.SelectLine(line.id))
                    }
                )
            }
        }
    }
}

@Composable
private fun FilterTabs(
    selected: LineFilter,
    onSelect: (LineFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RoboArmTheme.colors
    val tabs = listOf(
        LineFilter.ALL      to "Все",
        LineFilter.CRITICAL to "Критические",
        LineFilter.WARNING  to "Предупреждения"
    )
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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

@Composable
private fun DashboardSection(
    lines: List<LineDto>,
    colors: RoboArmColors,
    typography: RoboArmTypography
) {
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
            color = colors.blue,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Ошибки",
            subtitle = "Критические",
            value = criticalLines.toString().padStart(2, '0'),
            color = if (criticalLines > 0) colors.critical else colors.ok,
            modifier = Modifier.weight(1f)
        )
    }
}
