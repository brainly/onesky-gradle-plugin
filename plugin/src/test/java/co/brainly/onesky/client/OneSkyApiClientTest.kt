package co.brainly.onesky.client

import co.brainly.onesky.client.util.enqueueResponseWithFilesContent
import co.brainly.onesky.util.TimeProvider
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class OneSkyApiClientTest {

    private val server = MockWebServer()

    private val projectId = 41994
    private val client = OneSkyApiClient(
        "my-api-key",
        "my-api-secret",
        apiUrl = server.url("/").toString(),
        timeProvider = object : TimeProvider {
            override fun currentTimeMillis(): Long {
                return 12345L
            }
        }
    )

    @Test
    fun `downloads list of project's languages`() {
        server.enqueueResponseWithFilesContent("project_languages_response.json")

        val response = client.fetchProjectLanguages(projectId)

        val request = server.takeRequest()
        assertEquals(
            "/projects/41994/languages?api_key=my-api-key&timestamp=12&dev_hash=28dac32cc9ee8ab264d35087653be23e",
            request.path
        )

        val expected = listOf(
            Language("en", "English", true, "100.0%"),
            Language("fr", "French", false, "100.0%"),
            Language("ru", "Russian", false, "98.1%"),
            Language("tr", "Turkish", false, "100.0%"),
            Language("pl", "Polish", false, "100.0%"),
            Language("ro", "Romanian", false, "100.0%"),
            Language("kn-IN", "Kannada (India)", false, "0.0%"),
            Language("hi-IN", "Hindi (India)", false, "98.1%"),
            Language("ta-IN", "Tamil (India)", false, "0.0%"),
            Language("uk", "Ukrainian", false, "99.2%"),
            Language("te-IN", "Telugu (India)", false, "0.0%"),
            Language("id", "Indonesian", false, "99.8%"),
            Language("ms", "Malay", false, "0.0%"),
            Language("pa", "Punjabi", false, "0.1%"),
            Language("bn-IN", "Bengali (India)", false, "0.0%"),
            Language("ml-IN", "Malayalam (India)", false, "98.1%"),
            Language("pt-BR", "Portuguese (Brazil)", false, "99.9%"),
            Language("en-IN", "English (India)", false, "0.0%"),
            Language("mr", "Marathi", false, "96.9%"),
            Language("gu-IN", "Gujarati (India)", false, "0.0%"),
            Language("or-IN", "Oriya (India)", false, "100.0%"),
            Language("bn", "Bengali", false, "100.0%"),
            Language("ta", "Tamil", false, "100.0%"),
            Language("ur-IN", "Urdu (India)", false, "0.0%"),
            Language("en-US", "English (United States)", false, "0.0%"),
            Language("pa-IN", "Punjabi (India)", false, "93.8%"),
            Language("es-ES", "Spanish (Spain)", false, "100.0%"),
            Language("hi", "Hindi", false, "98.1%"),
            Language("gu", "Gujarati", false, "96.6%"),
            Language("kn", "Kannada", false, "90.9%"),
            Language("ml", "Malayalam", false, "0.0%"),
            Language("or", "Oriya", false, "0.0%"),
            Language("te", "Telugu", false, "98.1%")
        )

        assertEquals(expected, response.getOrNull()?.data)
    }

    @Test
    fun `downloads a translation file`() {
        server.enqueueResponseWithFilesContent("example_translation_file.xml")

        val language = Language("fr", "French", false, "100.0%")
        val translationResult = client.fetchTranslation(projectId, "strings.xml", language)

        assertEquals(
            """
                <?xml version="1.0" encoding="UTF-8"?>
                <resources>
                    <!-- Name of the application -->
                    <string name="app_name">Nosdevoirs.fr</string>
                    <!-- Name of the shortcut for memory leaks info -->
                    <!-- error informing the user that he/she doesnt have ticket -->
                    <string name="error_task_view_no_ticket">Désolé, une erreur est survenue. Réessaye plus tard.
                    </string>
                    <!-- error informing the user that the question he opened was deleted -->
                    <string name="error_task_view_missing_task">La question a été supprimée</string>
                </resources>

            """.trimIndent(),
            translationResult.getOrNull()
        )
    }

    @Test
    fun `uploads a translation file`() {
        val file = File.createTempFile("onesky", ".tmp")
        file.writeText("Hello OneSky Gradle Plugin")

        client.uploadTranslation(projectId, file, deprecateStrings = false)

        val request = server.takeRequest()

        assertEquals(
            "/projects/41994/files?api_key=my-api-key&timestamp=12" +
                "&dev_hash=28dac32cc9ee8ab264d35087653be23e&file_format=ANDROID_XML&is_keeping_all_strings=true",
            request.path
        )

        assertEquals(
            "POST",
            request.method
        )

        assertEquals(
            """
            --onesky-gradle-plugin-file
            Content-Disposition: form-data; name="file"; filename="${file.name}"
            Content-Type: application/octet-stream
            Content-Length: 26

            Hello OneSky Gradle Plugin
            --onesky-gradle-plugin-file--

            """.trimIndent().replace(
                "\n",
                "\r\n"
            ), // to avoid conflicts with OkHttp
            request.body.readByteString().utf8()
        )
    }

    @Test
    fun `uploads a translation file and deprecate old strings`() {
        val file = File.createTempFile("onesky", ".tmp")
        file.writeText("Hello OneSky Gradle Plugin")

        client.uploadTranslation(projectId, file, deprecateStrings = true)

        val request = server.takeRequest()

        assertEquals(
            "/projects/41994/files?api_key=my-api-key&timestamp=12" +
                "&dev_hash=28dac32cc9ee8ab264d35087653be23e&file_format=ANDROID_XML&is_keeping_all_strings=false",
            request.path
        )

        assertEquals(
            "POST",
            request.method
        )

        assertEquals(
            """
            --onesky-gradle-plugin-file
            Content-Disposition: form-data; name="file"; filename="${file.name}"
            Content-Type: application/octet-stream
            Content-Length: 26

            Hello OneSky Gradle Plugin
            --onesky-gradle-plugin-file--

            """.trimIndent().replace(
                "\n",
                "\r\n"
            ), // to avoid conflicts with OkHttp
            request.body.readByteString().utf8()
        )
    }
}
