package com.example.rafiq.data.remote

import com.example.rafiq.BuildConfig
import com.example.rafiq.data.local.ChatMessage
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiManager @Inject constructor() {

    private val apiKeyValid: Boolean
        get() = BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "YOUR_API_KEY_HERE"

    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash",
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
        try {
            val response = chatSession.sendMessage(prompt)
            response.text?.trim()?.ifBlank { "I'm sorry, I couldn't process that." }
                ?: "I'm sorry, I couldn't process that."
        } catch (e: Exception) {
            "Error: ${e.localizedMessage ?: "Unknown AI error"}"
        }
    }

    suspend fun generateResponseWithHistory(
        prompt: String,
        history: List<ChatMessage>
    ): String = withContext(Dispatchers.IO) {
        if (!apiKeyValid) return@withContext localFallback(prompt)
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
            response.text?.trim()?.ifBlank { "I'm sorry, I couldn't process that." }
                ?: "I'm sorry, I couldn't process that."
        } catch (e: Exception) {
            "Error: ${e.localizedMessage ?: "Unknown AI error"}"
        }
    }

    suspend fun processVoiceCommand(command: String): String = withContext(Dispatchers.IO) {
        if (!apiKeyValid) return@withContext localFallback(command)
        try {
            val prompt = """
                The user just said: "$command".
                If it is a command like "where is the hospital", "call for help", or "set medication", return a concise
                action confirmation. If it is a general question, answer helpfully and briefly. Keep it under 3 sentences.
            """.trimIndent()
            val response = generativeModel.generateContent(prompt)
            response.text?.trim()?.ifBlank { "I heard you, but I'm not sure how to help with that yet." }
                ?: "I heard you, but I'm not sure how to help with that yet."
        } catch (e: Exception) {
            "Error: ${e.localizedMessage ?: "Unknown AI error"}"
        }
    }

    private fun localFallback(input: String): String {
        val text = input.lowercase()
        return when {
            text.contains("sos") || text.contains("help") && (text.contains("call") || text.contains("emergency")) ->
                "In an emergency, use the SOS button in the app, which calls your emergency contact and shares your location."
            text.contains("hospital") || text.contains("clinic") || text.contains("doctor") ->
                "Use the Map tab to find accessible hospitals and clinics near you. I can help you get directions there."
            text.contains("medic") || text.contains("pill") || text.contains("dose") ->
                "Open the Health tab to manage medications and alarms. Tell me the medicine name and time and I'll confirm it."
            text.contains("sign") || text.contains("gesture") ->
                "Open the Sign Language tab and show a gesture to the camera. I understand common gestures like thumbs up and the victory sign."
            text.contains("glasses") || text.contains("glass") ->
                "Connected smart glasses help read signs and surroundings aloud. Make sure they are paired in Settings."
            text.contains("who are you") ->
                "I'm RAFIQ, your accessibility assistant. Ask me about medications, places, or safety, or use the voice command."
            text.contains("thank") ->
                "You're very welcome! I'm here whenever you need me."
            text.contains("hello") || text.contains("hi ") || text.trim() == "hi" || text == "hey" ->
                "Hello! How can I help you today?"
            else ->
                "I'm ready to help, but my AI service isn't configured yet. Set a Gemini API key in gradle.properties to enable full responses. Meanwhile, try asking about medications, hospitals, or SOS."
        }
    }
}
