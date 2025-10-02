package me.typeflu.calculator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val ExpressiveLightColors = lightColorScheme(
    primary = AuroraViolet,
    onPrimary = Color.White,
    primaryContainer = MoonlitLavender,
    onPrimaryContainer = DeepSpace,
    secondary = ElectricCyan,
    onSecondary = DeepSpace,
    secondaryContainer = ElectricCyan.copy(alpha = 0.18f),
    onSecondaryContainer = DeepSpace,
    tertiary = SolarFlare,
    onTertiary = DeepSpace,
    background = Color(0xFFFCF8FF),
    onBackground = DeepSpace,
    surface = Color.White,
    onSurface = DeepSpace,
    surfaceVariant = GlassSurfaceDark,
    onSurfaceVariant = DeepSpace.copy(alpha = 0.7f),
    outline = GlassSurfaceDark
)

private val ExpressiveDarkColors = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = DeepSpace,
    primaryContainer = ElectricCyan.copy(alpha = 0.2f),
    onPrimaryContainer = ElectricCyan,
    secondary = AuroraViolet,
    onSecondary = StellarEdge,
    secondaryContainer = AuroraViolet.copy(alpha = 0.35f),
    onSecondaryContainer = StellarEdge,
    tertiary = SolarFlare,
    onTertiary = StellarEdge,
    background = DeepSpace,
    onBackground = StellarEdge,
    surface = MidnightBlue,
    onSurface = StellarEdge,
    surfaceVariant = GlassSurfaceLight,
    onSurfaceVariant = StellarEdge.copy(alpha = 0.72f),
    outline = GlassSurfaceLight
)

@Composable
fun TCalculatorTheme(
    useDynamicColor: Boolean = true,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = remember(context, darkTheme, useDynamicColor) {
        when {
            useDynamicColor && darkTheme -> dynamicDarkColorScheme(context)
            useDynamicColor && !darkTheme -> dynamicLightColorScheme(context)
            darkTheme -> ExpressiveDarkColors
            else -> ExpressiveLightColors
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = TCalculatorTypography,
        shapes = TCalculatorShapes,
        content = content
    )
}
