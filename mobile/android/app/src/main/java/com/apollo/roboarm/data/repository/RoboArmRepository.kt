package com.apollo.roboarm.data.repository

import com.apollo.roboarm.data.models.LineDto
import com.apollo.roboarm.data.models.LinesResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class RoboArmRepository(private val client: HttpClient) {
    suspend fun getLines(): Result<List<LineDto>> = runCatching {
        client.get("/api/lines").body<LinesResponse>().lines
    }
}
