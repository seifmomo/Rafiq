package com.example.rafiq.presentation.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rafiq.data.hardware.TtsManager
import com.example.rafiq.data.local.ChatMessage
import com.example.rafiq.data.local.ChatMessageDao
import com.example.rafiq.data.local.UserPreferences
import com.example.rafiq.data.remote.GeminiManager
import com.example.rafiq.data.remote.api.ChatApi
import com.example.rafiq.data.remote.dto.CreateMessageRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class VoiceViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ttsManager: TtsManager,
    private val geminiManager: GeminiManager,
    private val userPreferences: UserPreferences,
    private val chatMessageDao: ChatMessageDao,
    private val chatApi: ChatApi
) : ViewModel() {

    private val _spokenText = MutableStateFlow("Tap the mic to start speaking...")
    val spokenText: StateFlow<String> = _spokenText.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            _spokenText.value = "Listening..."
        }

        override fun onBeginningOfSpeech() {}

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            _isListening.value = false
        }

        override fun onError(error: Int) {
            _isListening.value = false
            val errorMessage = when (error) {
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                SpeechRecognizer.ERROR_NETWORK -> "Network error"
                SpeechRecognizer.ERROR_AUDIO -> "Audio error"
                SpeechRecognizer.ERROR_SERVER -> "Server error"
                SpeechRecognizer.ERROR_CLIENT -> "Client error"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected"
                SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permission denied"
                else -> "Recognition error"
            }
            _spokenText.value = errorMessage
        }

        override fun onResults(results: Bundle?) {
            _isListening.value = false
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val spoken = matches[0].lowercase()
                _spokenText.value = spoken

                viewModelScope.launch {
                    val aiResponse = geminiManager.processVoiceCommand(spoken)
                    ttsManager.speak(aiResponse)
                    userPreferences.addPoints(15)
                    persistConversation(spoken, aiResponse)
                }
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val matches =
                partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                _spokenText.value = matches[0] + "..."
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    fun toggleListening() {
        if (_isListening.value) {
            stopListening()
        } else {
            startListening()
        }
    }

    private fun startListening() {
        try {
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                _spokenText.value = "Speech recognition not available on this device"
                return
            }

            // Destroy previous instance to avoid stale state
            speechRecognizer?.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(recognitionListener)

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, java.util.Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            }

            speechRecognizer?.startListening(intent)
            _isListening.value = true
        } catch (e: Exception) {
            _spokenText.value = "Speech recognition error: ${e.localizedMessage ?: "Unknown"}"
            _isListening.value = false
            speechRecognizer?.destroy()
            speechRecognizer = null
        }
    }

    private suspend fun persistConversation(spoken: String, reply: String) {
        val timestamp = System.currentTimeMillis()

        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            message = spoken,
            timestamp = timestamp,
            sender = "user"
        )
        val aiMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            message = reply,
            timestamp = timestamp + 1,
            sender = "rafiq"
        )
        chatMessageDao.insertMessage(userMsg)
        chatMessageDao.insertMessage(aiMsg)

        try {
            chatApi.sendMessage(CreateMessageRequest(userMsg.id, spoken, "user", timestamp))
            chatApi.sendMessage(CreateMessageRequest(aiMsg.id, reply, "rafiq", timestamp + 1))
        } catch (_: Exception) {}
    }

    private fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {
            // Ignore — recognizer may already be stopped
        }
        _isListening.value = false
    }

    override fun onCleared() {
        super.onCleared()
        try {
            speechRecognizer?.destroy()
        } catch (_: Exception) {
            // Ignore cleanup errors
        }
        speechRecognizer = null
    }
}
