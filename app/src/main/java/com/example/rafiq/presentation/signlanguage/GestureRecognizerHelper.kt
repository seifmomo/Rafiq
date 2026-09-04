package com.example.rafiq.presentation.signlanguage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import java.io.ByteArrayOutputStream

class GestureRecognizerHelper(
    private val context: Context,
    private val onResult: (GestureRecognizerResult) -> Unit,
    private val onError: (String) -> Unit
) {
    private var gestureRecognizer: GestureRecognizer? = null
    val isInitialized: Boolean get() = gestureRecognizer != null

    init {
        setupRecognizer()
    }

    private fun setupRecognizer() {
        try {
            val assetExists = try {
                context.assets.open(MODEL_FILE).use { true }
            } catch (_: Exception) {
                false
            }

            if (!assetExists) {
                onError("Gesture model file '$MODEL_FILE' not found in assets. Recognition is disabled.")
                return
            }

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
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to create gesture recognizer", e)
            gestureRecognizer = null
            onError("Could not start gesture recognition: ${e.message}")
        }
    }

    @OptIn(ExperimentalGetImage::class)
    fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        val image = imageProxy.image ?: return null
        return try {
            val nv21 = yuv420888ToNv21(image)
            val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 80, out)
            val bytes = out.toByteArray()
            android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to convert ImageProxy to Bitmap", e)
            null
        }
    }

    private fun yuv420888ToNv21(image: android.media.Image): ByteArray {
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        return nv21
    }

    fun recognizeAsync(bitmap: Bitmap, rotationDegrees: Int, timestampMs: Long) {
        val recognizer = gestureRecognizer ?: return

        val rotatedBitmap = if (rotationDegrees != 0) {
            val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            bitmap
        }

        try {
            val mpImage = BitmapImageBuilder(rotatedBitmap).build()
            recognizer.recognizeAsync(mpImage, timestampMs)
        } catch (e: Throwable) {
            Log.e(TAG, "Recognition error", e)
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
