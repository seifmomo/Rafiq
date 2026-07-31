package com.example.rafiq.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rafiq.data.local.UserPreferences
import com.example.rafiq.data.repository.AuthRepository
import com.example.rafiq.data.remote.api.UserApi
import com.example.rafiq.data.remote.dto.GuardianModeRequest
import com.example.rafiq.data.remote.dto.UpdateProfileRequest
import com.example.rafiq.util.BackupManager
import com.example.rafiq.util.LocaleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val authRepository: AuthRepository,
    private val userApi: UserApi,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    val emergencyContact: StateFlow<String> = userPreferences.emergencyContact
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "+1234567890")

    val darkTheme: StateFlow<String> = userPreferences.darkTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    val language: StateFlow<String> = userPreferences.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    val fontSize: StateFlow<String> = userPreferences.fontSize
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "normal")

    val fontFamily: StateFlow<String> = userPreferences.fontFamily
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "default")

    val speechRate: StateFlow<Float> = userPreferences.speechRate
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1.0f)

    val guardianMode: StateFlow<Boolean> = userPreferences.guardianMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> = _logoutState.asStateFlow()

    sealed class LogoutState {
        object Idle : LogoutState()
        object LoggingOut : LogoutState()
        object Done : LogoutState()
        data class Error(val message: String) : LogoutState()
    }

    fun saveEmergencyContact(contact: String) {
        viewModelScope.launch {
            userPreferences.setEmergencyContact(contact)
            try {
                userApi.updateProfile(UpdateProfileRequest(emergencyContact = contact))
            } catch (_: Exception) {}
        }
    }

    fun setDarkTheme(mode: String) {
        viewModelScope.launch {
            userPreferences.setDarkTheme(mode)
            try {
                userApi.updateProfile(UpdateProfileRequest(darkTheme = mode))
            } catch (_: Exception) {}
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            userPreferences.setLanguage(lang)
            LocaleManager.persistLanguage(appContext, lang)
        }
    }

    fun setFontSize(size: String) {
        viewModelScope.launch {
            userPreferences.setFontSize(size)
            try {
                userApi.updateProfile(UpdateProfileRequest(fontSize = size))
            } catch (_: Exception) {}
        }
    }

    fun setFontFamily(family: String) {
        viewModelScope.launch {
            userPreferences.setFontFamily(family)
            try {
                userApi.updateProfile(UpdateProfileRequest(fontFamily = family))
            } catch (_: Exception) {}
        }
    }

    fun setSpeechRate(rate: Float) {
        viewModelScope.launch {
            userPreferences.setSpeechRate(rate)
            try {
                userApi.updateProfile(UpdateProfileRequest(speechRate = rate.toDouble()))
            } catch (_: Exception) {}
        }
    }

    fun setGuardianMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setGuardianMode(enabled)
            try {
                userApi.updateGuardianMode(GuardianModeRequest(enabled))
            } catch (_: Exception) {}
        }
    }

    fun logout() {
        viewModelScope.launch {
            _logoutState.value = LogoutState.LoggingOut
            try {
                authRepository.logout()
                _logoutState.value = LogoutState.Done
            } catch (e: Exception) {
                _logoutState.value = LogoutState.Error(e.message ?: "Logout failed")
            }
        }
    }

    fun deleteAccount(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            when (val result = authRepository.deleteAccount()) {
                is AuthRepository.Result.Success -> onResult(true, "Account deleted")
                is AuthRepository.Result.Error -> onResult(false, result.message)
            }
        }
    }

    fun backupData(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = BackupManager.backupData(appContext)
            result.fold(
                onSuccess = { path -> onResult(true, path) },
                onFailure = { e -> onResult(false, e.message ?: "Backup failed") }
            )
        }
    }

    fun restoreData(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = BackupManager.restoreData(appContext)
            result.fold(
                onSuccess = { onResult(true, "Restore successful") },
                onFailure = { e -> onResult(false, e.message ?: "Restore failed") }
            )
        }
    }
}
