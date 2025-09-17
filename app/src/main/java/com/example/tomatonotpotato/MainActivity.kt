package com.example.tomatonotpotato

import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowInsets
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tomatonotpotato.data.AppOpenRepository
import com.example.tomatonotpotato.data.DatabaseProvider
import com.example.tomatonotpotato.data.PomodoroTimerSettings
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
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val appOpenDayDao = DatabaseProvider.getDatabase(applicationContext).appOpenDayDao()
        val appOpenRepository = AppOpenRepository(appOpenDayDao)
        val userStatsDao = DatabaseProvider.getDatabase(applicationContext).userStatsDao()
        val userStatsRepository = UserStatsRepository(userStatsDao)
        val pomodoroDao = DatabaseProvider.getDatabase(applicationContext).pomodoroDao()


        val settingsViewModelFactory = SettingsViewModelFactory(applicationContext.dataStore)

        fun setSystemUIVisibility(window: Window, isVisible: Boolean) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                when (isVisible) {
                    true -> window.insetsController?.show(WindowInsets.Type.statusBars())
                    else ->  window.insetsController?.hide(WindowInsets.Type.statusBars())
                }

            }
        }

        lifecycleScope.launch {
            appOpenRepository.logToday()
        }

        setContent {

            val settingsViewModel: SettingsViewModel = viewModel(factory = settingsViewModelFactory)
            val pomodoroTimerSettingsFlow = settingsViewModel.pomodoroTimerSettings
            val pomodoroFactory = PomodoroViewModelFactory(pomodoroDao, userStatsRepository, appOpenRepository, pomodoroTimerSettingsFlow)


            val pomodoroViewModel: PomodoroViewModel = viewModel(factory = pomodoroFactory)
            val isStatusBarVisible = settingsViewModel.pomodoroTimerSettings.collectAsState().value.isStatusBarVisible
            TomatoNotPotatoTheme(pomodoroViewModel = pomodoroViewModel, settingsViewModel = settingsViewModel) {
                LaunchedEffect(key1 = isStatusBarVisible) {
                    setSystemUIVisibility(window, isStatusBarVisible)
                }

                TimerApp(pomodoroViewModel = pomodoroViewModel, settingsViewModel = settingsViewModel)
            }
        }
    }
}

