package me.typeflu.calculator

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import me.typeflu.calculator.ui.CalculatorRoute
import me.typeflu.calculator.ui.theme.TCalculatorTheme
import android.graphics.Color as AndroidColor

class MainActivity : ComponentActivity() {

    private var wallpaperUri by mutableStateOf<Uri?>(null)

    private val pickWallpaperLauncher =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri: Uri? ->
            wallpaperUri = uri
        }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
        )

        setContent {
            TCalculatorTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
                    val windowSizeClass = calculateWindowSizeClass(this)
                    CalculatorRoute(
                        windowSizeClass = windowSizeClass,
                        wallpaperUri = wallpaperUri,
                        onSelectWallpaper = { pickWallpaperLauncher.launch("image/*") }
                    )
                }
            }
        }
    }
}