package com.example.tomatonotpotato.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tomatonotpotato.data.PomodoroState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// --- ViewModel ---
class PomodoroViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        PomodoroState(
            totalTimeMillis = 25 * 60 * 1000L,
            timeLeftMillis = 25 * 60 * 1000L,
            isRunning = false,
            isBreak = false
        )
    )
    val state = _state.asStateFlow()

    private var timerJob: Job? = null

    fun setTimer(mins: Int, isRunning: Boolean, isBreak: Boolean) {
        _state.value = PomodoroState(
            totalTimeMillis = mins * 60 * 1000L,
            timeLeftMillis = mins * 60 * 1000L,
            isRunning = isRunning,
            isBreak = isBreak
        )
    }

    fun toggleTimer() {
        if (_state.value.isRunning) pauseTimer() else startTimer()
    }

    private fun startTimer() {
        if (timerJob?.isActive == true) return
        _state.value = _state.value.copy(isRunning = true)

        timerJob = viewModelScope.launch {
            while (_state.value.timeLeftMillis > 0 && _state.value.isRunning) {
                delay(1000)
                _state.value = _state.value.copy(
                    timeLeftMillis = _state.value.timeLeftMillis - 1000
                )
            }
            if (_state.value.timeLeftMillis <= 0) onTimerFinished()
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _state.value = _state.value.copy(isRunning = false)
    }

    fun resetTimer() {
        timerJob?.cancel()
        setTimer(25, false, false)
    }

    private fun onTimerFinished() {
        if (!_state.value.isBreak) {
            setTimer(5, true, true)
        } else {
            resetTimer()
        }
        startTimer()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
