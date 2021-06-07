package co.brainly.onesky.util

import java.io.File

fun File.buildFile() = resolve("build.gradle.kts")
fun File.settingsFile() = resolve("settings.gradle")
