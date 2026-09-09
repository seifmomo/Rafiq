package com.example.rafiq.domain.model

/** Details of a requested assistant booking. */
data class BookingRequest(
    val needs: Set<DisabilityNeed>,
    val fromLocation: String,
    val toLocation: String,
    val preferredDate: String,
    val preferredTime: String,
    val budgetPerHour: Double
)

/** A confirmed booking with estimated time and cost. */
data class Booking(
    val id: String,
    val assistant: Assistant,
    val request: BookingRequest,
    val estimatedHours: Double,
    val estimatedCost: Double,
    val confirmedAt: Long = System.currentTimeMillis()
)