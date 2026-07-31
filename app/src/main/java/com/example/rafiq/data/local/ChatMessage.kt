package com.example.rafiq.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey
    val id: String,
    val message: String,
    val timestamp: Long,
    val sender: String
)
