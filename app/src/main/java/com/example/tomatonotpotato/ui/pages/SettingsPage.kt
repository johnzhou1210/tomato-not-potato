import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SignalWifiStatusbarConnectedNoInternet4
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tomatonotpotato.data.SettingsViewModel
import com.example.tomatonotpotato.ui.Screen
import com.example.tomatonotpotato.ui.components.SettingItem
import com.example.tomatonotpotato.ui.components.SettingsColumn

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SettingsPage(
    settingsViewModel: SettingsViewModel = viewModel(),
    onNavigate: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val pomodoroTimerSettings = settingsViewModel.pomodoroTimerSettings.collectAsState()
    // Collect the isDarkMode state from the ViewModel
    val isDarkMode = pomodoroTimerSettings.value.isDarkMode
    val isStatusBarVisible = pomodoroTimerSettings.value.isStatusBarVisible
    val focusDuration = pomodoroTimerSettings.value.focusDuration
    val breakDuration = pomodoroTimerSettings.value.breakDuration
    val longBreakDuration = pomodoroTimerSettings.value.longBreakDuration
    val pomodorosBeforeLongBreak = pomodoroTimerSettings.value.pomodorosBeforeLongBreak
    val dailyPomodoriGoal = pomodoroTimerSettings.value.dailyPomodoriGoal
    val autoStartBreak = pomodoroTimerSettings.value.autoStartBreak
    val autoStartFocusAfterBreak = pomodoroTimerSettings.value.autoStartFocusAfterBreak
    val autoStartFocusAfterLongBreak = pomodoroTimerSettings.value.autoStartFocusAfterLongBreak


    val settingsItems: List<SettingItem> = listOf(
        SettingItem.SwitchSetting(
            title = "Dark mode",
            description = "Dark mode is ${if (isDarkMode) "enabled" else "disabled"}",
            isChecked = isDarkMode,
            onCheckedChange = { settingsViewModel.toggleDarkMode()  },
            icon = {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = if (isDarkMode) "Dark mode" else "Light mode",
                )
            }
        ),
        SettingItem.NavigationSetting(
            title = "Notifications",
            description = "Control notifications",
            onClick = { onNavigate(Screen.Notifications.route) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                )
            }
        ),
        SettingItem.SwitchSetting(
            title = "Show status bar",
            description = "Status bar is currently ${if (isStatusBarVisible) "visible" else "hidden"}",
            isChecked = isStatusBarVisible,
            onCheckedChange = { settingsViewModel.toggleStatusBar()  },
            icon = {
                Icon(
                    imageVector = Icons.Default.SignalWifiStatusbarConnectedNoInternet4,
                    contentDescription = "Show status bar",
                )
            }
        ),
        SettingItem.NumberSetting(
            title = "Focus duration",
            description = "Set focus duration",
            value = focusDuration,
            onValueChange = { settingsViewModel.setFocusDuration(it) },
            unitSingular = "minute",
            unitPlural = "minutes",
            minVal = 1,
            maxVal = 999,
            icon = {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "Focus duration",
                )
            }
        ),
        SettingItem.NumberSetting(
            title = "Break duration",
            description = "Set short break duration",
            value = breakDuration,
            onValueChange = { settingsViewModel.setBreakDuration(it) },
            unitSingular = "minute",
            unitPlural = "minutes",
            minVal = 1,
            maxVal = 999,
            icon = {
                Icon(
                    imageVector = Icons.Default.SelfImprovement,
                    contentDescription = "Break duration",
                )
            }
        ),
        SettingItem.NumberSetting(
            title = "Long break duration",
            description = "Set long break duration",
            value = longBreakDuration,
            onValueChange = { settingsViewModel.setLongBreakDuration(it) },
            unitSingular = "minute",
            unitPlural = "minutes",
            minVal = 1,
            maxVal = 999,
            icon = {
                Icon(
                    imageVector = Icons.Default.BeachAccess,
                    contentDescription = "Long break duration",
                )
            }
        ),
        SettingItem.NumberSetting(
            title = "Daily goal",
            description = "Set number for daily goal",
            value = dailyPomodoriGoal,
            onValueChange = { settingsViewModel.setDailyPomodoriGoal(it) },
            unitSingular = "Pomodoro",
            unitPlural = "Pomodori",
            minVal = 1,
            maxVal = 999,
            icon = {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Daily goal",
                )
            }
        ),
        SettingItem.NumberSetting(
            title = "Pomodori before long break",
            description = "Set number before long break (max 12)",
            value = pomodorosBeforeLongBreak,
            onValueChange = { settingsViewModel.setPomodorosBeforeLongBreak(it) },
            unitSingular = "Pomodoro",
            unitPlural = "Pomodori",
            minVal = 1,
            maxVal = 12,
            icon = {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = "Pomodori before long break",
                )
            }
        ),
        SettingItem.SwitchSetting(
            title = "Auto-start break",
            description = "Automatically start break after focus",
            isChecked = autoStartBreak,
            onCheckedChange = { settingsViewModel.toggleAutoStartBreak() },
            icon = {
                Icon(
                    imageVector = Icons.Default.PlayCircleFilled,
                    contentDescription = "Auto-start break",
                )
            }

        ),
        SettingItem.SwitchSetting(
            title = "Auto-start focus",
            description = "Automatically start focus after break, excluding long break",
            isChecked = autoStartFocusAfterBreak,
            onCheckedChange = { settingsViewModel.toggleAutoStartFocusAfterBreak() },
            icon = {
                Icon(
                    imageVector = Icons.Default.PlayCircleOutline,
                    contentDescription = "Auto-start focus",
                )
            }
        ),
        SettingItem.SwitchSetting(
            title = "Auto restart timer after long break",
            description = "Automatically restart timer loop at the end",
            isChecked = autoStartFocusAfterLongBreak,
            onCheckedChange = { settingsViewModel.toggleAutoStartFocusAfterLongBreak() },
            icon = {
                Icon(
                    imageVector = Icons.Default.Loop,
                    contentDescription = "Auto restart timer after long break",
                )
            }
        ),
        SettingItem.NavigationSetting(
            title = "About",
            description = "Learn more about the app",
            onClick = { onNavigate(Screen.About.route) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "About",
                )
            }
        ),
        SettingItem.ResetSetting(
            title = "Reset settings",
            description = "Resets all settings to default",
            onReset = { settingsViewModel.resetSettings() },
            icon = {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = "Reset settings",
                )
            }
        )


    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                ) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            SettingsColumn(settingsItems = settingsItems)
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
        }

    }
}



