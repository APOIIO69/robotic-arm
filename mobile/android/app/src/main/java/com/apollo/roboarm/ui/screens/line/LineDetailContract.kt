package com.apollo.roboarm.ui.screens.line

import com.apollo.roboarm.data.models.RobotStatus
import com.apollo.roboarm.data.models.RobotSummaryDto
import com.apollo.roboarm.data.models.toRobotStatus

enum class RobotFilter { ALL, CRITICAL, WARNING }

data class LineDetailState(
    val isLoading: Boolean = false,
    val lineName: String = "",
    val robots: List<RobotSummaryDto> = emptyList(),
    val filter: RobotFilter = RobotFilter.ALL,
    val error: String? = null
) {
    val filteredRobots: List<RobotSummaryDto> get() = when (filter) {
        RobotFilter.ALL      -> robots
        RobotFilter.CRITICAL -> robots.filter { it.status.uppercase() == "CRITICAL" }
        RobotFilter.WARNING  -> robots.filter { it.status.uppercase() == "WARNING" }
    }

    val lineStatus: RobotStatus get() = robots.fold(RobotStatus.OK) { worst, robot ->
        val s = robot.status.toRobotStatus()
        when {
            worst == RobotStatus.CRITICAL            -> worst
            s     == RobotStatus.CRITICAL            -> s
            worst == RobotStatus.WARNING             -> worst
            s     == RobotStatus.WARNING             -> s
            else                                     -> worst
        }
    }
}

sealed class LineDetailIntent {
    data class Load(val lineId: Int, val lineName: String) : LineDetailIntent()
    data class SetFilter(val filter: RobotFilter) : LineDetailIntent()
    data class SelectRobot(val robotId: Int) : LineDetailIntent()
}

sealed class LineDetailEffect {
    data class NavigateToRobot(val robotId: Int) : LineDetailEffect()
}
