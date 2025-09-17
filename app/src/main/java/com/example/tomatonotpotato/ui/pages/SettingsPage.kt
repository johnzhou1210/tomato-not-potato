import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SignalWifiStatusbarConnectedNoInternet4
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tomatonotpotato.data.PomodoroViewModel
import com.example.tomatonotpotato.data.SettingsViewModel
import com.example.tomatonotpotato.ui.theme.ErrorColor
import kotlin.math.pow

@Preview
@Composable
fun SettingsPage(settingsViewModel: SettingsViewModel = viewModel()) {
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
            onClick = {},
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
            onClick = {},
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

    Column(
        modifier = Modifier.padding(end = 0.dp, start = 0.dp, top = 4.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(36.dp))
        Text(text = "Settings", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(32.dp))
        LazyColumn {
            // 2. The `items` function now correctly iterates over your sealed class list.
            items(settingsItems) { item ->
                // 3. Use a `when` statement to render the correct composable for each type.
                when (item) {
                    is SettingItem.SwitchSetting -> {
                        SwitchItem(
                            title = item.title,
                            description = item.description,
                            isChecked = item.isChecked,
                            onCheckedChange = item.onCheckedChange,
                            icon = item.icon
                        )
                    }
                    is SettingItem.NumberSetting -> {
                        NumberItem(
                            title = item.title,
                            value = item.value,
                            onValueChange = item.onValueChange,
                            unitSingular = item.unitSingular,
                            unitPlural = item.unitPlural,
                            description = item.description,
                            minVal = item.minVal,
                            maxVal = item.maxVal,
                            icon = item.icon
                        )
                    }
                    is SettingItem.NavigationSetting -> {
                        NavigationItem(
                            title = item.title,
                            description = item.description,
                            onClick = item.onClick,
                            icon = item.icon
                        )
                    }
                    is SettingItem.ResetSetting -> {
                        ResetItem(
                            title = item.title,
                            description = item.description,
                            onReset = item.onReset,
                            icon = item.icon
                        )
                    }
                    is SettingItem.InfoSetting -> {
                        InfoItem(
                            title = item.title,
                            description = item.description,
                            icon = item.icon
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun SwitchItem(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: @Composable () -> Unit = {}
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(description) },
        leadingContent = icon,
        trailingContent = {
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
        },
        modifier = Modifier.clickable { onCheckedChange(!isChecked) }
    )
}


@Composable
fun NumberItem(
    title: String,
    value: Int,
    unitSingular: String = "",
    unitPlural: String = "",
    onValueChange: (Int) -> Unit,
    icon: @Composable () -> Unit = {},
    description: String,
    minVal : Int = 0,
    maxVal : Int = 999
) {
    var showDialog by remember { mutableStateOf(false) }
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = {  Text("$value ${if (value == 1) unitSingular else unitPlural}")  },
        leadingContent = icon,
        modifier = Modifier.clickable { showDialog = true }
    )

    if (showDialog) {
        NumberInputDialog(
            title = description,
            currentValue = value,
            units = unitPlural,
            onDismiss = { showDialog = false},
            onConfirm = { newValue ->
                onValueChange(newValue)
                showDialog = false
            },
            minVal = minVal,
            maxVal = maxVal
        )
    }

}

@Composable
fun NavigationItem(
    title: String,
    description: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit = {}
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(description) },
        leadingContent = icon,
        trailingContent = { Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Go to $title",
        ) },
        modifier = Modifier.clickable { onClick() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetItem(
    title: String,
    description: String,
    onReset: () -> Unit,
    icon: @Composable () -> Unit = {},
) {
    var showDialog by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(title, color = ErrorColor) },
        supportingContent = { Text(description) },
        leadingContent = icon,
        modifier = Modifier.clickable { showDialog = true }
    )

    // Alert dialog goes here
    if (showDialog) {
        ResetDialog(
            onDismiss = { showDialog = false },
            onConfirm = {
                onReset()
                showDialog = false
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoItem(
    title: String,
    description: String,
    icon: @Composable () -> Unit = {},
) {
    var showDialog by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(title, color = MaterialTheme.colorScheme.onSurface) },
        supportingContent = { Text(description) },
        leadingContent = icon,
        modifier = Modifier.clickable {}
    )

}




sealed class SettingItem(
    open val title: String,
    open val description: String,
    open val icon: @Composable () -> Unit = {}
) {
    data class SwitchSetting(
        override val title: String,
        override val description: String,
        override val icon: @Composable () -> Unit = {},
        val isChecked: Boolean,
        val onCheckedChange: (Boolean) -> Unit
    ) : SettingItem(title, description)

    data class NumberSetting(
        override val title: String,
        override val description: String,
        override val icon: @Composable () -> Unit = {},
        val unitSingular: String,
        val unitPlural: String,
        val value: Int,
        val minVal : Int = 0,
        val maxVal : Int = 999,
        val onValueChange: (Int) -> Unit
    ) : SettingItem(title, description)

    data class NavigationSetting(
        override val title: String,
        override val description: String,
        override val icon: @Composable () -> Unit = {},
        val onClick: () -> Unit
    ) : SettingItem(title, description)

    data class ResetSetting(
        override val title: String,
        override val description: String,
        override val icon: @Composable () -> Unit = {},
        val onReset: () -> Unit,
    ) : SettingItem(title, description)

    data class InfoSetting(
        override val title: String,
        override val description: String,
        override val icon: @Composable () -> Unit = {},
    ) : SettingItem(title, description)
}

@Composable
fun NumberInputDialog(
    title: String,
    currentValue: Int,
    units: String,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
    minVal : Int,
    maxVal : Int,
) {
    var inputValue by remember{mutableStateOf(TextFieldValue(currentValue.toString()))}
    var isInputValid = remember(inputValue) {
        val number = inputValue.text.toIntOrNull()
        number != null && number in minVal..maxVal
    }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val indicatorColor by animateColorAsState(
        targetValue = if (isInputValid) MaterialTheme.colorScheme.secondary else ErrorColor,
        label = "indicatorColorAnimation"
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            OutlinedTextField(
                value = inputValue,
                onValueChange = { newValue ->
                    val number = newValue.text.toIntOrNull()
                    if (newValue.text.isEmpty() || (number != null && number in minVal..maxVal)) {
                        inputValue = newValue
                    }
                },
                label = {Text("$title ($units)", style = MaterialTheme.typography.labelSmall)},
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.colors(
                    focusedLabelColor = indicatorColor,
                    focusedIndicatorColor = indicatorColor
                ),
                modifier = Modifier.focusRequester(focusRequester)
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val newValue = inputValue.text.toIntOrNull()
                    if (newValue != null) {
                        onConfirm(newValue)
                    }
                },
                enabled = isInputValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                )
            ) {
                Text("OK", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(horizontal = 8.dp))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(
                containerColor = ErrorColor,
            )) {
                Text("Cancel", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(horizontal = 8.dp))
            }
        }
    )
    // Request focus when the dialog is first composed
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
        inputValue = inputValue.copy(selection = TextRange(inputValue.text.length))
    }
}


@Preview
@Composable
fun ResetDialog(
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    AlertDialog(
        title = {
            Text("Are you sure?", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
        },
        onDismissRequest = onDismiss,
        text = {
            Text("Your Pomodoro history will not be deleted.", style = MaterialTheme.typography.bodyMedium)
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorColor,
                )
            ) {
                Text("Yes", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(horizontal = 8.dp))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
            )) {
                Text("No", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(horizontal = 8.dp))
            }
        }
    )

}