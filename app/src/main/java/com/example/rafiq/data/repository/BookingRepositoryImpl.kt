package com.example.rafiq.data.repository

import com.example.rafiq.data.local.BookingRecordDao
import com.example.rafiq.data.local.BookingRecordEntity
import com.example.rafiq.domain.model.Booking
import com.example.rafiq.domain.model.BookingRecord
import com.example.rafiq.domain.model.DisabilityNeed
import com.example.rafiq.domain.model.toRecord
import com.example.rafiq.domain.repository.BookingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val bookingRecordDao: BookingRecordDao
) : BookingRepository {

    override fun observeBookings(): Flow<List<BookingRecord>> =
        bookingRecordDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun saveBooking(booking: Booking): BookingRecord {
        val record = booking.toRecord()
        bookingRecordDao.insert(record.toEntity())
        return record
    }

    override suspend fun rateBooking(bookingId: String, rating: Int) {
        val safe = rating.coerceIn(1, 5)
        bookingRecordDao.updateRating(bookingId, safe)
    }

    override suspend fun deleteBooking(bookingId: String) {
        bookingRecordDao.deleteById(bookingId)
    }

    override suspend fun averageRatingFor(assistantId: String): Double? =
        bookingRecordDao.averageRatingFor(assistantId)

    private fun BookingRecordEntity.toDomain(): BookingRecord = BookingRecord(
        id = id,
        assistantId = assistantId,
        assistantName = assistantName,
        assistantSpecialization = assistantSpecialization,
        assistantHourlyPrice = assistantHourlyPrice,
        assistantVerified = assistantVerified,
        needs = needs.split(",")
            .filter { it.isNotBlank() }
            .mapNotNull { label -> DisabilityNeed.entries.firstOrNull { it.name == label } },
        fromLocation = fromLocation,
        toLocation = toLocation,
        preferredDate = preferredDate,
        preferredTime = preferredTime,
        budgetPerHour = budgetPerHour,
        estimatedHours = estimatedHours,
        estimatedCost = estimatedCost,
        isRecurring = isRecurring,
        rating = rating,
        confirmedAt = confirmedAt
    )

    private fun BookingRecord.toEntity(): BookingRecordEntity = BookingRecordEntity(
        id = id,
        assistantId = assistantId,
        assistantName = assistantName,
        assistantSpecialization = assistantSpecialization,
        assistantHourlyPrice = assistantHourlyPrice,
        assistantVerified = assistantVerified,
        needs = needs.joinToString(",") { it.name },
        fromLocation = fromLocation,
        toLocation = toLocation,
        preferredDate = preferredDate,
        preferredTime = preferredTime,
        budgetPerHour = budgetPerHour,
        estimatedHours = estimatedHours,
        estimatedCost = estimatedCost,
        isRecurring = isRecurring,
        rating = rating,
        confirmedAt = confirmedAt
    )
}