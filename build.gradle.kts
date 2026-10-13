plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.10" apply false
    alias(libs.plugins.ksp) apply false
}