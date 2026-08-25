package com.example.rafiq.presentation.signlanguage

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rafiq.data.hardware.TtsManager
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SignLanguageUiState(
    val isProcessing: Boolean = false,
    val currentGesture: String = "",
    val recognizedText: String = "",
    val handDetected: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class SignLanguageViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ttsManager: TtsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignLanguageUiState())
    val uiState: StateFlow<SignLanguageUiState> = _uiState.asStateFlow()

    private var gestureRecognizerHelper: GestureRecognizerHelper? = null
    private var frameTimestampMs = 0L

    fun initializeRecognizer() {
        gestureRecognizerHelper?.close()
        gestureRecognizerHelper = GestureRecognizerHelper(
            context = context,
            onResult = ::onGestureRecognizerResult,
            onError = ::onGestureRecognizerError
        )
        frameTimestampMs = 0L
    }

    fun getHelper(): GestureRecognizerHelper? = gestureRecognizerHelper

    fun processFrame(bitmap: Bitmap) {
        val helper = gestureRecognizerHelper ?: return
        _uiState.update { it.copy(isProcessing = true) }
        helper.recognizeAsync(bitmap, frameTimestampMs)
        frameTimestampMs += 16L
    }

    private fun onGestureRecognizerResult(result: GestureRecognizerResult) {
        viewModelScope.launch(Dispatchers.Main) {
            val gestures = result.gestures()
            val landmarks = result.landmarks()

            if (gestures.isNullOrEmpty() || gestures[0].isEmpty()) {
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        handDetected = landmarks?.isNotEmpty() == true,
                        currentGesture = if (landmarks?.isNotEmpty() == true) "Hand detected" else ""
                    )
                }
                return@launch
            }

            val topGesture = gestures[0][0]
            val gestureName = topGesture.categoryName()
            val confidence = topGesture.score()

            if (confidence > CONFIDENCE_THRESHOLD) {
                val displayText = GESTURE_LABELS[gestureName] ?: gestureName
                val isDifferent = displayText != _uiState.value.currentGesture

                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        currentGesture = displayText,
                        handDetected = true,
                        recognizedText = it.recognizedText + if (isDifferent) displayText else ""
                    )
                }

                if (isDifferent && displayText.isNotEmpty()) {
                    ttsManager.speak(displayText)
                }
            } else {
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        handDetected = landmarks?.isNotEmpty() == true
                    )
                }
            }
        }
    }

    private fun onGestureRecognizerError(error: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _uiState.update {
                it.copy(isProcessing = false, errorMessage = error)
            }
        }
    }

    fun clearText() {
        _uiState.update { it.copy(recognizedText = "", currentGesture = "") }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        gestureRecognizerHelper?.close()
        gestureRecognizerHelper = null
    }

    companion object {
        private const val CONFIDENCE_THRESHOLD = 0.7f

        private val GESTURE_LABELS = mapOf(
            "Closed_Fist" to "Fist",
            "Open_Palm" to "Hello",
            "Pointing_Up" to "A",
            "Thumb_Down" to "No",
            "Thumb_Up" to "Yes",
            "Victory" to "Peace",
            "ILoveYou" to "I Love You"
        )
    }
}
