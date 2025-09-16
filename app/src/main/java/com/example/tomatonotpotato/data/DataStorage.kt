package com.example.tomatonotpotato.data

// MyApplication.kt
import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.tomatonotpotato.data.SettingsViewModelFactory

// This property delegate creates a singleton DataStore instance
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStorage : Application() {
    // You can create a factory here to inject the DataStore into your ViewModel
    val settingsViewModelFactory by lazy {
        SettingsViewModelFactory(dataStore)
    }
}