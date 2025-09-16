package com.example.tomatonotpotato

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tomatonotpotato.data.AppOpenRepository
import com.example.tomatonotpotato.data.DatabaseProvider
import com.example.tomatonotpotato.data.PomodoroViewModelFactory
import com.example.tomatonotpotato.ui.TimerApp
import com.example.tomatonotpotato.ui.theme.TomatoNotPotatoTheme
import com.example.tomatonotpotato.data.PomodoroViewModel
import com.example.tomatonotpotato.data.SettingsViewModel
import com.example.tomatonotpotato.data.SettingsViewModelFactory
import com.example.tomatonotpotato.data.UserStatsRepository
import com.example.tomatonotpotato.data.dataStore
import kotlinx.coroutines.launch


// --- MainActivity ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appOpenDayDao = DatabaseProvider.getDatabase(applicationContext).appOpenDayDao()
        val appOpenRepository = AppOpenRepository(appOpenDayDao)
        val userStatsDao = DatabaseProvider.getDatabase(applicationContext).userStatsDao()
        val userStatsRepository = UserStatsRepository(userStatsDao)
        val pomodoroDao = DatabaseProvider.getDatabase(applicationContext).pomodoroDao()
        val pomodoroFactory = PomodoroViewModelFactory(pomodoroDao, userStatsRepository, appOpenRepository)
        val settingsViewModelFactory = SettingsViewModelFactory(applicationContext.dataStore)

        lifecycleScope.launch {
            appOpenRepository.logToday()
        }

        setContent {

            val settingsViewModel: SettingsViewModel = viewModel(factory = settingsViewModelFactory)
            val pomodoroViewModel: PomodoroViewModel = viewModel(factory = pomodoroFactory)
            TomatoNotPotatoTheme(pomodoroViewModel = pomodoroViewModel, settingsViewModel = settingsViewModel) {
                TimerApp(viewModel = pomodoroViewModel, settingsViewModel = settingsViewModel)
            }
        }
    }
}

