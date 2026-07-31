package com.example.rafiq.data.hardware

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.rafiq.data.local.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TtsManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferences: UserPreferences
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private val isInitialized = AtomicBoolean(false)
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.getDefault())
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fall back to English
                val fallbackResult = tts?.setLanguage(Locale.ENGLISH)
                if (fallbackResult == TextToSpeech.LANG_MISSING_DATA || fallbackResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w("TtsManager", "TTS language not supported, even English fallback failed")
                    return
                }
            }
            isInitialized.set(true)
            scope.launch {
                userPreferences.speechRate.collectLatest { rate ->
                    tts?.setSpeechRate(rate)
                }
            }
        } else {
            Log.e("TtsManager", "TTS initialization failed with status: $status")
        }
    }


    /**
     * Speaks the given text, interrupting any currently speaking text.
     * Use for high-priority messages like obstacle warnings.
     */
    fun speak(text: String, utteranceId: String? = null) {
        if (isInitialized.get()) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId ?: System.currentTimeMillis().toString())
        }
    }

    /**
     * Speaks the given text without interrupting currently speaking text.
     * Use for queued informational messages.
     */
    fun speakQueued(text: String, utteranceId: String? = null) {
        if (isInitialized.get()) {
            tts?.speak(text, TextToSpeech.QUEUE_ADD, null, utteranceId ?: System.currentTimeMillis().toString())
        }
    }

    /**
     * Stops any currently speaking text.
     */
    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        isInitialized.set(false)
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
