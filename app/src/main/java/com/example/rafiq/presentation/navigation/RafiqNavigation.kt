package com.example.rafiq.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rafiq.data.local.UserPreferences
import com.example.rafiq.presentation.awareness.AwarenessScreen
import com.example.rafiq.presentation.bemyeyes.BeMyEyesScreen
import com.example.rafiq.presentation.chat.ChatScreen
import com.example.rafiq.presentation.companionscore.CompanionScoreScreen
import com.example.rafiq.presentation.contacts.ContactsScreen
import com.example.rafiq.presentation.gamification.AddPlaceScreen
import com.example.rafiq.presentation.home.HomeScreen
import com.example.rafiq.presentation.hospital.HospitalScreen
import com.example.rafiq.presentation.intro.IntroPage
import com.example.rafiq.presentation.intro.IntroScreen
import com.example.rafiq.presentation.intro.LoginScreen
import com.example.rafiq.presentation.learning.LearningScreen
import com.example.rafiq.presentation.map.MapScreen
import com.example.rafiq.presentation.medication.MedicationScreen
import com.example.rafiq.presentation.settings.SettingsScreen
import com.example.rafiq.presentation.sos.SosScreen
import com.example.rafiq.presentation.voice.VoiceScreen
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object Intro : Screen("intro_screen")
    object Home : Screen("home_screen")
    object Map : Screen("map_screen")
    object Voice : Screen("voice_screen")
    object Settings : Screen("settings_screen")
    object AddPlace : Screen("add_place_screen")
    object BeMyEyes : Screen("be_my_eyes_screen")
    object SOS : Screen("sos_screen")
    object Learning : Screen("learning_screen")
    object Awareness : Screen("awareness_screen")
    object Hospital : Screen("hospital_screen")
    object CompanionScore : Screen("companion_score_screen")
    object Contacts : Screen("contacts_screen")
    object Medication : Screen("medication_screen")
    object Chat : Screen("chat_screen")
}

@Composable
fun RafiqNavigation(
    startDestination: String = Screen.Home.route,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(tween(280)) { it / 4 } + fadeIn(tween(280))
        },
        exitTransition = {
            fadeOut(tween(180))
        },
        popEnterTransition = {
            fadeIn(tween(220))
        },
        popExitTransition = {
            slideOutHorizontally(tween(220)) { it / 4 } + fadeOut(tween(220))
        }
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Intro.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Intro.route) {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val userPreferences = remember { UserPreferences(context) }
            val completeIntro: () -> Unit = {
                scope.launch {
                    userPreferences.setIntroCompleted(true)
                }
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Intro.route) { inclusive = true }
                }
            }
            IntroScreen(
                pages = listOf(
                    IntroPage(
                        title = "RAFIQ",
                        subtitle = "Your Companion, Every Step."
                    ),
                    IntroPage(
                        title = "Navigate Life with Confidence",
                        subtitle = "Real-time guidance, personalized insights, and a companion who never leaves your side."
                    ),
                    IntroPage(
                        title = "AI Powered. Human Centered.",
                        subtitle = "Smart assistance tailored to your needs, keeping you connected and safe."
                    )
                ),
                onGetStarted = completeIntro,
                onSkip = completeIntro
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Map.route) {
            MapScreen(navController = navController)
        }
        composable(Screen.Voice.route) {
            VoiceScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(Screen.AddPlace.route) {
            AddPlaceScreen(navController = navController)
        }
        composable(Screen.BeMyEyes.route) {
            BeMyEyesScreen(navController = navController)
        }
        composable(Screen.SOS.route) {
            SosScreen(navController = navController)
        }
        composable(Screen.Learning.route) {
            LearningScreen(navController = navController)
        }
        composable(Screen.Awareness.route) {
            AwarenessScreen(navController = navController)
        }
        composable(Screen.Hospital.route) {
            HospitalScreen(navController = navController)
        }
        composable(Screen.CompanionScore.route) {
            CompanionScoreScreen(navController = navController)
        }
        composable(Screen.Contacts.route) {
            ContactsScreen(navController = navController)
        }
        composable(Screen.Medication.route) {
            MedicationScreen(navController = navController)
        }
        composable(Screen.Chat.route) {
            ChatScreen(navController = navController)
        }
    }
}
