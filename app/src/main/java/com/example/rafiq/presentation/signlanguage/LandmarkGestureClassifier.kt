package com.example.rafiq.presentation.signlanguage

/**
 * Lightweight hand-landmark classifier that recognises a few extra signs the bundled
 * MediaPipe gesture model cannot (its frozen category set only knows 7 gestures).
 *
 * Works on the 21 MediaPipe hand landmarks (index order from
 * https://ai.google.dev/edge/mediapipe/solutions/vision/hand_landmarker):
 *   0=wrist, 1-4=thumb (CMC,MCP,IP,TIP), 5-8=index (MCP,PIP,DIP,TIP),
 *   9-12=middle, 13-16=ring, 17-20=pinky
 *
 * Pure Kotlin with no Android/mediapipe dependencies so it can be unit-tested.
 */
object LandmarkGestureClassifier {

    data class Lm(val x: Float, val y: Float, val z: Float)

    /** 21 landmarks or null when the hand is too small / missing to trust the result. */
    fun classify(landmarks: List<Lm>): String? {
        if (landmarks.size != 21) return null

        val middleTip = landmarks[12]

        // Normalised hand size so thresholds stay scale invariant
        val handSize = dist(landmarks[0], middleTip)
        if (handSize < MIN_HAND_SIZE) return null

        val indexExt = isExtended(landmarks, 5, 6, 8)
        val middleExt = isExtended(landmarks, 9, 10, 12)
        val ringExt = isExtended(landmarks, 13, 14, 16)
        val pinkyExt = isExtended(landmarks, 17, 18, 20)
        val thumbExt = dist(landmarks[4], landmarks[5]) > dist(landmarks[3], landmarks[5]) * THUMB_EXTEND_RATIO

        // OK sign: thumb + index tips pinched together while middle/ring/pinky stay up
        val thumbIndexGap = dist(landmarks[4], landmarks[8]) / handSize
        if (thumbIndexGap < PINCH_THRESHOLD && middleExt && ringExt && pinkyExt) {
            return SIGN_OK
        }

        // Rock (metal horns): index + pinky up, middle + ring folded, thumb tucked in
        if (indexExt && pinkyExt && !middleExt && !ringExt && !thumbExt) {
            return SIGN_ROCK
        }

        // Letter L (ASL): index + thumb extended, all other fingers folded
        if (indexExt && thumbExt && !middleExt && !ringExt && !pinkyExt) {
            return SIGN_L
        }

        return null
    }

    private fun isExtended(landmarks: List<Lm>, mcp: Int, pip: Int, tip: Int): Boolean =
        dist(landmarks[tip], landmarks[mcp]) > dist(landmarks[pip], landmarks[mcp])

    private fun dist(a: Lm, b: Lm): Float {
        val dx = a.x - b.x
        val dy = a.y - b.y
        val dz = a.z - b.z
        return kotlin.math.sqrt(dx * dx + dy * dy + dz * dz)
    }

    const val SIGN_OK = "OK"
    const val SIGN_ROCK = "Rock"
    const val SIGN_L = "L"

    private const val MIN_HAND_SIZE = 0.15f
    private const val THUMB_EXTEND_RATIO = 1.3f
    private const val PINCH_THRESHOLD = 0.45f
}