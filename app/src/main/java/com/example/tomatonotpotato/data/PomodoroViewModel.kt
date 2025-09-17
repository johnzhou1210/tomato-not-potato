package com.example.tomatonotpotato.data

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

enum class BreakType {
    SHORT_BREAK,
    LONG_BREAK,
    NONE
}

// --- ViewModel ---
class PomodoroViewModel(
    private val dao: PomodoroDao,
    private val userStatsRepository: UserStatsRepository,
    private val appOpenRepository: AppOpenRepository,
    private val pomodoroTimerSettingsFlow: StateFlow<PomodoroTimerSettings>
) : ViewModel() {
    companion object {
        var TODAY = LocalDate.of(2025, 10, 1) // test // For testing only
    }

    val pomodoroTimerSettings: StateFlow<PomodoroTimerSettings> = pomodoroTimerSettingsFlow

    private val _state = MutableStateFlow(
        PomodoroState(
            totalTimeMillis = pomodoroTimerSettings.value.focusDuration * 60 * 1000L,
            timeLeftMillis = pomodoroTimerSettings.value.focusDuration * 60 * 1000L,
            isRunning = false,
            breakType = BreakType.NONE,
            currentPhase = 0,
        )
    )
    val state = _state.asStateFlow()

    private var timerJob: Job? = null


//    val TODAY = LocalDate.now()

    // Database management
    val records =
        dao.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    suspend fun addSession(date: LocalDate) {
        val existing = dao.getByDate(date)
        val updated =
            existing?.copy(completedSessions = existing.completedSessions + 1) ?: PomodoroRecord(
                date,
                1
            )
        dao.insert(updated)
    }

    fun clearAllSessions() {
        viewModelScope.launch {
            dao.deleteAll()
        }
    }

    fun recordSession() {
        viewModelScope.launch {
            addSession(TODAY)
            updateTotalPomodori(userStatsRepository.getTotalPomodori() + 1)
        }
    }


    // Streak management
    val bestStreak = mutableIntStateOf(0)

    private val _oldestDate = MutableStateFlow<LocalDate?>(null)
    private val _totalPomodori = MutableStateFlow(0)
    val oldestDate = _oldestDate.asStateFlow()
    val totalPomodori = _totalPomodori.asStateFlow()

    init {
        viewModelScope.launch {
            var previousSettings: PomodoroTimerSettings? = null

            // Observe flow and update timer when settings change
            pomodoroTimerSettingsFlow.collect { newSettings ->

                if (previousSettings != null) {
                    if (newSettings.focusDuration != previousSettings?.focusDuration && _state.value.breakType == BreakType.NONE) {
                        _state.value = _state.value.copy(
                            totalTimeMillis = newSettings.focusDuration * 60 * 1000L,
                            timeLeftMillis =  if (newSettings.focusDuration * 60 * 1000L < _state.value.timeLeftMillis) newSettings.focusDuration * 60 * 1000L else _state.value.timeLeftMillis
                        )
                        previousSettings = newSettings
                    } else if (newSettings.breakDuration != previousSettings?.breakDuration && _state.value.breakType == BreakType.SHORT_BREAK) {
                        _state.value = _state.value.copy(
                            totalTimeMillis = newSettings.breakDuration * 60 * 1000L,
                            timeLeftMillis =  if (newSettings.breakDuration * 60 * 1000L < _state.value.timeLeftMillis) newSettings.breakDuration * 60 * 1000L else _state.value.timeLeftMillis
                        )
                        previousSettings = newSettings
                    } else if (newSettings.longBreakDuration != previousSettings?.longBreakDuration && _state.value.breakType == BreakType.LONG_BREAK) {
                        _state.value = _state.value.copy(
                            totalTimeMillis = newSettings.longBreakDuration * 60 * 1000L,
                            timeLeftMillis =  if (newSettings.longBreakDuration * 60 * 1000L < _state.value.timeLeftMillis) newSettings.longBreakDuration * 60 * 1000L else _state.value.timeLeftMillis
                        )
                        previousSettings = newSettings
                    }
                }
                if (previousSettings == null) {
                    previousSettings = newSettings
                }

            }
        }
    }



    init {
        viewModelScope.launch {
            _totalPomodori.value = userStatsRepository.getTotalPomodori()
        }
    }

    init {
        viewModelScope.launch {
            _oldestDate.value = appOpenRepository.getOldestDate()
        }
    }

    init {
        viewModelScope.launch {
            bestStreak.intValue = userStatsRepository.getBestStreak()
        }
    }

    fun updateBestStreak(streak: Int) {
        viewModelScope.launch {
            userStatsRepository.updateBestStreak(streak)
            bestStreak.intValue = maxOf(bestStreak.intValue, streak)
        }
    }

    fun updateTotalPomodori(pomodori: Int) {
        viewModelScope.launch {
            userStatsRepository.updateTotalPomodori(pomodori)
            _totalPomodori.value = pomodori
        }
    }


    fun setTimer(mins: Int, isRunning: Boolean, breakType: BreakType, currentPhase: Int) {
        _state.value = PomodoroState(
            totalTimeMillis = mins * 60 * 1000L,
            timeLeftMillis = mins * 60 * 1000L,
            isRunning = isRunning,
            breakType = breakType,
            currentPhase = currentPhase
        )
    }

    fun toggleTimer() {
        if (_state.value.isRunning) pauseTimer() else startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        _state.value = _state.value.copy(isRunning = true)

        timerJob = viewModelScope.launch {
            while (_state.value.timeLeftMillis >= 0 && _state.value.isRunning) {
                delay(1000) // 16L
                _state.value = _state.value.copy(
                    timeLeftMillis = _state.value.timeLeftMillis - 1000 // 16L
                )
                Log.d("PomodoroViewModel", "Timer: ${_state.value}")
            }
            Log.d("PomodoroViewModel", "Timer finished. printing before call: ${_state.value}")
            if (_state.value.timeLeftMillis <= 0) onTimerFinished()
            Log.d("PomodoroViewModel", "Timer finished. printing after call: ${_state.value}")
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _state.value = _state.value.copy(isRunning = false)
    }

    fun resetTimer() {
        timerJob?.cancel()
        setTimer(pomodoroTimerSettings.value.focusDuration, false, BreakType.NONE, 0)
    }

    fun advanceToNextPhase() {
        pauseTimer()
        onTimerFinished(force = true)
    }


    private fun onTimerFinished(force: Boolean = false) {
        when {
            _state.value.breakType == BreakType.SHORT_BREAK -> {
                setTimer(
                    mins = pomodoroTimerSettings.value.focusDuration,
                    isRunning = false,
                    breakType = BreakType.NONE,
                    currentPhase = _state.value.currentPhase
                )
                if (pomodoroTimerSettings.value.autoStartFocusAfterBreak) {
                    startTimer()
                    Log.d( "PomodoroTimer","Autostarted focus 1")
                } else {
                    Log.d( "PomodoroTimer","Focus not autostarted 1")
                }
            }

            _state.value.breakType == BreakType.LONG_BREAK -> {
                resetTimer()
                if (pomodoroTimerSettings.value.autoStartFocusAfterLongBreak) {
                    startTimer()
                    Log.d( "PomodoroTimer","Autostarted focus 2")
                } else {
                    Log.d( "PomodoroTimer","Focus not autostarted 2")
                }
            }

            _state.value.currentPhase < pomodoroTimerSettings.value.pomodorosBeforeLongBreak - 1 -> {
                if (!force) recordSession()
                setTimer(
                    mins = pomodoroTimerSettings.value.breakDuration,
                    isRunning = false,
                    breakType = BreakType.SHORT_BREAK,
                    currentPhase = _state.value.currentPhase + 1
                )
                if (pomodoroTimerSettings.value.autoStartBreak) {
                    startTimer()
                    Log.d( "PomodoroTimer","Autostarted break 3")
                } else {
                    Log.d( "PomodoroTimer","Break not autostarted 3")
                }

            }

            _state.value.currentPhase == pomodoroTimerSettings.value.pomodorosBeforeLongBreak - 1 -> {
                if (!force) recordSession()
                setTimer(
                    mins = pomodoroTimerSettings.value.longBreakDuration,
                    isRunning = false,
                    breakType = BreakType.LONG_BREAK,
                    currentPhase = _state.value.currentPhase + 1
                )
                if (pomodoroTimerSettings.value.autoStartBreak) {
                    startTimer()
                    Log.d( "PomodoroTimer","Autostarted break 4")
                } else {
                    Log.d( "PomodoroTimer","Break not autostarted 4")
                }
            }

            else -> {
                throw IllegalStateException("Invalid state: ${_state.value}")
            }
        }
        Log.d("PomodoroViewModel", "onTimerFinished: ${_state.value}")

    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

data class PomodoroState(
    val totalTimeMillis: Long,
    val timeLeftMillis: Long,
    val isRunning: Boolean,
    val breakType: BreakType,
    val currentPhase: Int
)