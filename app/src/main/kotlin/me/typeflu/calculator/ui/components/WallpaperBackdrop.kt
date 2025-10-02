package me.typeflu.calculator.ui.components

import android.app.WallpaperManager
import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun WallpaperBackdrop(
    modifier: Modifier = Modifier,
    darkTheme: Boolean,
    wallpaperUri: Uri?,
    overlay: @Composable BoxScope.() -> Unit = {}
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenSize = remember(configuration, density, context) {
        val displayMetrics = context.resources.displayMetrics
        val width =
            configuration.screenWidthDp.takeIf { it != Configuration.SCREEN_WIDTH_DP_UNDEFINED }
                ?.let { with(density) { it.dp.roundToPx() } }
                ?.coerceAtLeast(1)
                ?: displayMetrics.widthPixels.coerceAtLeast(1)
        val height =
            configuration.screenHeightDp.takeIf { it != Configuration.SCREEN_HEIGHT_DP_UNDEFINED }
                ?.let { with(density) { it.dp.roundToPx() } }
                ?.coerceAtLeast(1)
                ?: displayMetrics.heightPixels.coerceAtLeast(1)
        IntSize(width, height)
    }
    val wallpaperManager = remember(context) { WallpaperManager.getInstance(context) }
    val wallpaperVisual by rememberWallpaperVisual(context, wallpaperManager, screenSize)
    val wallpaperBitmap = wallpaperVisual.bitmap
    val surfaceAlphaTarget = if (darkTheme) 0.78f else 0.66f
    val surfaceAlpha by animateFloatAsState(
        targetValue = surfaceAlphaTarget,
        label = "surfaceAlpha"
    )

    val fallbackGradient = listOf(
        MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
        MaterialTheme.colorScheme.surface
    )
    Box(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
        when {
            wallpaperUri != null -> {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(wallpaperUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(28.dp),
                    contentScale = ContentScale.Crop
                )
            }

            wallpaperBitmap != null -> {
                Image(
                    bitmap = wallpaperBitmap,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(28.dp),
                    contentScale = ContentScale.Crop
                )
            }

            wallpaperVisual.gradientColors.isNotEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(wallpaperVisual.gradientColors))
                )
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(fallbackGradient))
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = surfaceAlpha * 0.85f),
                            MaterialTheme.colorScheme.surface.copy(alpha = surfaceAlpha)
                        )
                    )
                )
        )
        overlay()
    }
}

@Composable
private fun rememberWallpaperVisual(
    context: Context,
    wallpaperManager: WallpaperManager,
    targetSize: IntSize
) = produceState(initialValue = WallpaperVisual(), wallpaperManager, targetSize, context) {
    value = loadWallpaperVisual(context, wallpaperManager, targetSize)
}

private suspend fun loadWallpaperVisual(
    context: Context,
    wallpaperManager: WallpaperManager,
    targetSize: IntSize
): WallpaperVisual = withContext(Dispatchers.IO) {
    val palette = obtainWallpaperPalette(wallpaperManager)
    val bitmap = when (val drawable = resolveWallpaperDrawable(context, wallpaperManager)) {
        is BitmapDrawable -> drawable.bitmap
        null -> null
        else -> runCatching {
            drawable.toBitmap(
                width = targetSize.width,
                height = targetSize.height
            )
        }.getOrNull()
    }
    val imageBitmap = bitmap?.asImageBitmap()
    WallpaperVisual(imageBitmap, palette)
}

private fun resolveWallpaperDrawable(
    context: Context,
    wallpaperManager: WallpaperManager
): Drawable? {
    return runCatching { wallpaperManager.builtInDrawable }.getOrNull()
}

private fun obtainWallpaperPalette(wallpaperManager: WallpaperManager): List<Color> {
    val colors =
        runCatching { wallpaperManager.getWallpaperColors(WallpaperManager.FLAG_SYSTEM) }.getOrNull()
    if (colors == null) return emptyList()
    return buildList {
        add(Color(colors.primaryColor.toArgb()))
        colors.secondaryColor?.let { add(Color(it.toArgb())) }
        colors.tertiaryColor?.let { add(Color(it.toArgb())) }
    }
}

private data class WallpaperVisual(
    val bitmap: ImageBitmap? = null,
    val gradientColors: List<Color> = emptyList()
)
