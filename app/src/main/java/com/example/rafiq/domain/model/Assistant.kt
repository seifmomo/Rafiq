package com.example.rafiq.domain.model

/** A trained support assistant available for booking. */
data class Assistant(
    val id: String,
    val name: String,
    val rating: Double,
    val reviews: Int,
    val specialization: String,
    val skills: List<String>,
    val distanceKm: Double,
    val hourlyPrice: Double,
    val verified: Boolean
) {
    fun supports(need: DisabilityNeed): Boolean = skills.any { skill ->
        when (need) {
            DisabilityNeed.WHEELCHAIR -> skill.contains("Wheelchair") || skill.contains("Mobility")
            DisabilityNeed.HEARING -> skill.contains("Hearing")
            DisabilityNeed.SIGN_LANGUAGE -> skill.contains("Sign Language")
            DisabilityNeed.VISUAL -> skill.contains("Visual") || skill.contains("Vision")
            DisabilityNeed.ELDERLY -> skill.contains("Elderly") || skill.contains("Senior")
            DisabilityNeed.MULTIPLE -> true
        }
    }
}