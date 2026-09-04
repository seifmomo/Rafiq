package com.example.rafiq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.rafiq.data.local.TokenManager
import com.example.rafiq.data.local.UserPreferences
import com.example.rafiq.presentation.navigation.RafiqNavigation
import com.example.rafiq.presentation.navigation.Screen
import com.example.rafiq.ui.theme.RAFIQTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userPreferences = UserPreferences(applicationContext)
        val tokenManager = TokenManager(applicationContext)

        setContent {
            val darkThemePref by userPreferences.darkTheme.collectAsState(initial = "system")
            val fontSizePref by userPreferences.fontSize.collectAsState(initial = "normal")
            val fontFamilyPref by userPreferences.fontFamily.collectAsState(initial = "default")
            val introCompleted by userPreferences.introCompleted.collectAsState(initial = false)
            val token by tokenManager.getToken.collectAsState(initial = null)

            LaunchedEffect(Unit) {
                userPreferences.setLoggedIn(false)
            }

            val useDarkTheme = when (darkThemePref) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            val startDestination = when {
                token == null -> null
                token?.isEmpty() == true -> Screen.Login.route
                !introCompleted -> Screen.Intro.route
                else -> Screen.Home.route
            }

            RAFIQTheme(darkTheme = useDarkTheme, fontSize = fontSizePref, fontFamily = fontFamilyPref) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (startDestination == null) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    } else {
                        RafiqNavigation(startDestination = startDestination)
                    }
                }
            }
        }
    }
}
