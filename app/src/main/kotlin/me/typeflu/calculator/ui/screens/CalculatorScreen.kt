package me.typeflu.calculator.ui.screens // Changed package

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
// Updated imports to me.typeflu.calculator
import me.typeflu.calculator.ui.theme.CalculatorAppTheme
import me.typeflu.calculator.ui.theme.EqualsButtonColor
import me.typeflu.calculator.ui.theme.LightEqualsButtonColor
import me.typeflu.calculator.viewmodel.CalculatorViewModel
import kotlinx.coroutines.launch

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = viewModel()
) {
    val displayState by viewModel.display.collectAsState()

    CalculatorAppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                val currentDisplayFontSize = when {
                    displayState.length > 9 -> 40.sp
                    displayState.length > 6 -> 56.sp
                    else -> 72.sp
                }

                Text(
                    text = displayState,
                    style = TextStyle(
                        fontSize = currentDisplayFontSize,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    maxLines = 1,
                )
            }

            CalculatorButtonGrid(onButtonClick = viewModel::onButtonClick)
        }
    }
}

@Composable
fun CalculatorButtonGrid(onButtonClick: (String) -> Unit) {
    val buttons = listOf(
        "AC", "±", "%", "÷",
        "7", "8", "9", "×",
        "4", "5", "6", "-",
        "1", "2", "3", "+",
        "0", ".", "="
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(buttons, key = { it }, span = { label -> GridItemSpan(if (label == "0") 2 else 1) }) { buttonLabel ->
            val (backgroundColor, contentColor, fontSize) = getButtonColorsAndStyle(label = buttonLabel)
            val buttonModifier = Modifier.aspectRatio(if (buttonLabel == "0") 2f else 1f)

            CalculatorButton(
                label = buttonLabel,
                onClick = { onButtonClick(buttonLabel) },
                modifier = buttonModifier,
                backgroundColor = backgroundColor,
                contentColor = contentColor,
                fontSize = fontSize
            )
        }
    }
}

@Composable
private fun getButtonColorsAndStyle(label: String): Triple<Color, Color, TextUnit> {
    val currentColorScheme = MaterialTheme.colorScheme
    val isDark = isSystemInDarkTheme()

    return when (label) {
        "AC" -> Triple(currentColorScheme.error, currentColorScheme.onError, 24.sp)
        "±", "%" -> Triple(currentColorScheme.primary, currentColorScheme.onPrimary, 28.sp)
        "÷", "×", "-", "+" -> Triple(currentColorScheme.primary, currentColorScheme.onPrimary, 32.sp)
        "=" -> Triple(
            if (isDark) EqualsButtonColor else LightEqualsButtonColor,
            if (isDark) Color.Black else Color.White, 
            32.sp
        )
        "." -> Triple(currentColorScheme.surfaceVariant, currentColorScheme.onSurfaceVariant, 32.sp)
        "0" -> Triple(currentColorScheme.surfaceVariant, currentColorScheme.onSurfaceVariant, 32.sp)
        else -> Triple(currentColorScheme.surfaceVariant, currentColorScheme.onSurfaceVariant, 32.sp)
    }
}

@Composable
fun CalculatorButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    fontSize: TextUnit = 32.sp
) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    Button(
        onClick = { /* Actual onClick is handled by pointerInput for animation control */ },
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        scope.launch {
                            scale.animateTo(
                                targetValue = 0.9f, 
                                animationSpec = tween(
                                    durationMillis = 75,
                                    easing = LinearOutSlowInEasing
                                )
                            )
                        }
                        tryAwaitRelease()
                        scope.launch {
                            scale.animateTo(
                                targetValue = 1f, 
                                animationSpec = tween(
                                    durationMillis = 100, 
                                    easing = FastOutSlowInEasing
                                )
                            )
                        }
                    },
                    onTap = { 
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onClick()
                    }
                )
            },
        shape = MaterialTheme.shapes.large, // Changed from CircleShape
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = fontSize,
            fontWeight = if (label.all { it.isDigit() || it == '.' }) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true, name = "Light Theme Preview")
@Composable
fun CalculatorScreenLightPreview() {
    CalculatorAppTheme(darkTheme = false) {
        CalculatorScreen()
    }
}

@Preview(showBackground = true, name = "Dark Theme Preview")
@Composable
fun CalculatorScreenDarkPreview() {
    CalculatorAppTheme(darkTheme = true) {
        CalculatorScreen()
    }
}
