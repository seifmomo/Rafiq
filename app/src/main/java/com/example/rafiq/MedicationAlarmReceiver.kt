package com.example.rafiq

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

class MedicationAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_MEDICINE_NAME = "extra_medicine_name"
        private const val TAG = "MedicationAlarm"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val medicineName = intent.getStringExtra(EXTRA_MEDICINE_NAME) ?: "الدواء"
        Log.d(TAG, "Alarm fired for medication: $medicineName")

        val message = "تنبيه، حان الآن موعد دواء $medicineName"

        var tts: TextToSpeech? = null
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.setLanguage(Locale("ar"))
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onDone(utteranceId: String?) {
                        tts?.stop()
                        tts?.shutdown()
                    }
                    override fun onError(utteranceId: String?) {
                        tts?.stop()
                        tts?.shutdown()
                    }
                })
                tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, "MedicationReminder")
            } else {
                Log.e(TAG, "TTS initialization failed with status: $status")
            }
        }
    }
}
