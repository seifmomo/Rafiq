package com.example.rafiq.data.repository

import com.example.rafiq.domain.model.Assistant
import com.example.rafiq.domain.model.BookingRequest
import com.example.rafiq.domain.model.DisabilityNeed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AssistantRepositoryImplTest {

    private val mobilityAssistant = Assistant(
        id = "a1", name = "Hassan", rating = 4.9, reviews = 132,
        specialization = "Mobility Support",
        skills = listOf("Wheelchair", "Mobility", "Physical Support"),
        distanceKm = 1.8, hourlyPrice = 150.0, verified = true
    )

    private val hearingAssistant = Assistant(
        id = "a2", name = "Nour", rating = 4.8, reviews = 41,
        specialization = "Hearing Support",
        skills = listOf("Hearing", "Telecoil", "Lip Reading"),
        distanceKm = 4.3, hourlyPrice = 160.0, verified = true
    )

    @Test
    fun estimateHours_minimumOne() {
        assertEquals(1.0, AssistantRepositoryImpl.estimateHours(0.5), 0.001)
        assertEquals(1.0, AssistantRepositoryImpl.estimateHours(1.0), 0.001)
    }

    @Test
    fun estimateHours_quarterHourRounding() {
        // 15 km → 1 hr → coerced 1 hr → 1.0
        assertEquals(1.0, AssistantRepositoryImpl.estimateHours(15.0), 0.001)
        // 30 km → 2 hr → 2.0
        assertEquals(2.0, AssistantRepositoryImpl.estimateHours(30.0), 0.001)
        // 22.5 km → 1.5 hr → 2.0 (round up to half hour then ×2 then int)
        assertEquals(1.5, AssistantRepositoryImpl.estimateHours(22.5), 0.001)
    }

    @Test
    fun estimateHours_longDistance() {
        // 60 km → 4 hr
        assertEquals(4.0, AssistantRepositoryImpl.estimateHours(60.0), 0.001)
    }

    @Test
    fun explainMatch_matchedNeeds_correct() {
        val request = BookingRequest(
            needs = setOf(DisabilityNeed.WHEELCHAIR, DisabilityNeed.HEARING),
            fromLocation = "A", toLocation = "B",
            preferredDate = "2026-01-01", preferredTime = "10:00",
            budgetPerHour = 200.0
        )
        val explanation = AssistantRepositoryImpl().explainMatch(mobilityAssistant, request)
        assertEquals(1, explanation.score)
        assertTrue(explanation.matchedNeeds.contains(DisabilityNeed.WHEELCHAIR))
        assertFalse(explanation.matchedNeeds.contains(DisabilityNeed.HEARING))
        assertTrue(explanation.matchesBudget)
    }

    @Test
    fun explainMatch_noBudgetMatch() {
        val request = BookingRequest(
            needs = setOf(DisabilityNeed.HEARING),
            fromLocation = "A", toLocation = "B",
            preferredDate = "2026-01-01", preferredTime = "10:00",
            budgetPerHour = 100.0
        )
        val explanation = AssistantRepositoryImpl().explainMatch(hearingAssistant, request)
        assertTrue(explanation.matchedNeeds.contains(DisabilityNeed.HEARING))
        assertFalse(explanation.matchesBudget)
    }

    @Test
    fun explainMatch_summary_containsWords() {
        val request = BookingRequest(
            needs = setOf(DisabilityNeed.WHEELCHAIR),
            fromLocation = "A", toLocation = "B",
            preferredDate = "2026-01-01", preferredTime = "10:00",
            budgetPerHour = 200.0
        )
        val explanation = AssistantRepositoryImpl().explainMatch(mobilityAssistant, request)
        assertTrue(explanation.summary.contains("supports"))
        assertTrue(explanation.summary.contains("within your"))
    }
}