package com.example.rafiq.domain.repository

import com.example.rafiq.domain.model.Assistant
import com.example.rafiq.domain.model.BookingRequest

interface AssistantRepository {
    suspend fun findAssistants(request: BookingRequest): List<Assistant>
}