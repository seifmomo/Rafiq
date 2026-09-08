package com.example.rafiq.presentation.signlanguage

import android.content.Context
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rafiq.data.hardware.TtsManager
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    val errorMessage: String? = null,
    val modelAvailable: Boolean = false
)

@HiltViewModel
class SignLanguageViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ttsManager: TtsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignLanguageUiState())
    val uiState: StateFlow<SignLanguageUiState> = _uiState.asStateFlow()

    private var gestureRecognizerHelper: GestureRecognizerHelper? = null
    private var lastSubmissionMs = 0L
    private var pendingFrames = 0
    private var stableGesture: String? = null
    private var stableGestureFrames = 0
    private var gestureGoneFrames = 0
    private var handPresentFrames = 0
    private var handAbsentFrames = 0

    private fun isModelAvailable(): Boolean {
        return try {
            context.assets.open(MODEL_FILE).use { true }
        } catch (_: Exception) {
            false
        }
    }

    fun initializeRecognizer() {
        if (!isModelAvailable()) {
            _uiState.update {
                it.copy(
                    modelAvailable = false,
                    errorMessage = "Gesture model not found. Add '$MODEL_FILE' to assets to enable recognition."
                )
            }
            return
        }

        try {
            gestureRecognizerHelper?.close()
            gestureRecognizerHelper = GestureRecognizerHelper(
                context = context,
                onResult = ::onGestureRecognizerResult,
                onError = ::onGestureRecognizerError
            )
            _uiState.update { it.copy(modelAvailable = true, errorMessage = null) }
            lastSubmissionMs = System.currentTimeMillis() - MIN_FRAME_INTERVAL_MS
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to initialize recognizer", e)
            gestureRecognizerHelper = null
            _uiState.update {
                it.copy(
                    modelAvailable = false,
                    errorMessage = "Recognition unavailable: ${e.message}"
                )
            }
        }
    }

    fun getHelper(): GestureRecognizerHelper? = gestureRecognizerHelper

    fun processImageProxy(imageProxy: ImageProxy) {
        val helper = gestureRecognizerHelper
        if (helper == null || !helper.isInitialized) {
            imageProxy.close()
            return
        }

        val now = System.currentTimeMillis()
        if (now - lastSubmissionMs < MIN_FRAME_INTERVAL_MS) {
            imageProxy.close()
            return
        }
        lastSubmissionMs = now

        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val bitmap = helper.imageProxyToBitmap(imageProxy)
        imageProxy.close()
        if (bitmap == null) return

        pendingFrames++
        if (!_uiState.value.isProcessing) {
            _uiState.update { it.copy(isProcessing = true) }
        }
        scheduleProcessingOff()
        helper.recognizeAsync(bitmap, rotationDegrees, now)
    }

    private fun scheduleProcessingOff() {
        viewModelScope.launch {
            delay(PROCESSING_DEBOUNCE_MS)
            if (pendingFrames == 0) {
                _uiState.update { it.copy(isProcessing = false) }
            }
        }
    }

    private fun onGestureRecognizerResult(result: GestureRecognizerResult) {
        viewModelScope.launch(Dispatchers.Main) {
            pendingFrames = (pendingFrames - 1).coerceAtLeast(0)

            val gestures = result.gestures()
            val landmarks = result.landmarks()
            val handVisible = landmarks?.isNotEmpty() == true

            var gestureName: String? = null
            if (handVisible && landmarks?.isNotEmpty() == true) {
                val lm = landmarks[0].map {
                    LandmarkGestureClassifier.Lm(it.x(), it.y(), it.z())
                }
                // Custom landmark signs take priority over the frozen model categories
                gestureName = LandmarkGestureClassifier.classify(lm)
                if (gestureName == null && !gestures.isNullOrEmpty() && gestures[0].isNotEmpty()) {
                    val top = gestures[0][0]
                    if (top.score() > CONFIDENCE_THRESHOLD) {
                        gestureName = top.categoryName()
                    }
                }
            }

            updateHandVisibility(handVisible)
            updateGesture(gestureName)
        }
    }

    private fun updateHandVisibility(handVisible: Boolean) {
        if (handVisible) {
            handAbsentFrames = 0
            handPresentFrames++
            if (handPresentFrames >= HAND_PRESENT_FRAMES_REQUIRED) {
                _uiState.update { it.copy(handDetected = true) }
            }
        } else {
            handPresentFrames = 0
            handAbsentFrames++
            if (handAbsentFrames >= HAND_ABSENT_FRAMES_REQUIRED) {
                _uiState.update { it.copy(handDetected = false) }
                stableGesture = null
                stableGestureFrames = 0
            }
        }
    }

    private fun updateGesture(gestureName: String?) {
        if (gestureName != null) {
            val displayText = GESTURE_LABELS[gestureName] ?: gestureName
            gestureGoneFrames = 0
            if (stableGesture == displayText) {
                stableGestureFrames++
            } else {
                stableGesture = displayText
                stableGestureFrames = 1
            }
            if (stableGestureFrames == GESTURE_STABLE_FRAMES_REQUIRED) {
                commitGesture(displayText)
            }
        } else {
            stableGesture = null
            stableGestureFrames = 0
            gestureGoneFrames++
            if (gestureGoneFrames >= GESTURE_GONE_FRAMES_REQUIRED) {
                _uiState.update { it.copy(currentGesture = "") }
            }
        }
    }

    private fun commitGesture(displayText: String) {
        val current = _uiState.value
        if (displayText == current.currentGesture) return

        val newRecognizedText = if (displayText.isNotBlank()) {
            if (current.recognizedText.isBlank()) displayText
            else "${current.recognizedText} $displayText"
        } else {
            current.recognizedText
        }

        _uiState.update {
            it.copy(
                currentGesture = displayText,
                handDetected = true,
                recognizedText = newRecognizedText
            )
        }

        if (displayText.isNotBlank()) {
            ttsManager.speak(displayText)
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
        private const val TAG = "SignLanguageVM"
        private const val MODEL_FILE = "gesture_recognizer.task"
        private const val CONFIDENCE_THRESHOLD = 0.7f
        private const val MIN_FRAME_INTERVAL_MS = 140L
        private const val PROCESSING_DEBOUNCE_MS = 600L
        private const val HAND_PRESENT_FRAMES_REQUIRED = 3
        private const val HAND_ABSENT_FRAMES_REQUIRED = 6
        private const val GESTURE_STABLE_FRAMES_REQUIRED = 3
        private const val GESTURE_GONE_FRAMES_REQUIRED = 6

        private val GESTURE_LABELS = mapOf(
            "Closed_Fist" to "Fist",
            "Open_Palm" to "Hello",
            "Pointing_Up" to "A",
            "Thumb_Down" to "No",
            "Thumb_Up" to "Yes",
            "Victory" to "Peace",
            "ILoveYou" to "I Love You",
            LandmarkGestureClassifier.SIGN_OK to "OK",
            LandmarkGestureClassifier.SIGN_ROCK to "Rock",
            LandmarkGestureClassifier.SIGN_L to "L"
        )
    }
}
