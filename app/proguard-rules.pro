####################################################################
# T-Calculator ProGuard configuration
# Keep critical Compose and lifecycle types to avoid runtime issues.
####################################################################

# Preserve our app classes and entry points.
-keep class me.typeflu.calculator.** { *; }

# Compose runtime relies on reflection for certain generated call stubs.
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class kotlinx.coroutines.** { *; }

# Prevent warnings for Kotlin metadata and coroutines debug instruments.
-dontwarn androidx.compose.**
-dontwarn kotlinx.coroutines.**

# Retain annotations that Compose uses to generate tooling previews.
-keepattributes *Annotation*
