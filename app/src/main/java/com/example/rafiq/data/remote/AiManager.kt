package com.example.rafiq.data.remote

import android.util.Log
import com.example.rafiq.BuildConfig
import com.example.rafiq.data.local.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiManager @Inject constructor() {

    private val openAiKey: String
        get() {
            val explicit = BuildConfig.OPENAI_API_KEY
            if (explicit.isNotBlank() && explicit != "YOUR_API_KEY_HERE") return explicit
            val legacyKey = BuildConfig.GEMINI_API_KEY
            return if (legacyKey.isNotBlank() && legacyKey.startsWith("sk-")) legacyKey else ""
        }

    private val apiKeyValid: Boolean
        get() = openAiKey.isNotBlank()

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(25, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    suspend fun generateResponse(prompt: String): String = withContext(Dispatchers.IO) {
        if (!apiKeyValid) return@withContext localFallback(prompt)

        val reply = queryOpenAiCompatible(prompt, emptyList())
        reply ?: localFallback(prompt)
    }

    suspend fun generateResponseWithHistory(
        prompt: String,
        history: List<ChatMessage>
    ): String = withContext(Dispatchers.IO) {
        if (!apiKeyValid) return@withContext localFallback(prompt)

        val reply = queryOpenAiCompatible(prompt, history)
        reply ?: localFallback(prompt)
    }

    suspend fun processVoiceCommand(command: String): String = withContext(Dispatchers.IO) {
        if (!apiKeyValid) return@withContext localFallback(command)

        val prompt = """
            The user just said: "$command".
            If it is a command like "where is the hospital", "call for help", or "set medication", return a concise
            action confirmation. If it is a general question, answer helpfully and briefly. Keep it under 3 sentences.
        """.trimIndent()

        val reply = queryOpenAiCompatible(prompt, emptyList())
        reply ?: localFallback(command)
    }

    private suspend fun queryOpenAiCompatible(prompt: String, history: List<ChatMessage>): String? {
        val key = openAiKey
        if (key.isBlank()) return null

        var attempt = 0
        while (attempt < MAX_RETRIES) {
            attempt++
            try {
                val messagesArray = JSONArray()

                val systemMsg = JSONObject()
                systemMsg.put("role", "system")
                systemMsg.put("content", "You are RAFIQ, a warm AI accessibility assistant for people with disabilities. Keep answers clear, empathetic, and concise.")
                messagesArray.put(systemMsg)

                history.sortedBy { it.timestamp }.takeLast(10).forEach { msg ->
                    val role = if (msg.sender.lowercase() == "user") "user" else "assistant"
                    val jsonMsg = JSONObject()
                    jsonMsg.put("role", role)
                    jsonMsg.put("content", msg.message)
                    messagesArray.put(jsonMsg)
                }

                val userMsg = JSONObject()
                userMsg.put("role", "user")
                userMsg.put("content", prompt)
                messagesArray.put(userMsg)

                val jsonBody = JSONObject()
                jsonBody.put("model", BuildConfig.OPENAI_MODEL)
                jsonBody.put("messages", messagesArray)
                jsonBody.put("max_tokens", 250)

                val request = Request.Builder()
                    .url(BuildConfig.OPENAI_BASE_URL)
                    .addHeader("Authorization", "Bearer $key")
                    .addHeader("Content-Type", "application/json")
                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = okHttpClient.newCall(request).execute()
                try {
                    if (response.isSuccessful) {
                        val bodyStr = response.body?.string() ?: return null
                        val resObj = JSONObject(bodyStr)
                        val choices = resObj.optJSONArray("choices")
                        if (choices != null && choices.length() > 0) {
                            val message = choices.getJSONObject(0).optJSONObject("message")
                            val content = message?.optString("content")?.trim()
                            return if (content.isNullOrBlank()) null else content
                        }
                        return null
                    }

                    val code = response.code
                    when {
                        code == 401 || code == 403 -> {
                            Log.e(TAG, "OpenAI-compatible auth rejected ($code): check the API key in gradle-secrets.properties")
                            return null
                        }
                        code == 429 -> {
                            Log.w(TAG, "OpenAI-compatible rate limited ($code)")
                            return null
                        }
                        code >= 500 -> {
                            Log.w(TAG, "OpenAI-compatible server error ($code), retry $attempt/$MAX_RETRIES")
                        }
                        else -> {
                            Log.e(TAG, "OpenAI-compatible request failed ($code): ${response.body?.string()}")
                            return null
                        }
                    }
                } finally {
                    response.close()
                }
            } catch (e: IOException) {
                Log.w(TAG, "OpenAI-compatible network error (${e.message}), retry $attempt/$MAX_RETRIES")
            } catch (e: Exception) {
                Log.e(TAG, "OpenAI-compatible request error: ${e.message}")
                return null
            }
            delay(RETRY_DELAY_MS)
        }
        return null
    }

    private fun localFallback(input: String): String = accessibilityFallbackReply(input)

    companion object {
        private const val TAG = "AiManager"
        private const val MAX_RETRIES = 2
        private const val RETRY_DELAY_MS = 400L
    }
}

internal fun accessibilityFallbackReply(input: String): String {
    val text = input.lowercase().trim()
    return when {
            text.contains("sos") || (text.contains("help") && (text.contains("call") || text.contains("emergency") || text.contains("danger"))) ->
                "In an emergency, tap the red SOS button on the home screen. It immediately alerts your emergency contact and shares your live location."

            text.contains("hospital") || text.contains("clinic") || text.contains("doctor") || text.contains("pharmacy") || text.contains("health center") ->
                "You can view accessible hospitals and clinics near you on the Map screen. Tap on any hospital marker for direct navigation."

            text.contains("medic") || text.contains("pill") || text.contains("dose") || text.contains("remind") ->
                "Manage your daily medications in the Health tab. You can set pill names, dosages, and custom alarm times so you never miss a dose."

            text.contains("sign") || text.contains("gesture") || text.contains("deaf") || text.contains("asl") || text.contains("hand") ->
                "Open the Sign Language tab and point your camera at your hand! RAFIQ detects gestures like Thumbs Up, Victory, Fist, and Open Palm in real time."

            text.contains("eye") || text.contains("see") || text.contains("read") || text.contains("blind") || text.contains("vision") || text.contains("look") ->
                "Use the Be My Eyes feature to point your camera at objects, signs, or text. RAFIQ will read and describe what's in front of you."

            text.contains("glass") || text.contains("bluetooth") || text.contains("hardware") || text.contains("device") || text.contains("headset") ->
                "Smart glasses and accessibility headsets can read text and signs aloud when connected to your device. Check your device's Bluetooth settings to pair compatible hardware with the app."

            text.contains("who are you") || text.contains("what can you do") || text.contains("your name") || text.contains("rafiq") ->
                "I am RAFIQ, your dedicated AI accessibility assistant! I help with sign language detection, medication reminders, accessible location mapping, and emergency SOS."

            text.contains("hello") || text.contains("hi") || text == "hey" || text.startsWith("good morning") || text.startsWith("good evening") ->
                "Hello! Welcome to RAFIQ. How can I help you today?"

            text.contains("thank") ->
                "You're very welcome! I'm always here to assist you."

            else ->
                "I am RAFIQ, your accessibility assistant. I can help with medication reminders, finding accessible places, emergency SOS, and sign language recognition. How can I support you right now?"
        }
    }