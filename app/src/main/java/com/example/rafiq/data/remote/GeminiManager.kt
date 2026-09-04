package com.example.rafiq.data.remote

import com.example.rafiq.BuildConfig
import com.example.rafiq.data.local.ChatMessage
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiManager @Inject constructor() {

    private val apiKeyValid: Boolean
        get() = BuildConfig.GEMINI_API_KEY.isNotBlank() &&
                BuildConfig.GEMINI_API_KEY != "YOUR_API_KEY_HERE"

    private val isGeminiKey: Boolean
        get() = BuildConfig.GEMINI_API_KEY.startsWith("AIza")

    private val isOpenAiKey: Boolean
        get() = BuildConfig.GEMINI_API_KEY.startsWith("sk-")

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY,
            systemInstruction = content {
                text(
                    """
                    You are RAFIQ, a warm, helpful assistant built into an accessibility app for people with
                    disabilities. You support visually impaired, deaf/hard-of-hearing, and mobility-challenged users.

                    - Be concise and clear. For voice replies, keep responses under 3 sentences so they can be read aloud.
                    - If asked about nearby accessible places, medication help, SOS, or sign language, give practical guidance.
                    - If the user asks to set a medication reminder, call for help, or locate a hospital, confirm the action
                      and briefly instruct them (the app provides those tools).
                    - Never invent phone numbers or addresses. Recommend contacting local services instead.
                    """.trimIndent()
                )
            }
        )
    }

    private val chatSession by lazy { generativeModel.startChat() }

    suspend fun generateResponse(prompt: String): String = withContext(Dispatchers.IO) {
        if (!apiKeyValid) return@withContext localFallback(prompt)

        if (isOpenAiKey) {
            val openAiReply = queryOpenAiCompatible(prompt, emptyList())
            if (openAiReply != null) return@withContext openAiReply
        }

        try {
            val response = chatSession.sendMessage(prompt)
            response.text?.trim()?.ifBlank { localFallback(prompt) }
                ?: localFallback(prompt)
        } catch (_: Exception) {
            localFallback(prompt)
        }
    }

    suspend fun generateResponseWithHistory(
        prompt: String,
        history: List<ChatMessage>
    ): String = withContext(Dispatchers.IO) {
        if (!apiKeyValid) return@withContext localFallback(prompt)

        if (isOpenAiKey) {
            val openAiReply = queryOpenAiCompatible(prompt, history)
            if (openAiReply != null) return@withContext openAiReply
        }

        try {
            val session = generativeModel.startChat(
                history = history
                    .sortedBy { it.timestamp }
                    .mapNotNull { msg ->
                        val role = when (msg.sender.lowercase()) {
                            "user" -> "user"
                            "rafiq", "assistant", "model" -> "model"
                            else -> null
                        } ?: return@mapNotNull null
                        content(role) { text(msg.message) }
                    }
            )
            val response = session.sendMessage(prompt)
            response.text?.trim()?.ifBlank { localFallback(prompt) }
                ?: localFallback(prompt)
        } catch (_: Exception) {
            localFallback(prompt)
        }
    }

    suspend fun processVoiceCommand(command: String): String = withContext(Dispatchers.IO) {
        if (!apiKeyValid) return@withContext localFallback(command)

        if (isOpenAiKey) {
            val openAiReply = queryOpenAiCompatible(command, emptyList())
            if (openAiReply != null) return@withContext openAiReply
        }

        try {
            val prompt = """
                The user just said: "$command".
                If it is a command like "where is the hospital", "call for help", or "set medication", return a concise
                action confirmation. If it is a general question, answer helpfully and briefly. Keep it under 3 sentences.
            """.trimIndent()
            val response = generativeModel.generateContent(prompt)
            response.text?.trim()?.ifBlank { localFallback(command) }
                ?: localFallback(command)
        } catch (_: Exception) {
            localFallback(command)
        }
    }

    private fun queryOpenAiCompatible(prompt: String, history: List<ChatMessage>): String? {
        return try {
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
            jsonBody.put("model", "gpt-3.5-turbo")
            jsonBody.put("messages", messagesArray)
            jsonBody.put("max_tokens", 250)

            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer ${BuildConfig.GEMINI_API_KEY}")
                .addHeader("Content-Type", "application/json")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val bodyStr = response.body?.string() ?: return null
                val resObj = JSONObject(bodyStr)
                val choices = resObj.optJSONArray("choices")
                if (choices != null && choices.length() > 0) {
                    val message = choices.getJSONObject(0).optJSONObject("message")
                    return message?.optString("content")?.trim()
                }
            }
            null
        } catch (_: Exception) {
            null
        }
    }

    private fun localFallback(input: String): String = accessibilityFallbackReply(input)
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
