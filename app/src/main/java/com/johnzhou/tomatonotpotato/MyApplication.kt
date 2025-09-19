package com.johnzhou.tomatonotpotato

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ProcessLifecycleOwner
import com.johnzhou.tomatonotpotato.services.AppLifecycleObserver

// This property delegate creates a singleton DataStore instance
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MyApplication : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver())
        createNotificationChannel()
    }

    // You can create a factory here to inject the DataStore into your ViewModel
    val settingsViewModelFactory by lazy {
        _root_ide_package_.com.johnzhou.tomatonotpotato.data.SettingsViewModelFactory(dataStore)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "pomodoro_timer_channel"
            val channelName = "Pomodoro Timer"
            val importance = android.app.NotificationManager.IMPORTANCE_HIGH
            val description = "Notification for when Pomodoro timers are finished"

            val channel = android.app.NotificationChannel(channelId, channelName, importance).apply {
                this.description = description
            }

            val notificationManager: NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}