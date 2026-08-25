package com.example.rafiq.presentation.signlanguage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult

class GestureRecognizerHelper(
    private val context: Context,
    private val onResult: (GestureRecognizerResult) -> Unit,
    private val onError: (String) -> Unit
) {
    private var gestureRecognizer: GestureRecognizer? = null

    init {
        setupRecognizer()
    }

    private fun setupRecognizer() {
        try {
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath(MODEL_FILE)
                .build()

            val options = GestureRecognizer.GestureRecognizerOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setNumHands(1)
                .setResultListener { result, _ -> onResult(result) }
                .setErrorListener { error -> onError(error.message ?: "Unknown error") }
                .build()

            gestureRecognizer = GestureRecognizer.createFromOptions(context, options)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create gesture recognizer", e)
            onError("Failed to initialize gesture recognizer: ${e.message}")
        }
    }

    fun recognizeAsync(bitmap: Bitmap, timestampMs: Long) {
        val recognizer = gestureRecognizer ?: return

        val matrix = Matrix().apply { postRotate(90f) }
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        try {
            val mpImage = BitmapImageBuilder(rotatedBitmap).build()
            recognizer.recognizeAsync(mpImage, timestampMs)
        } catch (e: Exception) {
            Log.e(TAG, "Recognition error", e)
            onError("Recognition error: ${e.message}")
        }
    }

    fun close() {
        gestureRecognizer?.close()
        gestureRecognizer = null
    }

    companion object {
        private const val TAG = "GestureRecognizerHelper"
        private const val MODEL_FILE = "gesture_recognizer.task"
    }
}
