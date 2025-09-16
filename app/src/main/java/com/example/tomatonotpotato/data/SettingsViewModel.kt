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
import androidx.datastore.preferences.preferencesDataStore
import com.example.tomatonotpotato.data.SettingsViewModelFactory

class SettingsViewModel(private val dataStore: DataStore<Preferences>) : ViewModel() {
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled")

    val isDarkMode = dataStore.data
        .map { preferences ->
            preferences[DARK_MODE_KEY] ?: false
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            false
        )

    fun toggleDarkMode() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[DARK_MODE_KEY] = !(preferences[DARK_MODE_KEY] ?: false)
            }
        }
    }
}