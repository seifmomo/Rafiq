package com.example.rafiq.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rafiq.data.local.ChatMessage
import com.example.rafiq.data.local.ChatMessageDao
import com.example.rafiq.data.local.UserPreferences
import com.example.rafiq.data.remote.api.ChatApi
import com.example.rafiq.data.remote.dto.CreateMessageRequest
import com.example.rafiq.data.remote.AiManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatMessageDao: ChatMessageDao,
    private val aiManager: AiManager,
    private val userPreferences: UserPreferences,
    private val chatApi: ChatApi
) : ViewModel() {

    val messages: StateFlow<List<ChatMessage>> = chatMessageDao.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    init {
        syncFromCloud()
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            val msgId = UUID.randomUUID().toString()
            val timestamp = System.currentTimeMillis()

            val history = chatMessageDao.getAllMessagesOnce()

            val userMessage = ChatMessage(
                id = msgId,
                message = text,
                timestamp = timestamp,
                sender = "user"
            )
            chatMessageDao.insertMessage(userMessage)

            // Cloud sync must never block the AI reply (LAN backend may be unreachable)
            syncToCloud(msgId, text, "user", timestamp)

            userPreferences.addPoints(10)

            _isTyping.value = true
            val aiResponse = try {
                aiManager.generateResponseWithHistory(text, history)
            } finally {
                _isTyping.value = false
            }

            val replyId = UUID.randomUUID().toString()
            val replyTimestamp = System.currentTimeMillis()

            val reply = ChatMessage(
                id = replyId,
                message = aiResponse,
                timestamp = replyTimestamp,
                sender = "rafiq"
            )
            chatMessageDao.insertMessage(reply)

            syncToCloud(replyId, aiResponse, "rafiq", replyTimestamp)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            val ids = chatMessageDao.getAllMessagesOnce().map { it.id }
            // Clear locally first and immediately — never wait on the cloud
            chatMessageDao.clearAll()
            ids.forEach { id ->
                launch { runCatching { chatApi.deleteMessage(id) } }
            }
        }
    }

    private fun syncToCloud(id: String, message: String, sender: String, timestamp: Long) {
        viewModelScope.launch {
            runCatching {
                chatApi.sendMessage(CreateMessageRequest(id, message, sender, timestamp))
            }
        }
    }

    private fun syncFromCloud() {
        viewModelScope.launch {
            try {
                val response = chatApi.getMessages(100)
                if (response.isSuccessful) {
                    val cloudMessages = response.body()?.messages ?: return@launch
                    for (cloudMsg in cloudMessages) {
                        val existing = chatMessageDao.getMessageById(cloudMsg.id)
                        if (existing == null) {
                            chatMessageDao.insertMessage(
                                ChatMessage(
                                    id = cloudMsg.id,
                                    message = cloudMsg.message,
                                    timestamp = cloudMsg.createdAt?.let {
                                        try {
                                            java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US).parse(it)?.time
                                                ?: System.currentTimeMillis()
                                        } catch (_: Exception) {
                                            System.currentTimeMillis()
                                        }
                                    } ?: System.currentTimeMillis(),
                                    sender = cloudMsg.sender
                                )
                            )
                        }
                    }
                }
            } catch (_: Exception) {}
        }
    }
}
