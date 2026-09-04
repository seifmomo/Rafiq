package com.example.rafiq.data.remote

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AccessibilityFallbackReplyTest {

    @Test
    fun sosEmergencyIntent_returnsSosGuidance() {
        val reply = accessibilityFallbackReply("I need help, call someone now")
        assertTrue(reply.contains("SOS"))
    }

    @Test
    fun hospitalIntent_returnsMapGuidance() {
        val reply = accessibilityFallbackReply("where is the nearest hospital?")
        assertTrue(reply.contains("Map") || reply.contains("hospital"))
    }

    @Test
    fun medicationIntent_returnsHealthGuidance() {
        val reply = accessibilityFallbackReply("set a pill reminder please")
        assertTrue(reply.contains("Health") || reply.contains("medication"))
    }

    @Test
    fun signLanguageIntent_returnsSignGuidance() {
        val reply = accessibilityFallbackReply("how does sign language work here?")
        assertTrue(reply.contains("Sign Language"))
    }

    @Test
    fun visionIntent_returnsBeMyEyesGuidance() {
        val reply = accessibilityFallbackReply("help me read this page")
        assertTrue(reply.contains("Be My Eyes"))
    }

    @Test
    fun identityIntent_returnsRafiqIntro() {
        val reply = accessibilityFallbackReply("who are you?")
        assertTrue(reply.contains("RAFIQ"))
    }

    @Test
    fun greeting_returnsGreeting() {
        val reply = accessibilityFallbackReply("Hi")
        assertEquals("Hello! Welcome to RAFIQ. How can I help you today?", reply)
    }

    @Test
    fun gratitude_returnsGratitude() {
        val reply = accessibilityFallbackReply("thank you")
        assertEquals("You're very welcome! I'm always here to assist you.", reply)
    }

    @Test
    fun fallback_returnsGenericHelp_onUnknownInput() {
        val reply = accessibilityFallbackReply("random gibberish xyz")
        assertTrue(reply.contains("accessibility assistant"))
    }

    @Test
    fun input_isCaseInsensitive() {
        val upper = accessibilityFallbackReply("MEDICATION")
        val lower = accessibilityFallbackReply("medication")
        assertEquals(upper, lower)
    }
}
