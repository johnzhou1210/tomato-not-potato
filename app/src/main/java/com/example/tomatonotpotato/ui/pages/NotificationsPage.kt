import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tomatonotpotato.R
import com.example.tomatonotpotato.data.SettingsViewModel
import com.example.tomatonotpotato.services.showToastWithCooldown
import com.example.tomatonotpotato.ui.components.SettingItem
import com.example.tomatonotpotato.ui.components.SettingsColumn

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun NotificationsPage(
    onBack: () -> Unit = {},
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val currContext = LocalContext.current
    val pomodoroTimerSettings = settingsViewModel.pomodoroTimerSettings.collectAsState()
    val pushNotificationsEnabled = pomodoroTimerSettings.value.pushNotificationsEnabled
    val pushNotificationsBreakEnabled = pomodoroTimerSettings.value.pushNotificationsBreakEnabled
    val pushNotificationsLongBreakEnabled = pomodoroTimerSettings.value.pushNotificationsLongBreakEnabled
    val pushNotificationsFocusEnabled = pomodoroTimerSettings.value.pushNotificationsFocusEnabled
    val pushNotificationsPomodoroLoopEnabled = pomodoroTimerSettings.value.pushNotificationsPomodoroLoopEnabled
    val pushNotificationsDailyGoalReachedEnabled = pomodoroTimerSettings.value.pushNotificationsDailyGoalReachedEnabled

    var hasPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    currContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else {
            mutableStateOf(true)
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasPermission = isGranted
            settingsViewModel.setPushNotificationsEnabled(isGranted)
            if (!isGranted) {
                showToastWithCooldown(
                    currContext,
                    "Please enable notifications in system app settings to use this feature",
                    longLengthToast = true,
                    duration = 4000L
                )
            }
        }
    )

    val settingsItems: List<SettingItem> = listOf(
        SettingItem.SwitchSetting(
            title = "Push Notifications",
            description = "Notifications are currently ${if (pushNotificationsEnabled) "enabled and will be sent to your device if the app is running in the background" else "disabled"}",
            isChecked = pushNotificationsEnabled,
            onCheckedChange = { newVal ->
                if (newVal) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
                settingsViewModel.setPushNotificationsEnabled(!pushNotificationsEnabled)
                              },
            icon = {
                Icon(
                    imageVector = if (pushNotificationsEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                    contentDescription = "Push notifications",
                )
            }
        ),
        SettingItem.SwitchSetting(
            title = "Push short break notifications",
            description = "Short break notifications are currently ${if (pushNotificationsBreakEnabled) "enabled" else "disabled"}",
            isChecked = pushNotificationsBreakEnabled,
            onCheckedChange = { settingsViewModel.setPushNotificationsBreakEnabled(!pushNotificationsBreakEnabled) },
            icon = {
                Icon(
                    imageVector = Icons.Default.SelfImprovement,
                    contentDescription = "Push short break notifications",
                )
            },
            enabled = pushNotificationsEnabled
        ),
        SettingItem.SwitchSetting(
            title = "Push long break notifications",
            description = "Long break notifications are currently ${if (pushNotificationsLongBreakEnabled) "enabled" else "disabled"}",
            isChecked = pushNotificationsLongBreakEnabled,
            onCheckedChange = { settingsViewModel.setPushNotificationsLongBreakEnabled(!pushNotificationsLongBreakEnabled) },
            icon = {
                Icon(
                    imageVector = Icons.Default.BeachAccess,
                    contentDescription = "Push long break notifications",
                )
            },
            enabled = pushNotificationsEnabled
        ),
        SettingItem.SwitchSetting(
            title = "Push focus notifications",
            description = "Focus notifications are currently ${if (pushNotificationsFocusEnabled) "enabled" else "disabled"}",
            isChecked = pushNotificationsFocusEnabled,
            onCheckedChange = { settingsViewModel.setPushNotificationsFocusEnabled(!pushNotificationsFocusEnabled) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "Push focus notifications",
                )
            },
            enabled = pushNotificationsEnabled
        ),
        SettingItem.SwitchSetting(
            title = "Push Pomodoro loop completed notifications",
            description = "Loop completed notifications are currently ${if (pushNotificationsPomodoroLoopEnabled) "enabled" else "disabled"}",
            isChecked = pushNotificationsPomodoroLoopEnabled,
            onCheckedChange = { settingsViewModel.setPushNotificationsPomodoroLoopEnabled(!pushNotificationsPomodoroLoopEnabled) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Loop,
                    contentDescription = "Push loop completed notifications",
                )
            },
            enabled = pushNotificationsEnabled
        ),
        SettingItem.SwitchSetting(
            title = "Push daily Pomodoro goal reached notifications",
            description = "Daily goal completion notifications are currently ${if (pushNotificationsDailyGoalReachedEnabled) "enabled" else "disabled"}",
            isChecked = pushNotificationsDailyGoalReachedEnabled,
            onCheckedChange = { settingsViewModel.setPushNotificationsDailyGoalReachedEnabled(!pushNotificationsDailyGoalReachedEnabled) },
            icon = {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Push daily goal reached notifications",
                )
            },
            enabled = pushNotificationsEnabled
        ),

    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notifications",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SettingsColumn(settingsItems)
        }

    }

}



