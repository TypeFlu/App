package me.typeflu.calculator.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.net.Uri // Added import for Uri
import coil.compose.AsyncImage // Added Coil import
import coil.request.ImageRequest // Added Coil import

@Composable
fun WallpaperBackdrop(
    modifier: Modifier = Modifier,
    darkTheme: Boolean,
    wallpaperUri: Uri?, // Changed to accept Uri?
    overlay: @Composable BoxScope.() -> Unit = {}
) {
    val context = LocalContext.current

    val surfaceAlphaTarget = if (darkTheme) 0.78f else 0.66f
    val surfaceAlpha by animateFloatAsState(targetValue = surfaceAlphaTarget, label = "surfaceAlpha")

    Box(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
        if (wallpaperUri != null) {
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
