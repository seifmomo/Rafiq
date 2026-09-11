package com.example.rafiq.domain.model

/** Readable explanation of why an assistant matched a booking request. */
data class MatchExplanation(
    val assistant: Assistant,
    val matchedNeeds: List<DisabilityNeed>,
    val matchesBudget: Boolean,
    val score: Int
) {
    /** Short one-line reason shown on the results card. */
    val summary: String
        get() {
            val parts = mutableListOf<String>()
            if (matchedNeeds.isNotEmpty()) {
                val labels = matchedNeeds.take(2).joinToString(", ") { it.label.lowercase() }
                parts.add("supports $labels")
            }
            if (matchesBudget) parts.add("within your EGP ${assistant.hourlyPrice.toInt()}/hr budget")
            return if (parts.isEmpty()) "General match" else parts.joinToString(" · ")
        }
}