package com.example.rafiq.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** A persisted confirmed booking with optional post-booking rating. */
@Entity(tableName = "booking_records")
data class BookingRecordEntity(
    @PrimaryKey
    val id: String,
    val assistantId: String,
    val assistantName: String,
    val assistantSpecialization: String,
    val assistantHourlyPrice: Double,
    val assistantVerified: Boolean,
    val needs: String,
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
)