package me.typeflu.calculator.ui

import android.net.Uri
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Wallpaper
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import me.typeflu.calculator.ui.components.CalculatorButton
import me.typeflu.calculator.ui.components.KeyStyle
import me.typeflu.calculator.ui.components.WallpaperBackdrop
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
    CalculatorKey("⌫", CalculatorAction.Delete, KeyStyle.Function),
    CalculatorKey("AC", CalculatorAction.Clear, KeyStyle.Function),
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
    CalculatorKey("=", CalculatorAction.Equals, KeyStyle.Accent, span = 2)
)

@Composable
fun CalculatorRoute(
    modifier: Modifier = Modifier,
    viewModel: CalculatorViewModel = viewModel(),
    windowSizeClass: WindowSizeClass,
    wallpaperUri: Uri?,
    onSelectWallpaper: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val darkTheme = isSystemInDarkTheme()
    CalculatorScreen(
        state = state,
        darkTheme = darkTheme,
        windowSizeClass = windowSizeClass,
        wallpaperUri = wallpaperUri,
        onSelectWallpaper = onSelectWallpaper,
        modifier = modifier,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun CalculatorScreen(
    state: CalculatorState,
    darkTheme: Boolean,
    windowSizeClass: WindowSizeClass,
    wallpaperUri: Uri?,
    onSelectWallpaper: () -> Unit,
    modifier: Modifier = Modifier,
    onAction: (CalculatorAction) -> Unit
) {
    val displayColor by animateColorAsState(
        targetValue = if (state.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
        label = "display"
    )
    val columns = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 4
        else -> 4
    }
    val layout = remember { keyLayout }
    Box(modifier = modifier.fillMaxSize()) {
        WallpaperBackdrop(
            modifier = Modifier.fillMaxSize(),
            darkTheme = darkTheme,
            wallpaperUri = wallpaperUri
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    IconButton(onClick = onSelectWallpaper) {
                        Icon(
                            imageVector = Icons.Outlined.Wallpaper,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                DisplayArea(
                    state = state,
                    textColor = displayColor,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(layout, span = { GridItemSpan(it.span) }) { key ->
                    CalculatorButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(if (key.span > 1) key.span.toFloat() else 1f),
                        style = key.style,
                        onClick = { onAction(key.action) }
                    ) {
                        Text(
                            text = key.label,
                            style = when (key.style) {
                                KeyStyle.Accent -> MaterialTheme.typography.displaySmall
                                KeyStyle.Secondary -> MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                                KeyStyle.Function -> MaterialTheme.typography.titleMedium
                                KeyStyle.Primary -> MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                            },
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun DisplayArea(state: CalculatorState, textColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.End
    ) {
        val expression = state.expression
        if (expression.isNotBlank()) {
            Text(
                text = expression,
                style = MaterialTheme.typography.bodyLarge.copy(color = textColor.copy(alpha = 0.6f)),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        AnimatedContent(
            targetState = formatDisplay(state.display),
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 240)) togetherWith
                    fadeOut(animationSpec = tween(durationMillis = 200))
            },
            label = "display"
        ) { value ->
            Text(
                text = value,
                style = MaterialTheme.typography.displayLarge.copy(
                    color = textColor,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End
            )
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
