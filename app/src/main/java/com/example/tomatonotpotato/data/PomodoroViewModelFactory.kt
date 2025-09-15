package com.example.tomatonotpotato.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PomodoroViewModelFactory(
    private val dao: PomodoroDao,
    private val userStatsRepository: UserStatsRepository,
    private val appOpenRepository: AppOpenRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>) : T {
        if (modelClass.isAssignableFrom(PomodoroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PomodoroViewModel(
                dao = dao,
                userStatsRepository = userStatsRepository,
                appOpenRepository = appOpenRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}