package com.example.rafiq.presentation.tracking

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rafiq.safety.LocationRelay
import com.example.rafiq.safety.LocationShareService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LiveTrackingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    internal val locationRelay: LocationRelay
) : ViewModel() {

    private val _marker = MutableStateFlow<Pair<Double, Double>?>(null)
    val marker: StateFlow<Pair<Double, Double>?> = _marker.asStateFlow()

    private val _sharing = MutableStateFlow(false)
    val sharing: StateFlow<Boolean> = _sharing.asStateFlow()

    private val _trackingEnabled = MutableStateFlow(false)
    val trackingEnabled: StateFlow<Boolean> = _trackingEnabled.asStateFlow()

    private val _firebaseConfigured = MutableStateFlow(true)
    val firebaseConfigured: StateFlow<Boolean> = _firebaseConfigured.asStateFlow()

    private val _lastUpdateText = MutableStateFlow("Waiting for live location…")
    val lastUpdateText: StateFlow<String> = _lastUpdateText.asStateFlow()

    val lastLocalLocation = locationRelay.lastLocation
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private var valueListener: ValueEventListener? = null

    init {
        _sharing.value = locationRelay.isSharing
        viewModelScope.launch {
            locationRelay.lastLocation.collect { loc ->
                if (loc != null) {
                    _marker.value = loc
                    _lastUpdateText.value = "Last ping " + formatTime(System.currentTimeMillis())
                }
            }
        }
        if (_sharing.value) {
            attachListener()
        }
    }

    fun startSharing() {
        if (locationRelay.isSharing) return
        ContextCompat.startForegroundService(context, Intent(context, LocationShareService::class.java))
        _sharing.value = true
        attachListener()
    }

    fun stopSharing() {
        locationRelay.stop()
        context.stopService(Intent(context, LocationShareService::class.java))
        _sharing.value = false
    }

    fun enableTracking() {
        if (trackingEnabled.value) return
        _trackingEnabled.value = true
        attachListener()
    }

    private fun attachListener() {
        if (valueListener != null) return
        try {
            val ref = FirebaseDatabase.getInstance()
                .getReference("locations")
                .child(LocationRelay.USER_ID)
                .child("latest")
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lat = snapshot.child("latitude").getValue(Double::class.java) ?: return
                    val lng = snapshot.child("longitude").getValue(Double::class.java) ?: return
                    val ts = snapshot.child("timestamp").getValue(Long::class.java) ?: System.currentTimeMillis()
                    _marker.value = lat to lng
                    _trackingEnabled.value = true
                    _lastUpdateText.value = "Updated " + formatTime(ts)
                }

                override fun onCancelled(error: DatabaseError) {
                    _firebaseConfigured.value = false
                }
            }
            ref.addValueEventListener(listener)
            valueListener = listener
        } catch (e: Exception) {
            _firebaseConfigured.value = false
        }
    }

    override fun onCleared() {
        valueListener?.let {
            try {
                val ref = FirebaseDatabase.getInstance()
                    .getReference("locations")
                    .child(LocationRelay.USER_ID)
                    .child("latest")
                ref.removeEventListener(it)
            } catch (_: Exception) {
            }
        }
        super.onCleared()
    }
}

private fun formatTime(ts: Long): String =
    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(ts))