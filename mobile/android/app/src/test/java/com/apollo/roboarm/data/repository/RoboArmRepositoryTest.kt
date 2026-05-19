package com.apollo.roboarm.data.repository

import com.apollo.roboarm.data.models.RobotDetailDto
import com.apollo.roboarm.data.models.RobotTelemetryResponse
import com.apollo.roboarm.data.models.SensorDto
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RoboArmRepositoryTest {

    private fun createMockClient(handler: suspend (HttpRequestData) -> HttpResponseData): HttpClient {
        return HttpClient(MockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            engine {
                addHandler(handler)
            }
        }
    }

    @Test
    fun `getRobotTelemetry returns success when API responds correctly`() = runBlocking {
        val expectedRobot = RobotDetailDto(1, "Arm 1", "L2", "Active")
        val expectedSensors = listOf(SensorDto(1, "Joint 1", "Angle", "deg", 45.0f, "OK"))
        val response = RobotTelemetryResponse(expectedRobot, expectedSensors)

        val client = createMockClient { request ->
            if (request.url.encodedPath == "/api/robots/1/telemetry") {
                respond(
                    content = Json.encodeToString(response),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            } else {
                respondError(HttpStatusCode.NotFound)
            }
        }

        val repository = RoboArmRepository(client)
        val result = repository.getRobotTelemetry(1)

        assertTrue(result.isSuccess)
        assertEquals(response, result.getOrNull())
    }

    @Test
    fun `sendCommand returns success when API responds correctly`() = runBlocking {
        val client = createMockClient { request ->
            if (request.url.encodedPath == "/api/command" && request.method == HttpMethod.Post) {
                respond(
                    content = "",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            } else {
                respondError(HttpStatusCode.NotFound)
            }
        }

        val repository = RoboArmRepository(client)
        val result = repository.sendCommand(1, listOf(45.0f, 90.0f))

        assertTrue(result.isSuccess)
    }
}
