package me.typeflu.calculator.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
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
    shape: Shape = CircleShape,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val visuals = keyVisuals(style)
    val haptics = LocalHapticFeedback.current
    val scale by animateFloatAsState(
        targetValue = if (isPressed) visuals.pressedScale else 1f,
        animationSpec = tween(durationMillis = 180),
        label = "scale"
    )
    val highlightAlpha by animateFloatAsState(
        targetValue = if (isPressed) visuals.overlayAlpha else 0f,
        animationSpec = tween(durationMillis = 180),
        label = "overlay"
    )
    Surface(
        modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale },
        color = visuals.container,
        contentColor = visuals.content,
        shape = shape,
        tonalElevation = visuals.tonalElevation,
        shadowElevation = 0.dp,
        interactionSource = interactionSource,
        onClick = {
            haptics.performHapticFeedback(hapticFor(style))
            onClick()
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (highlightAlpha > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(visuals.overlayColor.copy(alpha = highlightAlpha))
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(visuals.contentPadding),
                contentAlignment = Alignment.Center,
                content = content
            )
        }
    }
}

private fun hapticFor(style: KeyStyle): HapticFeedbackType = when (style) {
    KeyStyle.Primary -> HapticFeedbackType.KeyboardTap
    KeyStyle.Function -> HapticFeedbackType.LongPress
    KeyStyle.Secondary -> HapticFeedbackType.VirtualKey
    KeyStyle.Accent -> HapticFeedbackType.Confirm
}

private data class KeyVisuals(
    val container: Color,
    val content: Color,
    val overlayColor: Color,
    val contentPadding: PaddingValues,
    val pressedScale: Float,
    val overlayAlpha: Float,
    val tonalElevation: Dp
)

@Composable
private fun keyVisuals(style: KeyStyle): KeyVisuals {
    val scheme = MaterialTheme.colorScheme
    val neutral = lerp(scheme.surfaceVariant, scheme.surface, 0.45f)
    val subtle = lerp(scheme.surfaceBright, scheme.surfaceVariant, 0.6f)
    return when (style) {
        KeyStyle.Primary -> KeyVisuals(
            container = neutral,
            content = scheme.onSurface,
            overlayColor = scheme.onSurface,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
            pressedScale = 0.96f,
            overlayAlpha = 0.12f,
            tonalElevation = 2.dp
        )
        KeyStyle.Function -> KeyVisuals(
            container = subtle,
            content = scheme.onSurface,
            overlayColor = scheme.onSurface,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
            pressedScale = 0.96f,
            overlayAlpha = 0.08f,
            tonalElevation = 1.dp
        )
        KeyStyle.Secondary -> KeyVisuals(
            container = scheme.secondary,
            content = scheme.onSecondary,
            overlayColor = scheme.onSecondary,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
            pressedScale = 0.95f,
            overlayAlpha = 0.15f,
            tonalElevation = 4.dp
        )
        KeyStyle.Accent -> KeyVisuals(
            container = scheme.primary,
            content = scheme.onPrimary,
            overlayColor = scheme.onPrimary,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
            pressedScale = 0.94f,
            overlayAlpha = 0.18f,
            tonalElevation = 6.dp
        )
    }
}
