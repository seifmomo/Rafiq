package com.example.rafiq.domain.repository

import com.example.rafiq.domain.model.Booking
import com.example.rafiq.domain.model.BookingRecord
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    fun observeBookings(): Flow<List<BookingRecord>>

    suspend fun saveBooking(booking: Booking): BookingRecord

    suspend fun rateBooking(bookingId: String, rating: Int)

    suspend fun deleteBooking(bookingId: String)

    suspend fun averageRatingFor(assistantId: String): Double?
}