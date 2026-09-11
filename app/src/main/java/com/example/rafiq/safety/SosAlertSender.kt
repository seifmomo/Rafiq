package com.example.rafiq.safety

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.rafiq.data.local.UserPreferences
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

data class SosSendResult(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationUrl: String? = null,
    val error: String? = null
)

/**
 * Single SOS sending path shared by the manual SOS button (SosViewModel) and the
 * automatic fall detection service (FallSensorService): grab a high-accuracy fix,
 * text the emergency contact, then mirror the alert into Firebase for any guardian.
 */
@Singleton
class SosAlertSender @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferences: UserPreferences
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    private suspend fun currentLocation(): Pair<Double, Double> {
        return try {
            val token = CancellationTokenSource()
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                token.token
            ).await()
            if (location != null) location.latitude to location.longitude else 0.0 to 0.0
        } catch (e: Exception) {
            Log.e("SosAlertSender", "Location failed", e)
            0.0 to 0.0
        }
    }

    suspend fun sendAlert(): SosSendResult {
        val contact = userPreferences.emergencyContact.first()
        val (lat, lng) = currentLocation()
        val locationUrl = if (lat != 0.0 || lng != 0.0) {
            "https://maps.google.com/?q=$lat,$lng"
        } else {
            null
        }
        val message = "EMERGENCY: RAFIQ user needs help! ${locationUrl ?: "Location unavailable"}"

        return try {
            val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }
            smsManager.sendTextMessage(contact, null, message, null, null)

            writeAlertToFirebase(lat, lng, message, contact)

            SosSendResult(latitude = lat, longitude = lng, locationUrl = locationUrl)
        } catch (e: SecurityException) {
            SosSendResult(error = "SMS permission not granted. Use Share / Call below to alert your contact.")
        } catch (e: Exception) {
            SosSendResult(error = "Failed to send SMS: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    private fun writeAlertToFirebase(lat: Double, lng: Double, message: String, contactNumber: String) {
        try {
            val database = FirebaseDatabase.getInstance().getReference("sos_alerts")
            val userId = "demo_user"
            val alertId = database.child(userId).push().key ?: return
            database.child(userId).child(alertId).setValue(
                mapOf(
                    "timestamp" to System.currentTimeMillis(),
                    "latitude" to lat,
                    "longitude" to lng,
                    "message" to message,
                    "targetNumber" to contactNumber
                )
            )
        } catch (e: Exception) {
            Log.w("SosAlertSender", "Firebase SOS skipped (Firebase not configured)", e)
        }
    }
}