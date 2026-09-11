package com.example.rafiq.domain.repository

import com.example.rafiq.domain.model.Assistant
import com.example.rafiq.domain.model.BookingRequest
import com.example.rafiq.domain.model.MatchExplanation

interface AssistantRepository {
    suspend fun findAssistants(request: BookingRequest): List<Assistant>

    /** Readable reasons an assistant was recommended for the given request. */
    fun explainMatch(assistant: Assistant, request: BookingRequest): MatchExplanation
}