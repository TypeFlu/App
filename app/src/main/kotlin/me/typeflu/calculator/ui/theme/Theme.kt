package me.typeflu.calculator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AuroraViolet,
    onPrimary = StellarEdge,
    primaryContainer = MoonlitLavender,
    secondary = ElectricCyan,
    onSecondary = DeepSpace,
    background = Color(0xFFF5F6FF),
    onBackground = DeepSpace,
    surface = Color(0xFFF8F9FF),
    onSurface = DeepSpace,
    outline = GlassSurfaceDark
)

private val DarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = DeepSpace,
    primaryContainer = AuroraViolet,
    secondary = MoonlitLavender,
    onSecondary = StellarEdge,
    background = DeepSpace,
    onBackground = StellarEdge,
    surface = MidnightBlue,
    onSurface = StellarEdge,
    outline = GlassSurfaceLight
)

@Composable
fun TCalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = TCalculatorTypography,
        shapes = TCalculatorShapes,
        content = content
    )
}
