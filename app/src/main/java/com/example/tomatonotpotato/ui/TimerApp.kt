package com.example.tomatonotpotato.ui

import SettingsPage
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tomatonotpotato.ui.pages.TimerPage
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.tomatonotpotato.data.AppOpenRepository
import com.example.tomatonotpotato.ui.pages.HistoryPage
import com.example.tomatonotpotato.data.PomodoroViewModel
import com.example.tomatonotpotato.data.SettingsViewModel


sealed class Screen(val route: String) {
    object Timer : Screen("Timer")
    object History : Screen("History")
    object Settings : Screen("Settings")
}


@Composable
fun TimerApp(pomodoroViewModel: PomodoroViewModel = viewModel(), settingsViewModel: SettingsViewModel = viewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Timer.route

    val screens = listOf(
        Screen.Timer,
        Screen.History,
        Screen.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                screens.forEach { screen ->
                    NavigationBarItem(
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.surface,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),

                        icon = {
                            when (screen) {
                                Screen.Timer -> Icon(Icons.Filled.Timer, contentDescription = null)
                                Screen.History -> Icon(
                                    Icons.Filled.History,
                                    contentDescription = null
                                )

                                Screen.Settings -> Icon(
                                    Icons.Filled.Settings,
                                    contentDescription = null
                                )
                            }
                        },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).padding(horizontal = 0.dp)) {
            NavHost(navController = navController, startDestination = Screen.Timer.route) {
                composable(Screen.Timer.route) { TimerPage(pomodoroViewModel = pomodoroViewModel) }
                composable(Screen.History.route) { HistoryPage(pomodoroViewModel = pomodoroViewModel) }
                composable(Screen.Settings.route) { SettingsPage(settingsViewModel= settingsViewModel) }
            }
        }
    }


}