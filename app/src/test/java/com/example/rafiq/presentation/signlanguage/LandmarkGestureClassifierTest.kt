package com.example.rafiq.presentation.signlanguage

import com.example.rafiq.presentation.signlanguage.LandmarkGestureClassifier.Lm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LandmarkGestureClassifierTest {

    private enum class ThumbMode { TUCKED, EXTENDED, PINCH }

    private fun finger(extended: Boolean): List<Lm> =
        if (extended) {
            listOf(Lm(0f, 0f, 0f), Lm(0f, 1f, 0f), Lm(0f, 2f, 0f), Lm(0f, 3f, 0f))
        } else {
            listOf(Lm(0f, 0f, 0f), Lm(0f, 1f, 0f), Lm(0f, 0.6f, 0f), Lm(0f, 0.3f, 0f))
        }

    private fun buildHand(
        indexExt: Boolean = false,
        middleExt: Boolean = false,
        ringExt: Boolean = false,
        pinkyExt: Boolean = false,
        thumb: ThumbMode = ThumbMode.TUCKED
    ): List<Lm> {
        val pts = MutableList(21) { Lm(0f, 0f, 0f) }
        pts[0] = Lm(0f, 0f, 0f) // wrist

        val thumbFinger = listOf(Lm(0f, 0f, 0f), Lm(0f, 0f, 0f), Lm(0f, 0f, 0f), Lm(0f, 0f, 0f))
        val indexF = finger(indexExt)
        val middleF = finger(middleExt)
        val ringF = finger(ringExt)
        val pinkyF = finger(pinkyExt)

        fun place(base: Int, offsetX: Float, f: List<Lm>) {
            for (i in 0..3) pts[base + i] = Lm(offsetX + f[i].x, f[i].y, 0f)
        }

        place(1, 0f, thumbFinger)
        place(5, 1f, indexF)
        place(9, 2f, middleF)
        place(13, 3f, ringF)
        place(17, 4f, pinkyF)

        when (thumb) {
            ThumbMode.TUCKED -> pts[4] = Lm(0f, 0.1f, 0f)
            ThumbMode.EXTENDED -> pts[4] = Lm(1f, 1.5f, 0f)
            ThumbMode.PINCH -> pts[4] = pts[8] // thumb tip on index tip
        }

        // Scale so the hand is a realistic normalised size (handSize ~> 0.15)
        val s = 0.1f
        return pts.map { Lm(it.x * s, it.y * s, it.z * s) }
    }

    @Test
    fun `OK sign - thumb and index pinched, other fingers up`() {
        val hand = buildHand(middleExt = true, ringExt = true, pinkyExt = true, thumb = ThumbMode.PINCH)
        assertEquals(LandmarkGestureClassifier.SIGN_OK, LandmarkGestureClassifier.classify(hand))
    }

    @Test
    fun `Rock sign - index and pinky up, middle and ring folded, thumb tucked`() {
        val hand = buildHand(indexExt = true, middleExt = false, ringExt = false, pinkyExt = true)
        assertEquals(LandmarkGestureClassifier.SIGN_ROCK, LandmarkGestureClassifier.classify(hand))
    }

    @Test
    fun `Letter L - index and thumb extended, others folded`() {
        val hand = buildHand(indexExt = true, thumb = ThumbMode.EXTENDED)
        assertEquals(LandmarkGestureClassifier.SIGN_L, LandmarkGestureClassifier.classify(hand))
    }

    @Test
    fun `Closed fist - no custom sign detected`() {
        val hand = buildHand()
        assertNull(LandmarkGestureClassifier.classify(hand))
    }

    @Test
    fun `Open palm - no custom sign detected`() {
        val hand = buildHand(indexExt = true, middleExt = true, ringExt = true, pinkyExt = true)
        assertNull(LandmarkGestureClassifier.classify(hand))
    }

    @Test
    fun `I love you - no custom sign detected (thumb extended, pinky up)`() {
        val hand = buildHand(indexExt = true, middleExt = false, ringExt = false, pinkyExt = true, thumb = ThumbMode.EXTENDED)
        assertNull(LandmarkGestureClassifier.classify(hand))
    }

    @Test
    fun `Hand too small - ignored`() {
        val hand = buildHand(indexExt = true, thumb = ThumbMode.EXTENDED).map {
            Lm(it.x * 0.05f, it.y * 0.05f, it.z * 0.05f)
        }
        assertNull(LandmarkGestureClassifier.classify(hand))
    }

    @Test
    fun `Wrong landmark count - ignored`() {
        assertNull(LandmarkGestureClassifier.classify(listOf(Lm(0f, 0f, 0f), Lm(1f, 1f, 0f))))
    }
}