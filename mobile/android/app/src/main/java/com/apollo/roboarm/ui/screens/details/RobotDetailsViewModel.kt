package com.apollo.roboarm.ui.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollo.roboarm.data.repository.RoboArmRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class RobotDetailsViewModel(private val repository: RoboArmRepository) : ViewModel() {
    private val _state = MutableStateFlow(RobotDetailsState())
    val state = _state.asStateFlow()

    private var pollingJob: Job? = null
    private var commandJob: Job? = null

    fun handleIntent(intent: RobotDetailsIntent) {
        when (intent) {
            is RobotDetailsIntent.LoadTelemetry    -> startPolling(intent.robotId)
            is RobotDetailsIntent.UpdateAxis       -> updateAxis(intent.robotId, intent.index, intent.value)
            is RobotDetailsIntent.ToggleEmergencyStop -> toggleEmergencyStop()
        }
    }

    private fun startPolling(robotId: Int) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            while (isActive) {
                repository.getRobotTelemetry(robotId)
                    .onSuccess { data ->
                        val positionAngles = data.sensors
                            .filter { it.type == "position" }
                            .map { it.value }
                        _state.update { current ->
                            current.copy(
                                isLoading = false,
                                robot = data.robot,
                                sensors = data.sensors,
                                // Only update angles from server if user isn't actively controlling
                                currentAngles = if (current.currentAngles.isEmpty()) positionAngles else current.currentAngles,
                                error = null
                            )
                        }
                    }
                    .onFailure { err ->
                        _state.update { it.copy(isLoading = false, error = err.message) }
                    }
                delay(2000)
            }
        }
    }

    private fun updateAxis(robotId: Int, index: Int, value: Float) {
        // Update slider state immediately for responsive UI
        _state.update { current ->
            val angles = current.currentAngles.toMutableList()
            while (angles.size <= index) angles.add(0.0f)
            angles[index] = value
            current.copy(currentAngles = angles)
        }

        if (_state.value.isEmergencyStopped) return

        // Debounce the network call by 150ms
        commandJob?.cancel()
        commandJob = viewModelScope.launch {
            delay(150)
            val angles = _state.value.currentAngles
            repository.sendCommand(robotId, angles)
        }
    }

    private fun toggleEmergencyStop() {
        _state.update { it.copy(isEmergencyStopped = !it.isEmergencyStopped) }
    }
}
