package com.example.rafiq.util

import kotlin.math.sqrt

/**
 * Detects a fall from raw accelerometer samples without Android framework
 * dependencies, so the algorithm can be unit-tested on the JVM.
 *
 * A fall is modelled as two phases:
 *  1. Impact: a sudden spike in acceleration magnitude (the user hits the ground).
 *  2. Stagnation: after the impact, magnitudes settle back close to gravity (1 g)
 *     and stay there, meaning the user is now lying still.
 */
class FallDetectionClassifier(
    private val impactThresholdMs2: Float = GRAVITY_MS2 * 2.8f,
    private val stagnantBandMs2: Float = 1.5f,
    private val stagnantSamplesRequired: Int = 5
) {

    /** Analyze a time-ordered window of accelerometer samples and report falls. */
    fun detectFalls(
        samples: List<FloatArray>,
        afterImpactLag: Int = 1
    ): List<FallEvent> {
        if (samples.size < stagnantSamplesRequired) return emptyList()

        val events = mutableListOf<FallEvent>()

        samples.indices.forEach { impactIndex ->
            val magnitude = samples[impactIndex].magnitude()
            if (magnitude < impactThresholdMs2) return@forEach

            val stagnantStart = impactIndex + afterImpactLag
            if (stagnantStart >= samples.size) return@forEach
            val windowEnd = (stagnantStart + stagnantSamplesRequired).coerceAtMost(samples.size)
            if (windowEnd - stagnantStart < stagnantSamplesRequired) return@forEach

            var stagnant = true
            for (i in stagnantStart until windowEnd) {
                // "Still" means magnitude is close to gravity (the person lies on the ground).
                if (kotlin.math.abs(samples[i].magnitude() - GRAVITY_MS2) > stagnantBandMs2) {
                    stagnant = false
                    break
                }
            }

            if (stagnant) {
                events.add(
                    FallEvent(
                        impactIndex = impactIndex,
                        magnitudeMs2 = magnitude,
                        peakG = magnitude / GRAVITY_MS2
                    )
                )
            }
        }

        return events
    }

    /** Convenience: true when at least one fall was detected in the window. */
    fun isFallDetected(samples: List<FloatArray>): Boolean =
        detectFalls(samples).isNotEmpty()

    data class FallEvent(
        val impactIndex: Int,
        val magnitudeMs2: Float,
        val peakG: Float
    )

    companion object {
        const val GRAVITY_MS2 = 9.80665f

        fun FloatArray.magnitude(): Float =
            sqrt(get(0) * get(0) + get(1) * get(1) + get(2) * get(2))
    }
}