import co.brainly.onesky.OneSkyPluginExtension

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("com.android.application")
    kotlin("android")
    id("co.brainly.onesky") version "1.4.0"
}

android {
    compileSdk = 33
    defaultConfig {
        applicationId = "co.brainly.sample"
        minSdk = 21
        targetSdk = 28
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

configure<OneSkyPluginExtension> {
    apiKey = "hCJXEQkNy3mRrFKnWJV1nowmjtWkJEcQ"
    apiSecret = "my-api-secret"
    projectId = 378760
    sourceStringFiles = listOf("strings.xml", "plurals.xml")
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.10")

    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.1")
}
