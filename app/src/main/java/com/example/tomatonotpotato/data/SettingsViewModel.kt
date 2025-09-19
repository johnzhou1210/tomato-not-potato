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
    val autoStartFocusAfterLongBreak: Boolean,
    val pushNotificationsEnabled: Boolean = false,
    val pushNotificationsBreakEnabled: Boolean = false,
    val pushNotificationsLongBreakEnabled: Boolean = false,
    val pushNotificationsFocusEnabled: Boolean = false,
    val pushNotificationsPomodoroLoopEnabled: Boolean = false,
    val pushNotificationsDailyGoalReachedEnabled: Boolean = false
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
    private val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    private val PUSH_NOTIFICATIONS_ENABLED = booleanPreferencesKey("push_notifications_enabled")
    private val PUSH_NOTIFICATIONS_BREAK_ENABLED = booleanPreferencesKey("push_notifications_break_enabled")
    private val PUSH_NOTIFICATIONS_LONG_BREAK_ENABLED = booleanPreferencesKey("push_notifications_long_break_enabled")
    private val PUSH_NOTIFICATIONS_FOCUS_ENABLED = booleanPreferencesKey("push_notifications_focus_enabled")
    private val PUSH_NOTIFICATIONS_POMODORO_LOOP_ENABLED = booleanPreferencesKey("push_notifications_pomodoro_loop_enabled")
    private val PUSH_NOTIFICATIONS_DAILY_GOAL_REACHED_ENABLED = booleanPreferencesKey("push_notifications_daily_goal_reached_enabled")

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
                autoStartFocusAfterLongBreak = preferences[AUTO_START_FOCUS_AFTER_LONG_BREAK_KEY] ?: false,
                pushNotificationsEnabled = preferences[PUSH_NOTIFICATIONS_ENABLED] ?: false,
                pushNotificationsBreakEnabled = preferences[PUSH_NOTIFICATIONS_BREAK_ENABLED] ?: true,
                pushNotificationsLongBreakEnabled = preferences[PUSH_NOTIFICATIONS_LONG_BREAK_ENABLED] ?: true,
                pushNotificationsFocusEnabled = preferences[PUSH_NOTIFICATIONS_FOCUS_ENABLED] ?: false,
                pushNotificationsPomodoroLoopEnabled = preferences[PUSH_NOTIFICATIONS_POMODORO_LOOP_ENABLED] ?: false,
                pushNotificationsDailyGoalReachedEnabled = preferences[PUSH_NOTIFICATIONS_DAILY_GOAL_REACHED_ENABLED] ?: true

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
                autoStartFocusAfterLongBreak = false,
                pushNotificationsEnabled = false,
                pushNotificationsBreakEnabled = true,
                pushNotificationsLongBreakEnabled = true,
                pushNotificationsFocusEnabled = false,
                pushNotificationsPomodoroLoopEnabled = false,
                pushNotificationsDailyGoalReachedEnabled = true
            )
        )

    val isFirstLaunch : StateFlow<Boolean?> = dataStore.data.map { preferences ->
        preferences[FIRST_LAUNCH] ?: true
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )



    // FIRST LAUNCH
    fun setFirstLaunch(newValue : Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[FIRST_LAUNCH] = newValue
            }
        }
    }



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

    // PUSH NOTIFICATIONS
    fun setPushNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PUSH_NOTIFICATIONS_ENABLED] = enabled
            }
        }
    }

    // PUSH NOTIFICATIONS BREAK
    fun setPushNotificationsBreakEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PUSH_NOTIFICATIONS_BREAK_ENABLED] = enabled
            }
        }
    }

    // PUSH NOTIFICATIONS LONG BREAK
    fun setPushNotificationsLongBreakEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PUSH_NOTIFICATIONS_LONG_BREAK_ENABLED] = enabled
            }
        }
    }

    // PUSH NOTIFICATIONS FOCUS
    fun setPushNotificationsFocusEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PUSH_NOTIFICATIONS_FOCUS_ENABLED] = enabled
            }
        }
    }

    // PUSH NOTIFICATIONS POMODORO LOOP
    fun setPushNotificationsPomodoroLoopEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PUSH_NOTIFICATIONS_POMODORO_LOOP_ENABLED] = enabled
            }
        }
    }

    // PUSH NOTIFICATIONS DAILY GOAL REACHED
    fun setPushNotificationsDailyGoalReachedEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PUSH_NOTIFICATIONS_DAILY_GOAL_REACHED_ENABLED] = enabled
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

