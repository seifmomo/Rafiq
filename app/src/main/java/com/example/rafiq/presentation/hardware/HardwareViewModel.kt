package com.example.rafiq.presentation.hardware

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rafiq.data.hardware.TtsManager
import com.example.rafiq.domain.hardware.BleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HardwareViewModel @Inject constructor(
    private val bleManager: BleManager,
    private val ttsManager: TtsManager
) : ViewModel() {

    val isConnected = bleManager.isConnected
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val distanceCm = bleManager.distanceCm
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 300)
    val isScanning = bleManager.isScanning
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private var lastWarningTimeMs = 0L
    private val warningCooldownMs = 3000L

    fun startScanningAndConnect() {
        bleManager.startScanning()
        // Mock connection after scanning
        bleManager.connectToDevice("00:11:22:33:44:55")
    }

    fun disconnect() {
        bleManager.disconnect()
    }

    /**
     * Speaks an obstacle warning with a cooldown to prevent TTS spam.
     * Returns true if the warning was actually spoken, false if suppressed by cooldown.
     */
    fun speakWarning(distanceCm: Int): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastWarningTimeMs < warningCooldownMs) {
            return false
        }
        lastWarningTimeMs = now
        ttsManager.speak("Warning, obstacle ahead at $distanceCm centimeters", "obstacle_alert")
        return true
    }
}
