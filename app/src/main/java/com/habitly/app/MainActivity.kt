package com.habitly.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.habitly.app.data.ThemeMode
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.habitly.app.navigation.HabitlyNavGraph
import com.habitly.app.ui.theme.HabitlyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val preferencesRepository = (application as HabitlyApplication).preferencesRepository
        setContent {
            val themeMode by preferencesRepository.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
            val hasCompletedOnboarding by preferencesRepository.hasCompletedOnboarding.collectAsState(initial = false)
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (themeMode) {
                ThemeMode.SYSTEM -> systemDark
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            HabitlyAppTheme(darkTheme = darkTheme) {
                HabitlyNavGraph(showOnboarding = !hasCompletedOnboarding)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitlyAppTheme {
                HabitlyNavGraph()
            }
        }
    }
}
