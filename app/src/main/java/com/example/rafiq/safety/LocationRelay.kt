package com.example.rafiq.safety

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Publishes the user's location to Firebase RTDB (`locations/<user>/latest`) every
 * few seconds while sharing is active. A live-tracking map (or a guardian's screen)
 * subscribes to the same node to follow the person in real time.
 */
@Singleton
class LocationRelay @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _lastLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val lastLocation: StateFlow<Pair<Double, Double>?> = _lastLocation.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile
    private var active = false
    private var job: Job? = null

    val isSharing: Boolean get() = active

    fun start() {
        if (active) return
        active = true
        job = scope.launch {
            while (isActive && active) {
                pushOnce()
                delay(UPDATE_INTERVAL_MS)
            }
        }
    }

    fun stop() {
        active = false
        job?.cancel()
        job = null
    }

    @SuppressLint("MissingPermission")
    suspend fun pushOnce() {
        val location = try {
            val token = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                token.token
            ).await() ?: fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            Log.e("LocationRelay", "Location failed", e)
            null
        } ?: return

        val lat = location.latitude
        val lng = location.longitude
        _lastLocation.value = lat to lng

        try {
            val ref = FirebaseDatabase.getInstance()
                .getReference("locations")
                .child(USER_ID)
                .child("latest")
            ref.setValue(
                mapOf(
                    "latitude" to lat,
                    "longitude" to lng,
                    "timestamp" to System.currentTimeMillis(),
                    "source" to "rafiq-android"
                )
            )
        } catch (e: Exception) {
            Log.w("LocationRelay", "Firebase location skipped (Firebase not configured)", e)
        }
    }

    companion object {
        const val USER_ID = "demo_user"
        private const val UPDATE_INTERVAL_MS = 4_000L
    }
}