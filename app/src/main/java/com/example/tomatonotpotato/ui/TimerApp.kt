package com.example.tomatonotpotato.ui

import AboutPage
import NotificationsPage
import SettingsPage
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tomatonotpotato.ui.pages.TimerPage
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.tomatonotpotato.ui.pages.HistoryPage
import com.example.tomatonotpotato.data.PomodoroViewModel
import com.example.tomatonotpotato.data.SettingsViewModel
import com.example.tomatonotpotato.ui.pages.RequestNotificationsPage
import com.example.tomatonotpotato.ui.pages.WelcomePage


sealed class Screen(val route: String) {
    object Welcome : Screen("Welcome")
    object RequestNotifications : Screen("RequestNotifications")
    object Timer : Screen("Timer")
    object History : Screen("History")
    object Settings : Screen("Settings")
    object Notifications : Screen("Notifications")
    object About : Screen("About")
}


@Composable
fun TimerApp(
    pomodoroViewModel: PomodoroViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val isFirstLaunch by settingsViewModel.isFirstLaunch.collectAsState()
    // This is to prevent the app from briefly loading in welcome screen if the user has opened the app before after installation
    if (isFirstLaunch != null) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Timer.route
        val startDestination = if (isFirstLaunch == true) Screen.Welcome.route else Screen.Timer.route
        val screens = listOf(
            Screen.Timer,
            Screen.History,
            Screen.Settings
        )
        Scaffold(
            bottomBar = {
                if (currentRoute !in listOf(Screen.Welcome.route, Screen.Settings.route, Screen.About.route, Screen.RequestNotifications.route, Screen.Notifications.route)) {
                    NavBar(screens, currentRoute, navController)
                }
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(start = 0.dp, end = 0.dp, top = 14.dp, bottom = 0.dp)
            ) {
                NavHost(navController = navController, startDestination = startDestination) {
                    composable(Screen.Timer.route) { TimerPage(pomodoroViewModel = pomodoroViewModel) }
                    composable(Screen.History.route) { HistoryPage(pomodoroViewModel = pomodoroViewModel) }
                    composable(Screen.Settings.route) {
                        SettingsPage(
                            settingsViewModel = settingsViewModel,
                            onNavigate = { navController.navigate(it) },
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.Notifications.route) {
                        NotificationsPage(
                            onBack = { navController.popBackStack() },
                            settingsViewModel = settingsViewModel
                        )
                    }
                    composable(Screen.About.route) {
                        AboutPage(
                            onBack = {
                                navController.popBackStack()
                            })
                    }
                    composable(Screen.Welcome.route) {
                        WelcomePage(
                            onGetStarted = {
                                navController.navigate(Screen.RequestNotifications.route) {
                                    popUpTo(Screen.Welcome.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Screen.RequestNotifications.route) {
                        RequestNotificationsPage(
                            onRefuse = {
                                settingsViewModel.setFirstLaunch(false)
                                navController.navigate(Screen.Timer.route) {
                                    popUpTo(Screen.Welcome.route) { inclusive = true }
                                }
                            },
                            onAccept = {
                                settingsViewModel.setFirstLaunch(false)
                                navController.navigate(Screen.Timer.route) {
                                    popUpTo(Screen.Welcome.route) { inclusive = true }
                                }
                            },
                            settingsViewModel = settingsViewModel
                        )
                    }
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun NavBar(screens: List<Screen>, currentRoute: String, navController: NavController) {
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
                        Screen.Timer -> Icon(
                            Icons.Filled.Timer,
                            contentDescription = null
                        )

                        Screen.History -> Icon(
                            Icons.Filled.History,
                            contentDescription = null
                        )

                        Screen.Settings -> Icon(
                            Icons.Filled.Settings,
                            contentDescription = null
                        )
                        else -> {}
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
}