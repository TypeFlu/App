import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class ApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        apply(plugin = "com.android.application")
        apply(plugin = "org.jetbrains.kotlin.android")

        // Configure Java toolchain to Java 21
        // This will also be used by Kotlin.
        // AGP 8.x and higher uses this to set source/target compatibility for Java
        // and Kotlin JVM target if not explicitly overridden by Kotlin extension.
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }

        // Specific Android Application settings can be configured here if needed,
        // but most (like compileSdk, minSdk, targetSdk, versionCode, versionName)
        // are now being driven by app/build.gradle.kts for clarity and simplicity
        // for a single-module app.
        extensions.configure<ApplicationExtension> {
            // Example: set a consistent JVM target for Dexing if needed, though toolchain should cover it.
            // compileOptions {
            //    sourceCompatibility = JavaVersion.VERSION_1_8 // Should align with toolchain or be removed
            //    targetCompatibility = JavaVersion.VERSION_1_8 // Should align with toolchain or be removed
            // }
        }

        // Kotlin specific configurations if needed beyond what the Java toolchain provides.
        // For example, explicit Kotlin JVM target if different from Java toolchain (not typical).
        // extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension> {
        //     jvmToolchain(21) // This is now typically inferred from Java toolchain in AGP 8+
        // }
    }
}
