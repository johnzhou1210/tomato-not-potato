package com.example.tomatonotpotato.data

import android.util.Log
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
    private val appOpenRepository: AppOpenRepository
) : ViewModel() {
    companion object {
        var TODAY = LocalDate.of(2025, 9, 12) // test // For testing only
    }

    private val _state = MutableStateFlow(
        PomodoroState(
            totalTimeMillis = 25 * 60 * 1000L,
            timeLeftMillis = 25 * 60 * 1000L,
            isRunning = false,
            breakType = BreakType.NONE,
            currentPhase = 0
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
        if (timerJob?.isActive == true) return
        _state.value = _state.value.copy(isRunning = true)

        timerJob = viewModelScope.launch {
            while (_state.value.timeLeftMillis > 0 && _state.value.isRunning) {
                delay(16L)
                _state.value = _state.value.copy(
                    timeLeftMillis = _state.value.timeLeftMillis - 16L
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
        setTimer(1, false, BreakType.NONE, 0)
    }

    fun advanceToNextPhase() {
        onTimerFinished()
    }



    private fun onTimerFinished() {

        when {
            _state.value.breakType == BreakType.SHORT_BREAK -> {
                setTimer(
                    25,
                    isRunning = false,
                    breakType = BreakType.NONE,
                    currentPhase = _state.value.currentPhase
                )
            }

            _state.value.breakType == BreakType.LONG_BREAK -> resetTimer()

            _state.value.currentPhase < 3 -> {
                recordSession()
                setTimer(
                    5,
                    isRunning = false,
                    breakType = BreakType.SHORT_BREAK,
                    currentPhase = _state.value.currentPhase + 1
                )
            }

            _state.value.currentPhase == 3 -> {
                recordSession()
                setTimer(
                    30,
                    isRunning = false,
                    breakType = BreakType.LONG_BREAK,
                    currentPhase = _state.value.currentPhase + 1
                )
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
