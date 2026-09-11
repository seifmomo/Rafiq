package com.example.rafiq.safety

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.rafiq.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Foreground service that keeps [LocationRelay] publishing the user's location
 * even while the app is backgrounded, so a guardian can follow the person live.
 */
@AndroidEntryPoint
class LocationShareService : Service() {

    @Inject
    lateinit var locationRelay: LocationRelay

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        RafiqChannels.ensure(
            this, RafiqChannels.LIVE_LOCATION,
            "Live location sharing", "Publishes live location so guardians can follow you"
        )
        val notification = NotificationCompat.Builder(this, RafiqChannels.LIVE_LOCATION)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Live location sharing")
            .setContentText("Your location is being shared with your guardian")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        startForeground(NOTIFICATION_LIVE_LOCATION, notification)
        locationRelay.start()
        return START_STICKY
    }

    override fun onDestroy() {
        locationRelay.stop()
        super.onDestroy()
    }

    companion object {
        const val NOTIFICATION_LIVE_LOCATION = 1003
    }
}