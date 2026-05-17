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
