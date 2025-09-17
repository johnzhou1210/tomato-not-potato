package com.example.tomatonotpotato.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.StateFlow

class PomodoroViewModelFactory(
    private val dao: PomodoroDao,
    private val userStatsRepository: UserStatsRepository,
    private val appOpenRepository: AppOpenRepository,
    private val settingsViewModelFlow: StateFlow<PomodoroTimerSettings>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>) : T {
        if (modelClass.isAssignableFrom(PomodoroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PomodoroViewModel(
                dao = dao,
                userStatsRepository = userStatsRepository,
                appOpenRepository = appOpenRepository,
                pomodoroTimerSettingsFlow = settingsViewModelFlow
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}