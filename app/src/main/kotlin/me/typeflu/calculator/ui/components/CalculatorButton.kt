package me.typeflu.calculator.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

enum class KeyStyle {
    Primary,
    Secondary,
    Function,
    Accent
}

@Composable
fun CalculatorButton(
    modifier: Modifier = Modifier,
    style: KeyStyle,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val glow = when (style) {
        KeyStyle.Primary -> Brush.linearGradient(listOf(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)))
        KeyStyle.Secondary -> Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.85f), MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)))
        KeyStyle.Function -> Brush.linearGradient(listOf(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f), MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)))
        KeyStyle.Accent -> Brush.linearGradient(listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primary))
    }
    val borderColor by animateColorAsState(
        targetValue = if (isPressed) Color.White.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.2f),
        label = "border"
    )
    val elevation by animateDpAsState(targetValue = if (isPressed) 4.dp else 12.dp, label = "elevation")
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.97f else 1f, label = "scale")
    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale; shadowElevation = elevation.toPx() }
            .clip(RoundedCornerShape(28.dp))
            .background(glow)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(28.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.White.copy(alpha = 0.3f)),
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}
