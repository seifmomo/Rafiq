package com.example.rafiq.domain.model

/** Disability / assistance needs a user can select when booking an assistant. */
enum class DisabilityNeed(val label: String) {
    WHEELCHAIR("Wheelchair Support"),
    HEARING("Hearing Support"),
    SIGN_LANGUAGE("Sign Language Support"),
    VISUAL("Visual Assistance"),
    ELDERLY("Elderly Assistance"),
    MULTIPLE("Multiple Assistance")
}