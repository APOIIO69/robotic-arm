package com.apollo.roboarm.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LineDto(
    val id: Int,
    val name: String,
    val description: String,
    val status: String,
    val robots_count: Int
)

@Serializable
data class LinesResponse(val lines: List<LineDto>)

@Serializable
data class RobotDetailDto(
    val id: Int,
    val name: String,
    val model: String,
    val status: String
)

@Serializable
data class SensorDto(
    val id: Int,
    val label: String,
    val type: String,
    val unit: String,
    val value: Float,
    val status: String
)

@Serializable
data class RobotTelemetryResponse(
    val robot: RobotDetailDto,
    val sensors: List<SensorDto>
)

@Serializable
data class CommandRequest(
    val robot_id: Int,
    val angles: List<Float>
)
