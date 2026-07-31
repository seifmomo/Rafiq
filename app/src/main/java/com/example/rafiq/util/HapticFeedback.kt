package com.example.rafiq.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object HapticFeedback {

    fun lightClick(context: Context) {
        vibrate(context, 30, VibrationEffect.DEFAULT_AMPLITUDE)
    }

    fun heavyClick(context: Context) {
        vibrate(context, 60, VibrationEffect.DEFAULT_AMPLITUDE)
    }

    fun error(context: Context) {
        vibrate(context, 200, VibrationEffect.DEFAULT_AMPLITUDE)
    }

    private fun vibrate(context: Context, durationMs: Long, amplitude: Int) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                manager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(durationMs, amplitude))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (_: Exception) { }
    }
}
