package com.apollo.roboarm.data.repository

import com.apollo.roboarm.data.models.CommandRequest
import com.apollo.roboarm.data.models.LineDto
import com.apollo.roboarm.data.models.LinesResponse
import com.apollo.roboarm.data.models.LineRobotsResponse
import com.apollo.roboarm.data.models.RobotSummaryDto
import com.apollo.roboarm.data.models.RobotTelemetryResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class RoboArmRepository(private val client: HttpClient) {
    suspend fun getLines(): Result<List<LineDto>> = runCatching {
        client.get("/api/lines").body<LinesResponse>().lines
    }

    suspend fun getLineRobots(lineId: Int): Result<List<RobotSummaryDto>> = runCatching {
        client.get("/api/lines/$lineId/robots").body<LineRobotsResponse>().robots
    }

    suspend fun getRobotTelemetry(robotId: Int): Result<RobotTelemetryResponse> = runCatching {
        client.get("/api/robots/$robotId/telemetry").body<RobotTelemetryResponse>()
    }

    suspend fun sendCommand(robotId: Int, angles: List<Float>): Result<Unit> = runCatching {
        client.post("/api/command") {
            setBody(CommandRequest(robot_id = robotId, angles = angles))
            contentType(ContentType.Application.Json)
        }.body()
    }
}
