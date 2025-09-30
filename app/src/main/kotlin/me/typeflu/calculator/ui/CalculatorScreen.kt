package me.typeflu.calculator.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import me.typeflu.calculator.data.CalculatorAction
import me.typeflu.calculator.data.CalculatorOperation
import me.typeflu.calculator.data.CalculatorState
import me.typeflu.calculator.data.CalculatorViewModel
import me.typeflu.calculator.ui.components.AuroraBackground
import me.typeflu.calculator.ui.components.CalculatorButton
import me.typeflu.calculator.ui.components.CalculatorChip
import me.typeflu.calculator.ui.components.KeyStyle
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private data class CalculatorKey(
    val label: String,
    val action: CalculatorAction,
    val style: KeyStyle,
    val span: Int = 1
)

private val keyLayout = listOf(
    CalculatorKey("C", CalculatorAction.Clear, KeyStyle.Function),
    CalculatorKey("±", CalculatorAction.ToggleSign, KeyStyle.Function),
    CalculatorKey("%", CalculatorAction.Percent, KeyStyle.Function),
    CalculatorKey("÷", CalculatorAction.Operation(CalculatorOperation.Divide), KeyStyle.Secondary),
    CalculatorKey("7", CalculatorAction.Digit(7), KeyStyle.Primary),
    CalculatorKey("8", CalculatorAction.Digit(8), KeyStyle.Primary),
    CalculatorKey("9", CalculatorAction.Digit(9), KeyStyle.Primary),
    CalculatorKey("×", CalculatorAction.Operation(CalculatorOperation.Multiply), KeyStyle.Secondary),
    CalculatorKey("4", CalculatorAction.Digit(4), KeyStyle.Primary),
    CalculatorKey("5", CalculatorAction.Digit(5), KeyStyle.Primary),
    CalculatorKey("6", CalculatorAction.Digit(6), KeyStyle.Primary),
    CalculatorKey("−", CalculatorAction.Operation(CalculatorOperation.Subtract), KeyStyle.Secondary),
    CalculatorKey("1", CalculatorAction.Digit(1), KeyStyle.Primary),
    CalculatorKey("2", CalculatorAction.Digit(2), KeyStyle.Primary),
    CalculatorKey("3", CalculatorAction.Digit(3), KeyStyle.Primary),
    CalculatorKey("+", CalculatorAction.Operation(CalculatorOperation.Add), KeyStyle.Secondary),
    CalculatorKey("0", CalculatorAction.Digit(0), KeyStyle.Primary, span = 2),
    CalculatorKey(".", CalculatorAction.Decimal, KeyStyle.Primary),
    CalculatorKey("⌫", CalculatorAction.Delete, KeyStyle.Function),
    CalculatorKey("=", CalculatorAction.Equals, KeyStyle.Accent, span = 4)
)

@Composable
fun CalculatorRoute(
    modifier: Modifier = Modifier,
    viewModel: CalculatorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val darkTheme = isSystemInDarkTheme()
    CalculatorScreen(state = state, darkTheme = darkTheme, modifier = modifier, onAction = viewModel::onAction)
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun CalculatorScreen(
    state: CalculatorState,
    darkTheme: Boolean,
    modifier: Modifier = Modifier,
    onAction: (CalculatorAction) -> Unit
) {
    val accentColor by animateColorAsState(
        targetValue = if (state.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
        label = "accent"
    )
    Box(modifier = modifier.fillMaxSize()) {
        AuroraBackground(modifier = Modifier.fillMaxSize(), darkTheme = darkTheme)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            DisplayArea(state = state, textColor = accentColor)
            Spacer(modifier = Modifier.height(18.dp))
            MemoryRow(state = state, onAction = onAction)
            Spacer(modifier = Modifier.height(18.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(keyLayout, span = { GridItemSpan(it.span) }) { key ->
                    CalculatorButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (key.label == "=") 78.dp else 68.dp),
                        style = key.style,
                        onClick = { onAction(key.action) }
                    ) {
                        Text(
                            text = key.label,
                            style = when (key.style) {
                                KeyStyle.Accent -> MaterialTheme.typography.displayMedium
                                KeyStyle.Secondary -> MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                                KeyStyle.Function -> MaterialTheme.typography.titleLarge
                                KeyStyle.Primary -> MaterialTheme.typography.titleLarge
                            },
                            textAlign = TextAlign.Center,
                            color = when (key.style) {
                                KeyStyle.Accent -> MaterialTheme.colorScheme.onPrimary
                                KeyStyle.Secondary -> MaterialTheme.colorScheme.onPrimary
                                KeyStyle.Function -> MaterialTheme.colorScheme.onSurface
                                KeyStyle.Primary -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun DisplayArea(state: CalculatorState, textColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.End
    ) {
        val expression = state.expression
        if (expression.isNotBlank()) {
            Text(
                text = expression,
                style = MaterialTheme.typography.bodyLarge.copy(color = textColor.copy(alpha = 0.65f)),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        AnimatedContent(
            targetState = formatDisplay(state.display),
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 260)) togetherWith fadeOut(animationSpec = tween(durationMillis = 220))
            },
            label = "display"
        ) { value ->
            Text(
                text = value,
                style = MaterialTheme.typography.displayLarge.copy(color = textColor),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End
            )
        }
        if (state.hasMemory) {
            Text(
                text = "Memory",
                style = MaterialTheme.typography.labelLarge.copy(color = textColor.copy(alpha = 0.7f)),
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Composable
private fun MemoryRow(state: CalculatorState, onAction: (CalculatorAction) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val memoryActions = listOf(
            Triple("MC", CalculatorAction.MemoryClear, state.hasMemory),
            Triple("MR", CalculatorAction.MemoryRecall, state.hasMemory),
            Triple("M+", CalculatorAction.MemoryAdd, false),
            Triple("M−", CalculatorAction.MemorySubtract, false)
        )
        memoryActions.forEach { (label, action, highlight) ->
            CalculatorChip(text = label, highlight = highlight) { onAction(action) }
        }
    }
}

private fun formatDisplay(raw: String): String {
    if (raw == "Error") return raw
    val negative = raw.startsWith('-')
    val clean = if (negative) raw.removePrefix("-") else raw
    val hasTrailingDot = clean.endsWith('.')
    val parts = clean.split('.', limit = 2)
    val integerPart = parts.getOrElse(0) { "0" }.ifEmpty { "0" }
    val fractionalPart = parts.getOrNull(1)
    val formatter = DecimalFormat("#,###", DecimalFormatSymbols(Locale.getDefault()))
    val grouped = integerPart.toBigDecimalOrNull()?.toBigInteger()?.let { formatter.format(it) } ?: integerPart
    val result = buildString {
        if (negative) append('-')
        append(grouped)
        when {
            hasTrailingDot -> append('.')
            fractionalPart != null -> append('.').append(fractionalPart)
        }
    }
    return result
}
