package com.example.rafiq

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.rafiq.data.local.UserPreferences
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RafiqFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "Message received: ${remoteMessage.data}")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userPrefs = UserPreferences(applicationContext)
                val isGuardian = userPrefs.guardianMode.first()
                if (isGuardian) {
                    if (remoteMessage.data["emergency"] == "true") {
                        triggerSirenAndNotification(remoteMessage.data["message"] ?: "EMERGENCY SOS")
                    }
                } else {
                    Log.d("FCM", "Ignored message because Guardian Mode is OFF.")
                }
            } catch (e: Exception) {
                Log.e("FCM", "Error processing message", e)
            }
        }
    }

    private fun triggerSirenAndNotification(message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "emergency_sos_channel"

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0)

        try {
            val mediaPlayer = MediaPlayer.create(this, android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI)
            mediaPlayer.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            mediaPlayer.isLooping = true
            mediaPlayer.start()

            CoroutineScope(Dispatchers.Main).launch {
                kotlinx.coroutines.delay(30000)
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    mediaPlayer.release()
                }
            }
        } catch (e: Exception) {
            Log.e("FCM", "Failed to play siren", e)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Emergency SOS Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical alerts from RAFIQ users in danger"
                setBypassDnd(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("EMERGENCY SOS")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, true)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onNewToken(token: String) {
        Log.d("FCM", "New Token: $token")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userPrefs = UserPreferences(applicationContext)
                if (userPrefs.guardianMode.first()) {
                    val database = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("guardians")
                    database.child("demo_user").child("fcmToken").setValue(token)
                }
            } catch (e: Exception) {
                Log.e("FCM", "Failed to store FCM token", e)
            }
        }
    }
}
