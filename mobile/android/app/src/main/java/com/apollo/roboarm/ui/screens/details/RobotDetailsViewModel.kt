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

    fun handleIntent(intent: RobotDetailsIntent) {
        when (intent) {
            is RobotDetailsIntent.LoadTelemetry -> startPolling(intent.robotId)
            is RobotDetailsIntent.UpdateAxis -> sendCommand(intent.robotId, intent.index, intent.value)
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
                        _state.update { it.copy(
                            isLoading = false,
                            robot = data.robot,
                            sensors = data.sensors,
                            isEmergencyStopped = data.robot.status == "critical" // Simplification for MVP
                        ) }
                    }
                    .onFailure { err ->
                        _state.update { it.copy(isLoading = false, error = err.message) }
                    }
                delay(2000)
            }
        }
    }

    private fun sendCommand(robotId: Int, index: Int, value: Float) {
        viewModelScope.launch {
            // In a real app we'd maintain the current angles state
            // For MVP we just send the one changed axis with 0s for others
            val angles = MutableList(6) { 0.0f }
            if (index < 6) angles[index] = value
            repository.sendCommand(robotId, angles)
        }
    }

    private fun toggleEmergencyStop() {
        val robotId = _state.value.robot?.id ?: return
        viewModelScope.launch {
            // Send empty angles or specific command if backend supports it
            // For now, let's assume we send a specific value or just use the existing command
            repository.sendCommand(robotId, emptyList())
        }
    }
}
