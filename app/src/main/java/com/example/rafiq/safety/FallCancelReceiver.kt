package com.example.rafiq.safety

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Receives the "It's OK — cancel SOS" action from the fall alert notification. */
class FallCancelReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == FallSensorService.ACTION_CANCEL_FALL) {
            FallSensorService.cancelPending()
        }
    }
}