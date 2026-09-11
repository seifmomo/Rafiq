package com.example.rafiq.safety

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.annotation.SuppressLint
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.rafiq.R
import com.example.rafiq.util.FallDetectionClassifier
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Real fall-to-SOS wiring. A foreground foreground service listens to the
 * accelerometer, feeds [FallDetectionClassifier] a rolling window of samples and,
 * on a confirmed fall, shows a 10-second notification with a "cancel" action.
 * If nobody cancels, it pushes the exact same SOS the manual button sends
 * (SMS + Firebase) via [SosAlertSender].
 */
@AndroidEntryPoint
class FallSensorService : Service(), SensorEventListener {

    @Inject
    lateinit var alertSender: SosAlertSender

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var releaseJob: Job? = null

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null

    private val classifier = FallDetectionClassifier()
    private val window = ArrayDeque<FloatArray>()

    @Volatile
    private var fallInProgress = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startRun()
        return START_STICKY
    }

    private fun startRun() {
        RafiqChannels.ensure(
            this, RafiqChannels.FALL_DETECTION,
            "Fall detection", "Monitors the accelerometer and alerts contacts on falls"
        )
        val notification = NotificationCompat.Builder(this, RafiqChannels.FALL_DETECTION)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Fall detection is ON")
            .setContentText("RAFIQ will alert your emergency contact if you fall")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        startForeground(NOTIFICATION_FALL_DETECTION, notification)

        if (sensorManager == null) {
            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        }
        accelerometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return
        if (fallInProgress) return

        val sample = floatArrayOf(event.values[0], event.values[1], event.values[2])
        val snapshot: List<FloatArray>
        synchronized(window) {
            window.addLast(sample)
            while (window.size > WINDOW_SIZE) window.removeFirst()
            snapshot = window.toList()
        }

        if (classifier.isFallDetected(snapshot)) {
            onFallDetected()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun onFallDetected() {
        fallInProgress = true
        suspendedCancel = false
        vibrate()
        showFallAlertNotification()
        releaseJob = scope.launch {
            delay(CANCEL_WINDOW_MS)
            if (!suspendedCancel) {
                notifySosStatus("Contacting emergency contact…")
                val result = alertSender.sendAlert()
                notifySosStatus(result.error ?: "SOS sent with your live location")
            } else {
                cancelFallAlertNotification()
            }
            fallInProgress = false
            releaseJob = null
        }
    }

    private fun vibrate() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as? Vibrator
        }
        vibrator?.let { v ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 300, 200, 300), -1))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(longArrayOf(0, 300, 200, 300), -1)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showFallAlertNotification() {
        RafiqChannels.ensure(
            this, RafiqChannels.FALL_ALERT,
            "Fall alerts", "Shown when a possible fall needs confirmation"
        )
        val cancelIntent = Intent(this, FallCancelReceiver::class.java).apply {
            action = ACTION_CANCEL_FALL
        }
        val cancelPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, RafiqChannels.FALL_ALERT)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Possible fall detected")
            .setContentText("SOS will be sent in 10 seconds unless you cancel")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(cancelPendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "It's OK — cancel SOS", cancelPendingIntent)
            .build()
        NotificationManagerCompat.from(this).notify(NOTIFICATION_FALL_ALERT, notification)
    }

    @SuppressLint("MissingPermission")
    private fun notifySosStatus(text: String) {
        val notification: Notification = NotificationCompat.Builder(this, RafiqChannels.FALL_ALERT)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Fall detected")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(this).notify(NOTIFICATION_FALL_ALERT, notification)
    }

    private fun cancelFallAlertNotification() {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(NOTIFICATION_FALL_ALERT)
    }

    override fun onDestroy() {
        sensorManager?.unregisterListener(this)
        releaseJob?.cancel()
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val WINDOW_SIZE = 40
        const val CANCEL_WINDOW_MS = 10_000L
        const val ACTION_CANCEL_FALL = "com.example.rafiq.action.CANCEL_FALL"
        const val NOTIFICATION_FALL_DETECTION = 1001
        const val NOTIFICATION_FALL_ALERT = 1002

        @Volatile
        private var suspendedCancel = false

        fun cancelPending() {
            suspendedCancel = true
        }
    }
}