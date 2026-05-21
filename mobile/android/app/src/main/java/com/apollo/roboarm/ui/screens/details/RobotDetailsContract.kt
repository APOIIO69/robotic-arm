package com.apollo.roboarm.ui.screens.details

import com.apollo.roboarm.data.models.RobotDetailDto
import com.apollo.roboarm.data.models.SensorDto

data class RobotDetailsState(
    val isLoading: Boolean = false,
    val robot: RobotDetailDto? = null,
    val sensors: List<SensorDto> = emptyList(),
    val currentAngles: List<Float> = emptyList(),
    val error: String? = null,
    val isEmergencyStopped: Boolean = false
)

sealed class RobotDetailsIntent {
    data class LoadTelemetry(val robotId: Int) : RobotDetailsIntent()
    data class UpdateAxis(val robotId: Int, val index: Int, val value: Float) : RobotDetailsIntent()
    object ToggleEmergencyStop : RobotDetailsIntent()
}

sealed class RobotDetailsEffect {
    data class ShowError(val message: String) : RobotDetailsEffect()
}
