package com.example.tomatonotpotato.data

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.StateFlow

data class PomodoroTimerSettings(
    val focusDuration: Int,
    val breakDuration: Int,
    val longBreakDuration: Int,
    val pomodorosBeforeLongBreak: Int,
    val isStatusBarVisible: Boolean,
    val isDarkMode: Boolean,
    val dailyPomodoriGoal: Int,
    val autoStartBreak: Boolean,
    val autoStartFocusAfterBreak: Boolean,
    val autoStartFocusAfterLongBreak: Boolean
)


class SettingsViewModel(private val dataStore: DataStore<Preferences>) : ViewModel() {
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled")
    private val STATUS_BAR_KEY = booleanPreferencesKey("status_bar_enabled")
    private val FOCUS_DURATION = intPreferencesKey("focus_duration")
    private val BREAK_DURATION = intPreferencesKey("break_duration")
    private val LONG_BREAK_DURATION = intPreferencesKey("long_break_duration")
    private val POMODORO_BEFORE_LONG_BREAK = intPreferencesKey("pomodoros_before_long_break")
    private val DAILY_POMODORI_GOAL = intPreferencesKey("daily_pomodori_goal")
    private val AUTO_START_BREAK_KEY = booleanPreferencesKey("auto_start_break")
    private val AUTO_START_FOCUS_AFTER_BREAK_KEY =
        booleanPreferencesKey("auto_start_focus_after_break")
    private val AUTO_START_FOCUS_AFTER_LONG_BREAK_KEY =
        booleanPreferencesKey("auto_start_focus_after_long_break")


    // Bundled version of Settings
    val pomodoroTimerSettings: StateFlow<PomodoroTimerSettings> =
        dataStore.data.map { preferences ->
            PomodoroTimerSettings(
                focusDuration = preferences[FOCUS_DURATION] ?: 25,
                breakDuration = preferences[BREAK_DURATION] ?: 5,
                longBreakDuration = preferences[LONG_BREAK_DURATION] ?: 30,
                pomodorosBeforeLongBreak = preferences[POMODORO_BEFORE_LONG_BREAK] ?: 4,
                isStatusBarVisible = preferences[STATUS_BAR_KEY] ?: false,
                isDarkMode = preferences[DARK_MODE_KEY] ?: false,
                dailyPomodoriGoal = preferences[DAILY_POMODORI_GOAL] ?: 1,
                autoStartBreak = preferences[AUTO_START_BREAK_KEY] ?: false,
                autoStartFocusAfterBreak = preferences[AUTO_START_FOCUS_AFTER_BREAK_KEY] ?: false,
                autoStartFocusAfterLongBreak = preferences[AUTO_START_FOCUS_AFTER_LONG_BREAK_KEY] ?: false
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            PomodoroTimerSettings(
                focusDuration = 25,
                breakDuration = 5,
                longBreakDuration = 30,
                pomodorosBeforeLongBreak = 4,
                isStatusBarVisible = false,
                isDarkMode = false,
                dailyPomodoriGoal = 1,
                autoStartBreak = false,
                autoStartFocusAfterBreak = false,
                autoStartFocusAfterLongBreak = false
            )
        )


    //　DARK MODE
    fun toggleDarkMode() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[DARK_MODE_KEY] = !(preferences[DARK_MODE_KEY] ?: false)
            }
        }
    }

    // STATUS BAR VISIBILITY
    fun toggleStatusBar() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                val currentValue = preferences[STATUS_BAR_KEY] ?: false
                preferences[STATUS_BAR_KEY] = !currentValue
                println("Status bar visibility toggled to: ${!currentValue}")
            }
        }
    }

    // FOCUS DURATION
    fun setFocusDuration(duration: Int) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[FOCUS_DURATION] = duration
            }
        }
    }


    // BREAK DURATION
    fun setBreakDuration(duration: Int) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[BREAK_DURATION] = duration
            }
        }
    }


    // LONG BREAK DURATION
    fun setLongBreakDuration(duration: Int) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[LONG_BREAK_DURATION] = duration
            }
        }
    }


    // POMODORO BEFORE LONG BREAK
    fun setPomodorosBeforeLongBreak(pomodoros: Int) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[POMODORO_BEFORE_LONG_BREAK] = pomodoros
            }
        }
    }


    // DAILY POMODORI GOAL
    fun setDailyPomodoriGoal(pomodoros: Int) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[DAILY_POMODORI_GOAL] = pomodoros
            }
        }
    }

    // AUTO START BREAK
    fun toggleAutoStartBreak() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                val currentValue = preferences[AUTO_START_BREAK_KEY] ?: false
                preferences[AUTO_START_BREAK_KEY] = !currentValue
                println("Auto start break toggled to: ${!currentValue}")
            }
        }
    }

    // AUTO START FOCUS AFTER BREAK
    fun toggleAutoStartFocusAfterBreak() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                val currentValue = preferences[AUTO_START_FOCUS_AFTER_BREAK_KEY] ?: false
                preferences[AUTO_START_FOCUS_AFTER_BREAK_KEY] = !currentValue
                println("Auto start focus after break toggled to: ${!currentValue}")
            }
        }
    }

    // AUTO START FOCUS AFTER LONG BREAK
    fun toggleAutoStartFocusAfterLongBreak() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                val currentValue = preferences[AUTO_START_FOCUS_AFTER_LONG_BREAK_KEY] ?: false
                preferences[AUTO_START_FOCUS_AFTER_LONG_BREAK_KEY] = !currentValue
                println("Auto start focus after long break toggled to: ${!currentValue}")
            }
        }
    }

    fun resetSettings() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences.clear()
            }
        }
    }

}

