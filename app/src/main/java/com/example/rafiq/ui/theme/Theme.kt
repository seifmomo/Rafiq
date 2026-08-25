package com.example.rafiq.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val PremiumShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

private val DarkColorScheme = darkColorScheme(
    primary = Cyan,
    onPrimary = Color.White,
    primaryContainer = Cyan.copy(alpha = 0.15f),
    onPrimaryContainer = CyanLight,
    secondary = Teal,
    onSecondary = Color.White,
    secondaryContainer = Teal.copy(alpha = 0.15f),
    onSecondaryContainer = TealLight,
    tertiary = Color(0xFF818CF8),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF818CF8).copy(alpha = 0.15f),
    onTertiaryContainer = Color(0xFFC7D2FE),
    background = DeepBlueDark,
    onBackground = Color(0xFFF1F5F9),
    surface = DeepBlue,
    onSurface = Color(0xFFF1F5F9),
    surfaceDim = DeepBlueDark,
    surfaceContainer = DeepBlueLight,
    surfaceContainerHigh = Color(0xFF2A3A52),
    surfaceContainerLow = DeepBlue,
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155),
    outlineVariant = Color(0xFF1E293B),
    error = ErrorRed,
    errorContainer = ErrorRed.copy(alpha = 0.15f),
    onError = Color.White,
    onErrorContainer = Color(0xFFFEE2E2)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0891B2),
    onPrimary = Color.White,
    primaryContainer = CyanSoft,
    onPrimaryContainer = Color(0xFF164E63),
    secondary = Teal,
    onSecondary = Color.White,
    secondaryContainer = TealSoft,
    onSecondaryContainer = Color(0xFF134E4A),
    tertiary = Color(0xFF6366F1),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE0E7FF),
    onTertiaryContainer = Color(0xFF312E81),
    background = SurfaceDim,
    onBackground = DeepBlue,
    surface = Surface,
    onSurface = OnSurface,
    surfaceDim = SurfaceDim,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerLow = SurfaceDim,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
    error = ErrorRed,
    errorContainer = ErrorRedLight,
    onError = Color.White,
    onErrorContainer = Color(0xFF7F1D1D)
)

@Composable
fun RAFIQTheme(
    darkTheme: Boolean = true,
    fontSize: String = "normal",
    fontFamily: String = "default",
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val fontScale = FontScale.fromValue(fontSize)
    val fontType = FontType.fromValue(fontFamily)
    val typography = scaledTypography(fontScale, fontType)
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = Color.Transparent.toArgb()
            @Suppress("DEPRECATION")
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = PremiumShapes,
        content = content
    )
}
