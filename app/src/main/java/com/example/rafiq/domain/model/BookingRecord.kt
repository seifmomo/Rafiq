package com.example.rafiq.domain.model

/** A confirmed booking the user can review later, rate, and view as an invoice. */
data class BookingRecord(
    val id: String,
    val assistantId: String,
    val assistantName: String,
    val assistantSpecialization: String,
    val assistantHourlyPrice: Double,
    val assistantVerified: Boolean,
    val needs: List<DisabilityNeed>,
    val fromLocation: String,
    val toLocation: String,
    val preferredDate: String,
    val preferredTime: String,
    val budgetPerHour: Double,
    val estimatedHours: Double,
    val estimatedCost: Double,
    val isRecurring: Boolean = false,
    val rating: Int? = null,
    val confirmedAt: Long = System.currentTimeMillis()
) {
    val hourlyTotal: Double
        get() = estimatedHours * assistantHourlyPrice
}

/** Build a persistable record from a confirmed live booking. */
fun Booking.toRecord(): BookingRecord = BookingRecord(
    id = id,
    assistantId = assistant.id,
    assistantName = assistant.name,
    assistantSpecialization = assistant.specialization,
    assistantHourlyPrice = assistant.hourlyPrice,
    assistantVerified = assistant.verified,
    needs = request.needs.toList(),
    fromLocation = request.fromLocation,
    toLocation = request.toLocation,
    preferredDate = request.preferredDate,
    preferredTime = request.preferredTime,
    budgetPerHour = request.budgetPerHour,
    estimatedHours = estimatedHours,
    estimatedCost = estimatedCost,
    isRecurring = request.isRecurring,
    confirmedAt = confirmedAt
)