package com.example.rafiq.presentation.bemyeyes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BeMyEyesViewModel @Inject constructor() : ViewModel() {

    private val _isCalling = MutableStateFlow(true)
    val isCalling: StateFlow<Boolean> = _isCalling.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _isVideoOn = MutableStateFlow(true)
    val isVideoOn: StateFlow<Boolean> = _isVideoOn.asStateFlow()

    private var connectionJob: Job? = null

    init {
        connectionJob = viewModelScope.launch {
            delay(3000)
            // Only connect if still calling (user hasn't ended the call)
            if (_isCalling.value) {
                _isCalling.value = false
                _isConnected.value = true
            }
        }
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
    }

    fun toggleVideo() {
        _isVideoOn.value = !_isVideoOn.value
    }

    fun endCall() {
        connectionJob?.cancel()
        connectionJob = null
        _isCalling.value = false
        _isConnected.value = false
        _isMuted.value = false
        _isVideoOn.value = true
    }

    override fun onCleared() {
        super.onCleared()
        connectionJob?.cancel()
    }
}
