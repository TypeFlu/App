package me.typeflu.calculator.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun AuroraBackground(modifier: Modifier = Modifier, darkTheme: Boolean) {
    val transition = rememberInfiniteTransition(label = "aurora")
    val primaryShift by transition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "primary"
    )
    val secondaryShift by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "secondary"
    )
    val tertiaryShift by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tertiary"
    )
    val baseColor = if (darkTheme) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surface
    val primaryColors = if (darkTheme) listOf(Color(0xFF1E1465), Color(0xFF1B8FFF), Color.Transparent) else listOf(Color(0xFFFF7AD2), Color(0xFF6DDCFF), Color.Transparent)
    val secondaryColors = if (darkTheme) listOf(Color(0xFF441B6D), Color(0xFF14FFC4), Color.Transparent) else listOf(Color(0xFF8FDFFF), Color(0xFFEABAFF), Color.Transparent)
    val tertiaryColors = if (darkTheme) listOf(Color(0xFF081B4B), Color(0xFF8246FF), Color.Transparent) else listOf(Color(0xFFB3FFAB), Color(0xFF12FFF7), Color.Transparent)
    Box(modifier = modifier.background(baseColor)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val maxRadius = max(size.width, size.height)
            val primaryCenter = androidx.compose.ui.geometry.Offset(size.width * primaryShift, size.height * 0.35f)
            val secondaryCenter = androidx.compose.ui.geometry.Offset(size.width * 0.25f, size.height * secondaryShift)
            val tertiaryCenter = androidx.compose.ui.geometry.Offset(size.width * 0.75f, size.height * tertiaryShift)
            drawCircle(
                brush = Brush.radialGradient(primaryColors, center = primaryCenter, radius = maxRadius * 0.9f),
                radius = maxRadius * 0.9f,
                center = primaryCenter,
                alpha = 0.85f
            )
            drawCircle(
                brush = Brush.radialGradient(secondaryColors, center = secondaryCenter, radius = maxRadius * 0.75f),
                radius = maxRadius * 0.75f,
                center = secondaryCenter,
                alpha = 0.65f
            )
            drawCircle(
                brush = Brush.radialGradient(tertiaryColors, center = tertiaryCenter, radius = maxRadius * 0.7f),
                radius = maxRadius * 0.7f,
                center = tertiaryCenter,
                alpha = 0.55f
            )
            translate(left = size.width * 0.1f, top = size.height * 0.1f) {
                drawOval(
                    brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)),
                    topLeft = androidx.compose.ui.geometry.Offset.Zero,
                    size = androidx.compose.ui.geometry.Size(size.width * 0.8f, size.height * 0.4f)
                )
            }
            drawCircle(
                brush = Brush.radialGradient(listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)),
                radius = maxRadius * 0.5f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.15f)
            )
            val rippleRadius = maxRadius * (0.3f + 0.05f * (1f - primaryShift))
            drawCircle(
                color = Color.White.copy(alpha = 0.12f),
                radius = rippleRadius,
                center = primaryCenter,
                style = Stroke(width = 4.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), phase = rippleRadius * 0.18f))
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 96.dp, end = 48.dp)
                .size(220.dp)
                .blur(140.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.45f), Color.Transparent)))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 32.dp)
                .size(180.dp)
                .blur(120.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.35f), Color.Transparent)))
        )
    }
}
