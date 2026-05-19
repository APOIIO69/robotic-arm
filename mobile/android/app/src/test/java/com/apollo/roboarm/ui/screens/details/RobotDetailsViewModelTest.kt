package com.apollo.roboarm.ui.screens.details

import com.apollo.roboarm.data.models.RobotDetailDto
import com.apollo.roboarm.data.models.RobotTelemetryResponse
import com.apollo.roboarm.data.models.SensorDto
import com.apollo.roboarm.data.repository.RoboArmRepository
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RobotDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createMockRepository(handler: suspend (HttpRequestData) -> HttpResponseData): RoboArmRepository {
        val client = HttpClient(MockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            engine {
                addHandler(handler)
            }
        }
        return RoboArmRepository(client)
    }

    @Test
    fun `LoadTelemetry intent updates state with telemetry data`() = runTest {
        val expectedRobot = RobotDetailDto(1, "Arm 1", "L2", "Active")
        val expectedSensors = listOf(SensorDto(1, "Joint 1", "Angle", "deg", 45.0f, "OK"))
        val response = RobotTelemetryResponse(expectedRobot, expectedSensors)

        val repository = createMockRepository { request ->
            respond(
                content = Json.encodeToString(response),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val viewModel = RobotDetailsViewModel(repository)
        viewModel.handleIntent(RobotDetailsIntent.LoadTelemetry(1))

        advanceUntilIdle()

        assertEquals(expectedRobot, viewModel.state.value.robot)
        assertEquals(expectedSensors, viewModel.state.value.sensors)
    }
}
