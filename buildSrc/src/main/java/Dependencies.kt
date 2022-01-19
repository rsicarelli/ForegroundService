object Dependencies {
    private const val androidXCoreVersion = "1.6.0"
    const val androidXCore = "androidx.core:core-ktx:$androidXCoreVersion"

    private const val androidXAppCompatVersion = "1.3.1"
    const val androidXAppCompat = "androidx.appcompat:appcompat:$androidXAppCompatVersion"

    private const val coroutinesVersion = "1.3.9"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion"

    private const val materialVersion = "1.4.0"
    const val material = "implementation 'com.google.android.material:material:$materialVersion"
}

object Plugins {
    const val androidApplication = "com.android.application"
    const val android = "android"
}

object Build {
    private const val androidBuildToolsVersion = "7.1.0-alpha03"
    const val androidBuildTools = "com.android.tools.build:gradle:$androidBuildToolsVersion"

    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Kotlin.version}"
}

object Kotlin {
    const val version = "1.5.20"
}

object Application {
    const val id = "com.rsicarelli.foregroundservice"
    const val minSdk = 21
    const val targetSdk = 31
    const val versionCode = 1
    const val versionName = "1.0"
    const val instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
}