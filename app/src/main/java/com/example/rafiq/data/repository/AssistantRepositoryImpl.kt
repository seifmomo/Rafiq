package com.example.rafiq.data.repository

import com.example.rafiq.domain.model.Assistant
import com.example.rafiq.domain.model.BookingRequest
import com.example.rafiq.domain.model.DisabilityNeed
import com.example.rafiq.domain.model.MatchExplanation
import com.example.rafiq.domain.repository.AssistantRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.math.roundToInt

class AssistantRepositoryImpl @Inject constructor() : AssistantRepository {

    private val pool: List<Assistant> = listOf(
        Assistant(
            id = "a1",
            name = "Hassan Abdo",
            rating = 4.9,
            reviews = 132,
            specialization = "Mobility Support",
            skills = listOf("Wheelchair", "Mobility", "Physical Support"),
            distanceKm = 1.8,
            hourlyPrice = 150.0,
            verified = true
        ),
        Assistant(
            id = "a2",
            name = "Mona El-Sayed",
            rating = 4.8,
            reviews = 98,
            specialization = "Sign Language Interpreter",
            skills = listOf("Sign Language", "Deaf Culture", "Hearing"),
            distanceKm = 3.4,
            hourlyPrice = 180.0,
            verified = true
        ),
        Assistant(
            id = "a3",
            name = "Omar Fathy",
            rating = 4.7,
            reviews = 214,
            specialization = "Vision Guide",
            skills = listOf("Visual", "Vision", "Reading Assistance"),
            distanceKm = 5.1,
            hourlyPrice = 140.0,
            verified = true
        ),
        Assistant(
            id = "a4",
            name = "Salma Hassan",
            rating = 4.9,
            reviews = 76,
            specialization = "Elderly Care",
            skills = listOf("Elderly", "Senior", "Medical Companion"),
            distanceKm = 2.6,
            hourlyPrice = 200.0,
            verified = true
        ),
        Assistant(
            id = "a5",
            name = "Ahmed Salah",
            rating = 4.6,
            reviews = 54,
            specialization = "All-Round Assistant",
            skills = listOf("Wheelchair", "Hearing", "Visual", "Elderly", "Sign Language"),
            distanceKm = 7.9,
            hourlyPrice = 220.0,
            verified = false
        ),
        Assistant(
            id = "a6",
            name = "Nour Adel",
            rating = 4.8,
            reviews = 41,
            specialization = "Hearing Support",
            skills = listOf("Hearing", "Telecoil", "Lip Reading"),
            distanceKm = 4.3,
            hourlyPrice = 160.0,
            verified = true
        ),
        Assistant(
            id = "a7",
            name = "Karim Mostafa",
            rating = 4.7,
            reviews = 67,
            specialization = "Mobility Companion",
            skills = listOf("Wheelchair", "Mobility", "Driving Support"),
            distanceKm = 3.0,
            hourlyPrice = 190.0,
            verified = true
        ),
        Assistant(
            id = "a8",
            name = "Dina Ashraf",
            rating = 4.9,
            reviews = 120,
            specialization = "Companion & Care",
            skills = listOf("Elderly", "Visual", "Companion"),
            distanceKm = 6.5,
            hourlyPrice = 170.0,
            verified = true
        )
    )

    override suspend fun findAssistants(request: BookingRequest): List<Assistant> {
        delay(700)

        val needs = request.needs.ifEmpty { setOf(DisabilityNeed.MULTIPLE) }

        val matching = pool
            .map { assistant ->
                val score = needs.count { assistant.supports(it) }
                assistant to score
            }
            .filter { (_, score) -> score > 0 }
            .sortedWith(
                compareByDescending<Pair<Assistant, Int>> { it.second }
                    .thenByDescending { it.first.rating }
                    .thenBy { it.first.distanceKm }
            )
            .map { it.first }

        val withinBudget = matching.filter { it.hourlyPrice <= request.budgetPerHour }
        return if (withinBudget.isNotEmpty()) withinBudget else matching
            // relaxation: if no assistant fits the budget, still surface the best matches
    }

    override fun explainMatch(assistant: Assistant, request: BookingRequest): MatchExplanation {
        val needs = request.needs.ifEmpty { setOf(DisabilityNeed.MULTIPLE) }
        val matched = needs.filter { assistant.supports(it) }
        val withinBudget = assistant.hourlyPrice <= request.budgetPerHour
        return MatchExplanation(
            assistant = assistant,
            matchedNeeds = matched,
            matchesBudget = withinBudget,
            score = matched.size
        )
    }

    companion object {
        /** Rough estimate: 15 km covered per hour of assistance, minimum 1 hour. */
        fun estimateHours(distanceKm: Double): Double {
            val tripHours = distanceKm / 15.0
            return (tripHours.coerceAtLeast(1.0) * 2).roundToInt() / 2.0
        }
    }
}