package com.example.rafiq.presentation.sos

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rafiq.data.local.UserPreferences
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SosViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _countdown = MutableStateFlow(10)
    val countdown: StateFlow<Int> = _countdown.asStateFlow()

    private val _isFallDetected = MutableStateFlow(false)
    val isFallDetected: StateFlow<Boolean> = _isFallDetected.asStateFlow()

    private val _sosSent = MutableStateFlow(false)
    val sosSent: StateFlow<Boolean> = _sosSent.asStateFlow()

    private val _sosError = MutableStateFlow<String?>(null)
    val sosError: StateFlow<String?> = _sosError.asStateFlow()

    val emergencyContact: StateFlow<String> = userPreferences.emergencyContact
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "+1234567890")

    private var countdownJob: Job? = null

    fun simulateFall() {
        if (_isFallDetected.value) return

        _isFallDetected.value = true
        _countdown.value = 10
        _sosSent.value = false
        _sosError.value = null
        startCountdown()
    }

    fun cancelSos() {
        countdownJob?.cancel()
        countdownJob = null
        _isFallDetected.value = false
        _countdown.value = 10
        _sosSent.value = false
        _sosError.value = null
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

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(): Pair<Double, Double> {
        return try {
            val cancellationTokenSource = CancellationTokenSource()
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).await()
            if (location != null) {
                Pair(location.latitude, location.longitude)
            } else {
                Pair(0.0, 0.0)
            }
        } catch (e: Exception) {
            Log.e("SOS", "Location failed", e)
            Pair(0.0, 0.0)
        }
    }

    private suspend fun sendSos() {
        try {
            val contact = userPreferences.emergencyContact.first()
            val (lat, lng) = getCurrentLocation()
            val locationUrl = if (lat != 0.0 || lng != 0.0) {
                "https://maps.google.com/?q=$lat,$lng"
            } else {
                "Location unavailable"
            }
            val message = "EMERGENCY: RAFIQ user needs help! $locationUrl"

            val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }
            smsManager.sendTextMessage(contact, null, message, null, null)

            sendToFirebase(lat, lng, message, contact)

            _sosSent.value = true
        } catch (e: SecurityException) {
            _sosError.value = "SMS permission not granted. Please allow SMS in settings."
            _sosSent.value = true
        } catch (e: Exception) {
            _sosError.value = "Failed to send SOS: ${e.localizedMessage ?: "Unknown error"}"
            _sosSent.value = true
        }
    }

    private fun sendToFirebase(lat: Double, lng: Double, message: String, contactNumber: String) {
        try {
            val database = FirebaseDatabase.getInstance().getReference("sos_alerts")
            val userId = "demo_user"
            val alertId = database.child(userId).push().key
            if (alertId != null) {
                val payload = mapOf(
                    "timestamp" to System.currentTimeMillis(),
                    "latitude" to lat,
                    "longitude" to lng,
                    "message" to message,
                    "targetNumber" to contactNumber
                )
                database.child(userId).child(alertId).setValue(payload)
                    .addOnSuccessListener {
                        Log.d("SOS", "SOS sent to Firebase successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e("SOS", "Failed to send SOS to Firebase", e)
                    }
            }
        } catch (e: Exception) {
            Log.e("SOS", "Firebase SOS failed", e)
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}
