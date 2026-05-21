package com.apollo.roboarm.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollo.roboarm.data.repository.RoboArmRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: RoboArmRepository) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect = _effect.asSharedFlow()

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadLines  -> loadLines()
            is HomeIntent.SetFilter  -> _state.update { it.copy(filter = intent.filter) }
            is HomeIntent.SelectLine -> viewModelScope.launch {
                val name = _state.value.lines.find { it.id == intent.lineId }?.name ?: ""
                _effect.emit(HomeEffect.NavigateToLine(intent.lineId, name))
            }
        }
    }

    private fun loadLines() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repository.getLines()
                .onSuccess { lines ->
                    _state.update { it.copy(isLoading = false, lines = lines) }
                }
                .onFailure { err ->
                    _state.update { it.copy(isLoading = false, error = err.message) }
                }
        }
    }
}
