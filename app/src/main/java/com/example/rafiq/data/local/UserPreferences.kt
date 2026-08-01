package com.example.rafiq.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userPrefsStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val EMERGENCY_CONTACT = stringPreferencesKey("emergency_contact")
        val TOTAL_POINTS = intPreferencesKey("total_points")
        val DARK_THEME = stringPreferencesKey("dark_theme")
        val LANGUAGE = stringPreferencesKey("language")
        val FONT_SIZE = stringPreferencesKey("font_size")
        val FONT_FAMILY = stringPreferencesKey("font_family")
        val SPEECH_RATE = floatPreferencesKey("speech_rate")
        val INTRO_COMPLETED = booleanPreferencesKey("intro_completed")
        val WHATS_NEW_VERSION = intPreferencesKey("whats_new_version")
        val DISABILITY_TYPE = stringPreferencesKey("disability_type")
        val DISABILITY_PROMPT_SHOWN = booleanPreferencesKey("disability_prompt_shown")
        val GUARDIAN_MODE = booleanPreferencesKey("guardian_mode")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    val introCompleted: Flow<Boolean> = context.userPrefsStore.data.map { prefs ->
        prefs[Keys.INTRO_COMPLETED] ?: false
    }

    val whatsNewVersion: Flow<Int> = context.userPrefsStore.data.map { prefs ->
        prefs[Keys.WHATS_NEW_VERSION] ?: 0
    }

    val disabilityType: Flow<String> = context.userPrefsStore.data.map { prefs ->
        prefs[Keys.DISABILITY_TYPE] ?: ""
    }

    val disabilityPromptShown: Flow<Boolean> = context.userPrefsStore.data.map { prefs ->
        prefs[Keys.DISABILITY_PROMPT_SHOWN] ?: false
    }

    val emergencyContact: Flow<String> = context.userPrefsStore.data.map { prefs ->
        prefs[Keys.EMERGENCY_CONTACT] ?: "+1234567890"
    }

    val totalPoints: Flow<Int> = context.userPrefsStore.data.map { prefs ->
        prefs[Keys.TOTAL_POINTS] ?: 0
    }

    val darkTheme: Flow<String> = context.userPrefsStore.data.map { prefs ->
        prefs[Keys.DARK_THEME] ?: "system"
    }

    val language: Flow<String> = context.userPrefsStore.data.map { prefs ->
        prefs[Keys.LANGUAGE] ?: "en"
    }

    val fontSize: Flow<String> = context.userPrefsStore.data.map { prefs ->
        prefs[Keys.FONT_SIZE] ?: "normal"
    }

    val fontFamily: Flow<String> = context.userPrefsStore.data.map { prefs ->
        prefs[Keys.FONT_FAMILY] ?: "default"
    }

    val speechRate: Flow<Float> = context.userPrefsStore.data.map { prefs ->
        prefs[Keys.SPEECH_RATE] ?: 1.0f
    }

    val guardianMode: Flow<Boolean> = context.userPrefsStore.data.map { prefs ->
        prefs[Keys.GUARDIAN_MODE] ?: false
    }

    val isLoggedIn: Flow<Boolean> = context.userPrefsStore.data.map { prefs ->
        prefs[Keys.IS_LOGGED_IN] ?: false
    }

    suspend fun setEmergencyContact(contact: String) {
        context.userPrefsStore.edit { prefs ->
            prefs[Keys.EMERGENCY_CONTACT] = contact
        }
    }

    suspend fun addPoints(points: Int) {
        context.userPrefsStore.edit { prefs ->
            val current = prefs[Keys.TOTAL_POINTS] ?: 0
            prefs[Keys.TOTAL_POINTS] = current + points
        }
    }

    suspend fun setDarkTheme(mode: String) {
        context.userPrefsStore.edit { prefs ->
            prefs[Keys.DARK_THEME] = mode
        }
    }

    suspend fun setLanguage(lang: String) {
        context.userPrefsStore.edit { prefs ->
            prefs[Keys.LANGUAGE] = lang
        }
    }

    suspend fun setFontSize(size: String) {
        context.userPrefsStore.edit { prefs ->
            prefs[Keys.FONT_SIZE] = size
        }
    }

    suspend fun setFontFamily(family: String) {
        context.userPrefsStore.edit { prefs ->
            prefs[Keys.FONT_FAMILY] = family
        }
    }

    suspend fun setSpeechRate(rate: Float) {
        context.userPrefsStore.edit { prefs ->
            prefs[Keys.SPEECH_RATE] = rate
        }
    }

    suspend fun setIntroCompleted(completed: Boolean) {
        context.userPrefsStore.edit { prefs ->
            prefs[Keys.INTRO_COMPLETED] = completed
        }
    }

    suspend fun setWhatsNewVersion(version: Int) {
        context.userPrefsStore.edit { prefs ->
            prefs[Keys.WHATS_NEW_VERSION] = version
        }
    }

    suspend fun setDisabilityType(type: String) {
        context.userPrefsStore.edit { prefs ->
            prefs[Keys.DISABILITY_TYPE] = type
        }
    }

    suspend fun setDisabilityPromptShown(shown: Boolean) {
        context.userPrefsStore.edit { prefs ->
            prefs[Keys.DISABILITY_PROMPT_SHOWN] = shown
        }
    }

    suspend fun setGuardianMode(enabled: Boolean) {
        context.userPrefsStore.edit { prefs ->
            prefs[Keys.GUARDIAN_MODE] = enabled
        }
    }

    suspend fun setLoggedIn(loggedIn: Boolean) {
        context.userPrefsStore.edit { prefs ->
            prefs[Keys.IS_LOGGED_IN] = loggedIn
        }
    }
}
