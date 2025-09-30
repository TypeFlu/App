package me.typeflu.calculator.ui // Changed package

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import me.typeflu.calculator.ui.screens.CalculatorScreen // Updated import
import me.typeflu.calculator.ui.theme.CalculatorAppTheme // Updated import

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CalculatorAppTheme {
                CalculatorScreen()
            }
        }
    }
}
