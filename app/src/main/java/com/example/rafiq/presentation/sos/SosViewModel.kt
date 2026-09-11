package com.example.rafiq.presentation.sos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rafiq.data.local.UserPreferences
import com.example.rafiq.safety.SosAlertSender
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SosViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val sosAlertSender: SosAlertSender
) : ViewModel() {

    private val _countdown = MutableStateFlow(10)
    val countdown: StateFlow<Int> = _countdown.asStateFlow()

    private val _isFallDetected = MutableStateFlow(false)
    val isFallDetected: StateFlow<Boolean> = _isFallDetected.asStateFlow()

    private val _sosSent = MutableStateFlow(false)
    val sosSent: StateFlow<Boolean> = _sosSent.asStateFlow()

    private val _sosError = MutableStateFlow<String?>(null)
    val sosError: StateFlow<String?> = _sosError.asStateFlow()

    private val _lastLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val lastLocation: StateFlow<Pair<Double, Double>?> = _lastLocation.asStateFlow()

    private val _lastLocationUrl = MutableStateFlow<String?>(null)
    val lastLocationUrl: StateFlow<String?> = _lastLocationUrl.asStateFlow()

    val emergencyContact: StateFlow<String> = userPreferences.emergencyContact
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "+1234567890")

    private var countdownJob: Job? = null

    fun simulateFall() {
        if (_isFallDetected.value) return

        _isFallDetected.value = true
        _countdown.value = 10
        _sosSent.value = false
        _sosError.value = null
        _lastLocation.value = null
        _lastLocationUrl.value = null
        startCountdown()
    }

    fun cancelSos() {
        countdownJob?.cancel()
        countdownJob = null
        _isFallDetected.value = false
        _countdown.value = 10
        _sosSent.value = false
        _sosError.value = null
        _lastLocation.value = null
        _lastLocationUrl.value = null
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (_countdown.value > 0 && _isFallDetected.value) {
                delay(1000L)
                if (_isFallDetected.value) {
                    _countdown.value -= 1
                }
            }
            if (_countdown.value == 0 && _isFallDetected.value) {
                sendSos()
            }
        }
    }

    private suspend fun sendSos() {
        val result = sosAlertSender.sendAlert()
        _lastLocation.value = if (result.locationUrl != null) Pair(result.latitude, result.longitude) else null
        _lastLocationUrl.value = result.locationUrl
        _sosSent.value = true
        _sosError.value = result.error
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}
