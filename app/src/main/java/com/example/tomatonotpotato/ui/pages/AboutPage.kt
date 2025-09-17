import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
fun AboutPage() {
    Column(
        modifier = Modifier.padding(end = 0.dp, start = 0.dp, top = 4.dp, bottom = 4.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(36.dp))
        Text(text = "Settings", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(32.dp))

        val settingsItems: List<SettingItem> = listOf(
            SettingItem.InfoSetting(
                title = "Reset settings",
                description = "Resets all settings to default",
                icon = {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = "Reset settings",
                    )
                }
            )


        )




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



