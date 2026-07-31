package com.example.rafiq.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

enum class FontScale(val label: String, val multiplier: Float) {
    SMALL("small", 0.85f),
    NORMAL("normal", 1.0f),
    LARGE("large", 1.3f),
    XLARGE("xlarge", 1.7f);

    companion object {
        fun fromValue(value: String): FontScale = entries.find { it.name.equals(value, ignoreCase = true) } ?: NORMAL
    }
}

enum class FontType(val label: String, val family: FontFamily) {
    DEFAULT("default", FontFamily.Default),
    SERIF("serif", FontFamily.Serif),
    SANS_SERIF("sans-serif", FontFamily.SansSerif),
    MONOSPACE("monospace", FontFamily.Monospace);

    companion object {
        fun fromValue(value: String): FontType = entries.find { it.name.equals(value, ignoreCase = true) } ?: DEFAULT
    }
}

fun scaledTypography(scale: FontScale, fontType: FontType): Typography {
    val m = scale.multiplier
    val f = fontType.family
    return Typography(
        displayLarge = TextStyle(fontFamily = f, fontWeight = FontWeight.Normal, fontSize = (57 * m).sp, lineHeight = (64 * m).sp, letterSpacing = (-0.25 * m).sp),
        displayMedium = TextStyle(fontFamily = f, fontWeight = FontWeight.Normal, fontSize = (45 * m).sp, lineHeight = (52 * m).sp, letterSpacing = 0.sp),
        displaySmall = TextStyle(fontFamily = f, fontWeight = FontWeight.Normal, fontSize = (36 * m).sp, lineHeight = (44 * m).sp, letterSpacing = 0.sp),
        headlineLarge = TextStyle(fontFamily = f, fontWeight = FontWeight.Normal, fontSize = (32 * m).sp, lineHeight = (40 * m).sp, letterSpacing = 0.sp),
        headlineMedium = TextStyle(fontFamily = f, fontWeight = FontWeight.Normal, fontSize = (28 * m).sp, lineHeight = (36 * m).sp, letterSpacing = 0.sp),
        headlineSmall = TextStyle(fontFamily = f, fontWeight = FontWeight.Normal, fontSize = (24 * m).sp, lineHeight = (32 * m).sp, letterSpacing = 0.sp),
        titleLarge = TextStyle(fontFamily = f, fontWeight = FontWeight.Normal, fontSize = (22 * m).sp, lineHeight = (28 * m).sp, letterSpacing = 0.sp),
        titleMedium = TextStyle(fontFamily = f, fontWeight = FontWeight.Medium, fontSize = (16 * m).sp, lineHeight = (24 * m).sp, letterSpacing = (0.15 * m).sp),
        titleSmall = TextStyle(fontFamily = f, fontWeight = FontWeight.Medium, fontSize = (14 * m).sp, lineHeight = (20 * m).sp, letterSpacing = (0.1 * m).sp),
        bodyLarge = TextStyle(fontFamily = f, fontWeight = FontWeight.Normal, fontSize = (16 * m).sp, lineHeight = (24 * m).sp, letterSpacing = (0.5 * m).sp),
        bodyMedium = TextStyle(fontFamily = f, fontWeight = FontWeight.Normal, fontSize = (14 * m).sp, lineHeight = (20 * m).sp, letterSpacing = (0.25 * m).sp),
        bodySmall = TextStyle(fontFamily = f, fontWeight = FontWeight.Normal, fontSize = (12 * m).sp, lineHeight = (16 * m).sp, letterSpacing = (0.4 * m).sp),
        labelLarge = TextStyle(fontFamily = f, fontWeight = FontWeight.Medium, fontSize = (14 * m).sp, lineHeight = (20 * m).sp, letterSpacing = (0.1 * m).sp),
        labelMedium = TextStyle(fontFamily = f, fontWeight = FontWeight.Medium, fontSize = (12 * m).sp, lineHeight = (16 * m).sp, letterSpacing = (0.5 * m).sp),
        labelSmall = TextStyle(fontFamily = f, fontWeight = FontWeight.Medium, fontSize = (11 * m).sp, lineHeight = (16 * m).sp, letterSpacing = (0.5 * m).sp)
    )
}
