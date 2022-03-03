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
    id("co.brainly.onesky") version "1.2.0"
}

android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "co.brainly.sample"
        minSdkVersion(21)
        targetSdkVersion(28)
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
    implementation(fileTree("libs").matching { include("*.jar") })
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.50")
    implementation("androidx.appcompat:appcompat:1.0.2")
    implementation("androidx.core:core-ktx:1.0.1")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("com.squareup.leakcanary:leakcanary-android:1.6.3")

    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.1")
}
