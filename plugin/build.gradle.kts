import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "0.10.1"
    `maven-publish`
}

group = "co.brainly"
version = "1.1-SNAPSHOT"

gradlePlugin {
    plugins {
        register("onesky-gradle") {
            id = "co.brainly.onesky"
            displayName = "OneSky Gradle Plugin"
            description = "Sync your translations files with OneSky"
            implementationClass = "co.brainly.onesky.OneSkyPlugin"
        }
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        languageVersion = "1.4"
        freeCompilerArgs = freeCompilerArgs + listOf("-Xopt-in=kotlin.contracts.ExperimentalContracts")
    }
}

repositories {
    jcenter()
}

dependencies {
    compileOnly(gradleApi())

    val okHttp = "4.9.0"
    implementation("com.squareup.okhttp3:okhttp:$okHttp")
    val moshi = "1.11.0"
    implementation("com.squareup.moshi:moshi:$moshi")
    implementation("com.squareup.moshi:moshi-kotlin:$moshi")

    implementation("com.jakewharton.picnic:picnic:0.4.0") {
        because("0.5.0 crashes on Table.renderText() call")
    }

    testImplementation("junit:junit:4.12")
    testImplementation("com.squareup.okhttp3:mockwebserver:$okHttp")

}
