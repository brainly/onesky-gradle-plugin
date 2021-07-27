package co.brainly.onesky.task

import co.brainly.onesky.client.util.enqueueResponseWithFilesContent
import co.brainly.onesky.util.buildFile
import co.brainly.onesky.util.settingsFile
import okhttp3.mockwebserver.MockWebServer
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import org.junit.jupiter.api.Assertions.assertEquals as assertEquals

class DownloadTranslationsTaskTest {

    private val server = MockWebServer()

    @Test
    fun `uses values-in directory for Indonesian locale (id) `(@TempDir tempDir: File) {
        val projectDir = tempDir.resolve("onesky-project").apply { mkdirs() }
        val moduleDir = projectDir.resolve("my-module").apply { mkdirs() }

        projectDir.settingsFile().writeText(
            """
                include ":my-module"
            """.trimIndent()
        )
        val serverUrl = server.url("/")
        moduleDir.buildFile().writeText(
            """
                import co.brainly.onesky.OneSkyPluginExtension
                
                buildscript {
                    repositories {
                        mavenLocal()
                        mavenCentral()
                        gradlePluginPortal()
                    }
                }
                
                plugins {
                    id("co.brainly.onesky")
                }
                
                configure<OneSkyPluginExtension> {
                    apiKey = "???"
                    apiSecret = "???"
                    projectId = 378760
                    sourceStringFiles = listOf("strings.xml")
                    overrideOneSkyApiUrl("$serverUrl")
                }
                  
            """.trimIndent()
        )

        server.enqueueResponseWithFilesContent("project_languages_response_just_id.json")
        server.enqueueResponseWithFilesContent("example_translation_file.xml")

        GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments(":my-module:downloadTranslations")
            .build()

        assertEquals(
            false,
            moduleDir.resolve("src/main/res/values-id/strings.xml").exists(),
            "files not created in values-id directory"
        )

        assertEquals(
            true,
            moduleDir.resolve("src/main/res/values-in/strings.xml").exists(),
            "files created in values-in directory"
        )
    }
}
