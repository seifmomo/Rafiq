package com.example.rafiq.util

import com.example.rafiq.util.FallDetectionClassifier.Companion.magnitude
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FallDetectionClassifierTest {

    private val classifier = FallDetectionClassifier()

    private fun stationary() = floatArrayOf(0.1f, 9.8f, 0.2f)
    private fun walking() = floatArrayOf(3.0f, 11.5f, 2.0f)
    private fun fallImpact() = floatArrayOf(2.0f, 30.0f, 1.0f)
    private fun preImpact() = floatArrayOf(0.8f, 9.6f, 0.5f)

    @Test
    fun stationary_returnsNoFall() {
        val samples = List(20) { stationary() }
        assertFalse(classifier.isFallDetected(samples))
        assertTrue(classifier.detectFalls(samples).isEmpty())
    }

    @Test
    fun walking_returnsNoFall() {
        val samples = List(20) { walking() }
        assertFalse(classifier.isFallDetected(samples))
    }

    @Test
    fun oneImpactFollowedByStillness_reportsOneFall() {
        val samples = (0 until 8).map { preImpact() } +
            listOf(fallImpact()) +
            (0 until 8).map { stationary() }

        val falls = classifier.detectFalls(samples)
        assertEquals(1, falls.size)
        assertEquals(8, falls.first().impactIndex)
        assertTrue(falls.first().peakG > 2.0f)
    }

    @Test
    fun impactWithoutStagnation_returnsNoFall() {
        val samples = (0 until 8).map { preImpact() } +
            listOf(fallImpact()) +
            (0 until 8).map { walking() }

        val falls = classifier.detectFalls(samples)
        assertTrue(falls.isEmpty())
    }

    @Test
    fun emptyList_returnsNoFall() {
        assertTrue(classifier.detectFalls(emptyList()).isEmpty())
        assertFalse(classifier.isFallDetected(emptyList()))
    }

    @Test
    fun impactTooCloseToEnd_returnsNoFall() {
        val samples = (0 until 8).map { preImpact() } + listOf(fallImpact()) + listOf(stationary()) + listOf(stationary())
        assertTrue(classifier.detectFalls(samples).isEmpty())
    }

    @Test
    fun veryShortWindow_returnsNoFall() {
        val samples = listOf(fallImpact(), stationary())
        assertFalse(classifier.isFallDetected(samples))
    }

    @Test
    fun twoDistinctFalls_detectedInSequence() {
        val samples =
            (0 until 3).map { preImpact() } +
            listOf(fallImpact()) +
            (0 until 5).map { stationary() } +
            (0 until 3).map { preImpact() } +
            listOf(fallImpact()) +
            (0 until 5).map { stationary() }

        val falls = classifier.detectFalls(samples)
        assertEquals(2, falls.size)
    }

    @Test
    fun customThreshold_higherImpactRequired() {
        val strictClassifier = FallDetectionClassifier(impactThresholdMs2 = 60.0f)
        val samples = (0 until 8).map { preImpact() } +
            listOf(fallImpact()) +
            (0 until 8).map { stationary() }
        assertTrue(strictClassifier.detectFalls(samples).isEmpty())
    }

    @Test
    fun magnitude_calculation_isCorrect() {
        val mag = floatArrayOf(3f, 4f, 0f).magnitude()
        assertEquals(5f, mag, 0.01f)
    }

    @Test
    fun gravityStationary_magnitudeCloseToGravity() {
        val mag = stationary().magnitude()
        assertEquals(9.80665f, mag, 0.5f)
    }
}