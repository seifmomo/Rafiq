package com.example.rafiq.safety

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object RafiqChannels {
    const val FALL_DETECTION = "fall_detection"
    const val FALL_ALERT = "fall_alert"
    const val LIVE_LOCATION = "live_location"

    fun ensure(context: Context, id: String, name: String, description: String = "") {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH).apply {
            this.description = description
            setShowBadge(false)
        }
        manager.createNotificationChannel(channel)
    }
}