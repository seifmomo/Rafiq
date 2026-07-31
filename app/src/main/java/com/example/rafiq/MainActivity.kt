package com.example.rafiq

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rafiq.data.local.TokenManager
import com.example.rafiq.data.local.UserPreferences
import com.example.rafiq.presentation.hardware.HardwareViewModel
import com.example.rafiq.presentation.navigation.RafiqNavigation
import com.example.rafiq.presentation.navigation.Screen
import com.example.rafiq.ui.theme.RAFIQTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
            val scope = rememberCoroutineScope()

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

            val hardwareViewModel: HardwareViewModel = hiltViewModel()
            val isConnected by hardwareViewModel.isConnected.collectAsState()
            val distanceCm by hardwareViewModel.distanceCm.collectAsState()

            LaunchedEffect(isConnected, distanceCm) {
                if (isConnected && distanceCm < 100) {
                    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                        vibratorManager.defaultVibrator
                    } else {
                        @Suppress("DEPRECATION")
                        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(500)
                    }
                    hardwareViewModel.speakWarning(distanceCm)
                }
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
