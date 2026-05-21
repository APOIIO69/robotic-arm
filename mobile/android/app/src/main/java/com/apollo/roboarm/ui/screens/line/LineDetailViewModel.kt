package com.apollo.roboarm.ui.screens.line

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollo.roboarm.data.repository.RoboArmRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LineDetailViewModel(private val repository: RoboArmRepository) : ViewModel() {
    private val _state = MutableStateFlow(LineDetailState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LineDetailEffect>()
    val effect = _effect.asSharedFlow()

    fun handleIntent(intent: LineDetailIntent) {
        when (intent) {
            is LineDetailIntent.Load        -> load(intent.lineId, intent.lineName)
            is LineDetailIntent.SetFilter   -> _state.update { it.copy(filter = intent.filter) }
            is LineDetailIntent.SelectRobot -> viewModelScope.launch {
                _effect.emit(LineDetailEffect.NavigateToRobot(intent.robotId))
            }
        }
    }

    private fun load(lineId: Int, lineName: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, lineName = lineName) }
            repository.getLineRobots(lineId)
                .onSuccess { robots ->
                    _state.update { it.copy(isLoading = false, robots = robots) }
                }
                .onFailure { err ->
                    _state.update { it.copy(isLoading = false, error = err.message) }
                }
        }
    }
}
