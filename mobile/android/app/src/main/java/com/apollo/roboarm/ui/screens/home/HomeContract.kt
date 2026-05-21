package com.apollo.roboarm.ui.screens.home

import com.apollo.roboarm.data.models.LineDto

enum class LineFilter { ALL, CRITICAL, WARNING }

data class HomeState(
    val isLoading: Boolean = false,
    val lines: List<LineDto> = emptyList(),
    val filter: LineFilter = LineFilter.ALL,
    val error: String? = null
) {
    val filteredLines: List<LineDto> get() = when (filter) {
        LineFilter.ALL      -> lines
        LineFilter.CRITICAL -> lines.filter { it.status.uppercase() == "CRITICAL" }
        LineFilter.WARNING  -> lines.filter { it.status.uppercase() == "WARNING" }
    }
}

sealed class HomeIntent {
    object LoadLines : HomeIntent()
    data class SelectLine(val lineId: Int) : HomeIntent()
    data class SetFilter(val filter: LineFilter) : HomeIntent()
}

sealed class HomeEffect {
    data class NavigateToLine(val lineId: Int, val lineName: String) : HomeEffect()
}
