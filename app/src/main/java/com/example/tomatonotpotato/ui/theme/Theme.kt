package com.example.tomatonotpotato.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tomatonotpotato.data.BreakType
import com.example.tomatonotpotato.data.PomodoroViewModel
import com.example.tomatonotpotato.data.SettingsViewModel


@Composable
fun TomatoNotPotatoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    pomodoroViewModel: PomodoroViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(),
    content: @Composable () -> Unit
) {
    val state by pomodoroViewModel.state.collectAsState()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val colors  = when {
        isDarkMode -> { // Dark mode
           if (state.breakType != BreakType.NONE) BreakColorsDark else FocusColorsDark
        }
        else -> { // L:ght mode
           if (state.breakType != BreakType.NONE) BreakColorsLight else FocusColorsLight
        }
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}