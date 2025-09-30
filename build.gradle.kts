plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false // Keep for now, in case you add library modules later
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.jvm) apply false
}

tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}
