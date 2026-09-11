package com.example.rafiq.data.repository

import com.example.rafiq.data.local.BookingRecordEntity
import com.example.rafiq.domain.model.Assistant
import com.example.rafiq.domain.model.Booking
import com.example.rafiq.domain.model.BookingRecord
import com.example.rafiq.domain.model.BookingRequest
import com.example.rafiq.domain.model.DisabilityNeed
import com.example.rafiq.domain.model.toRecord
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BookingRecordMapperTest {

    private val sampleAssistant = Assistant(
        id = "a1", name = "Hassan", rating = 4.9, reviews = 132,
        specialization = "Mobility Support",
        skills = listOf("Wheelchair", "Mobility"),
        distanceKm = 1.8, hourlyPrice = 150.0, verified = true
    )

    private fun sampleBooking(
        isRecurring: Boolean = false,
        rating: Int? = null
    ) = BookingRecord(
        id = "RAFIQ-20260101-A1",
        assistantId = "a1",
        assistantName = "Hassan",
        assistantSpecialization = "Mobility Support",
        assistantHourlyPrice = 150.0,
        assistantVerified = true,
        needs = listOf(DisabilityNeed.WHEELCHAIR, DisabilityNeed.VISUAL),
        fromLocation = "Nasr City",
        toLocation = "Tahrir Square",
        preferredDate = "2026-01-01",
        preferredTime = "10:00",
        budgetPerHour = 200.0,
        estimatedHours = 2.0,
        estimatedCost = 300.0,
        isRecurring = isRecurring,
        rating = rating,
        confirmedAt = 1700000000000
    )

    @Test
    fun bookingToRecord_preservesFields() {
        val booking = Booking(
            id = "RAFIQ-20260101-A1",
            assistant = sampleAssistant,
            request = BookingRequest(
                needs = setOf(DisabilityNeed.WHEELCHAIR, DisabilityNeed.VISUAL),
                fromLocation = "Nasr City",
                toLocation = "Tahrir Square",
                preferredDate = "2026-01-01",
                preferredTime = "10:00",
                budgetPerHour = 200.0,
                isRecurring = true
            ),
            estimatedHours = 2.0,
            estimatedCost = 300.0
        )
        val record = booking.toRecord()
        assertEquals(booking.id, record.id)
        assertEquals("Nasr City", record.fromLocation)
        assertEquals("Tahrir Square", record.toLocation)
        assertTrue(record.isRecurring)
        assertEquals(2, record.needs.size)
    }

    @Test
    fun hourlyTotal_isHoursTimesPrice() {
        val record = sampleBooking()
        assertEquals(2.0 * 150.0, record.hourlyTotal, 0.001)
    }

    @Test
    fun defaultFields_areCorrect() {
        val record = sampleBooking()
        assertFalse(record.isRecurring)
        assertNull(record.rating)
    }

    @Test
    fun ratingClamped_toValidRange() = runBlocking {
        val dao = TestBookingRecordDao()
        val impl = TestBookingRepositoryImpl(dao)
        dao.insert(sampleBooking().toEntity())
        impl.rateBooking("RAFIQ-20260101-A1", -3)
        assertEquals(1, dao.currentRating) // impl clamps rating to 1..5
    }

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

private class TestBookingRecordDao : com.example.rafiq.data.local.BookingRecordDao {
    var currentRating: Int? = null
    private val store = mutableMapOf<String, BookingRecordEntity>()
    override suspend fun insert(record: BookingRecordEntity) { store[record.id] = record }
    override fun observeAll() = kotlinx.coroutines.flow.flowOf(store.values.toList())
    override suspend fun getById(id: String) = store[id]
    override suspend fun updateRating(id: String, rating: Int) {
        currentRating = rating
        store[id]?.let { store[id] = it.copy(rating = rating) }
    }
    override suspend fun deleteById(id: String) { store.remove(id) }
    override suspend fun averageRatingFor(assistantId: String) = null
}

private class TestBookingRepositoryImpl(
    private val dao: com.example.rafiq.data.local.BookingRecordDao
) : com.example.rafiq.domain.repository.BookingRepository {
    override fun observeBookings() = kotlinx.coroutines.flow.flowOf(emptyList<com.example.rafiq.domain.model.BookingRecord>())
    override suspend fun saveBooking(booking: com.example.rafiq.domain.model.Booking) =
        com.example.rafiq.domain.model.BookingRecord(
            id = booking.id,
            assistantId = booking.assistant.id,
            assistantName = booking.assistant.name,
            assistantSpecialization = booking.assistant.specialization,
            assistantHourlyPrice = booking.assistant.hourlyPrice,
            assistantVerified = booking.assistant.verified,
            needs = booking.request.needs.toList(),
            fromLocation = booking.request.fromLocation,
            toLocation = booking.request.toLocation,
            preferredDate = booking.request.preferredDate,
            preferredTime = booking.request.preferredTime,
            budgetPerHour = booking.request.budgetPerHour,
            estimatedHours = booking.estimatedHours,
            estimatedCost = booking.estimatedCost,
            isRecurring = booking.request.isRecurring
        )
    override suspend fun rateBooking(bookingId: String, rating: Int) = dao.updateRating(bookingId, rating.coerceIn(1, 5))
    override suspend fun deleteBooking(bookingId: String) = dao.deleteById(bookingId)
    override suspend fun averageRatingFor(assistantId: String) = null
}