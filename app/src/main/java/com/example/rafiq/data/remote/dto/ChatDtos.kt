package com.example.rafiq.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatMessageDto(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String?,
    @SerializedName("message") val message: String,
    @SerializedName("sender") val sender: String,
    @SerializedName("created_at") val createdAt: String?
)

data class MessagesResponse(
    @SerializedName("messages") val messages: List<ChatMessageDto>
)

data class MessageResponse(
    @SerializedName("message") val message: ChatMessageDto
)

data class CreateMessageRequest(
    @SerializedName("id") val id: String,
    @SerializedName("message") val message: String,
    @SerializedName("sender") val sender: String,
    @SerializedName("timestamp") val timestamp: Long
)

data class SyncMessagesRequest(
    @SerializedName("messages") val messages: List<CreateMessageRequest>
)

data class SyncMessagesResponse(
    @SerializedName("synced") val synced: Int,
    @SerializedName("pointsAwarded") val pointsAwarded: Int
)
