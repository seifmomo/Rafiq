package com.example.rafiq.data.remote

import com.example.rafiq.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiManager @Inject constructor() {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun generateResponse(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            if (BuildConfig.GEMINI_API_KEY == "YOUR_API_KEY_HERE") {
                return@withContext "API Key not configured. Please add your Gemini API Key to gradle.properties."
            }
            val response = generativeModel.generateContent(prompt)
            response.text ?: "I'm sorry, I couldn't process that."
        } catch (e: Exception) {
            "Error: ${e.localizedMessage ?: "Unknown AI error"}"
        }
    }

    suspend fun processVoiceCommand(command: String): String = withContext(Dispatchers.IO) {
        val systemPrompt = """
            You are RAFIQ, a helpful assistant for people with disabilities. 
            The user just said: "$command".
            If it's a command like "where is the hospital", "call help", or "set medication", respond with a concise action confirmation.
            If it's a general question, answer it helpfully and briefly.
            Keep your response short so it can be read aloud clearly.
        """.trimIndent()
        
        generateResponse(systemPrompt)
    }
}
