package com.apollo.roboarm.ui.screens.home

import com.apollo.roboarm.data.models.LineDto

data class HomeState(
    val isLoading: Boolean = false,
    val lines: List<LineDto> = emptyList(),
    val error: String? = null
)

sealed class HomeIntent {
    object LoadLines : HomeIntent()
    data class SelectLine(val lineId: Int) : HomeIntent()
}

sealed class HomeEffect {
    data class NavigateToLine(val lineId: Int) : HomeEffect()
}
