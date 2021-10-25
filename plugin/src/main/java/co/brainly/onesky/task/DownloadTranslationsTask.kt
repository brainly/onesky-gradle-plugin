package co.brainly.onesky.task

import co.brainly.onesky.OneSkyPluginExtension
import co.brainly.onesky.client.Language
import co.brainly.onesky.client.LanguageListResponse
import co.brainly.onesky.client.OneSkyApiClient
import okio.buffer
import okio.sink
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.internal.logging.progress.ProgressLoggerFactory
import org.slf4j.LoggerFactory
import java.io.File
import javax.inject.Inject

open class DownloadTranslationsTask @Inject constructor(
    extension: OneSkyPluginExtension
) : DefaultTask() {

    private val projectId = extension.projectId
    private val files = extension.sourceStringFiles
    private val sourcePath = extension.sourcePath

    private val logger = LoggerFactory.getLogger("downloadTranslations")
    private val progressLogger by lazy {
        services.get(ProgressLoggerFactory::class.java)
            .newOperation("Downloading translations")
    }

    private val client = OneSkyApiClient(
        apiKey = extension.apiKey,
        apiSecret = extension.apiSecret,
        apiUrl = extension.oneSkyApiUrl
    )

    @TaskAction
    fun run() {
        progressLogger.start("Downloading translations", "")

        val languagesResult = client.fetchProjectLanguages(projectId)
        languagesResult.handle(
            onSuccess = ::downloadLanguages,
            onFailure = ::reportFailure
        )

        progressLogger.completed()
    }

    private fun downloadLanguages(languageListResponse: LanguageListResponse) {
        val languages = languageListResponse.data
            .filter { !it.is_base_language }

        val totalFiles = languages.size * files.size
        languages.forEachIndexed { languageIndex, language ->
            files.forEachIndexed { fileIndex, filename ->
                progressLogger.progress(
                    "${language.code}: $filename (${languageIndex * files.size + fileIndex}/$totalFiles)"
                )
                val translation = client.fetchTranslation(projectId, filename, language)
                translation.handle(
                    onSuccess = { saveTranslation(language, filename, it) },
                    onFailure = { reportTranslationFailure(language, filename, it) }
                )
            }
        }
    }

    private fun saveTranslation(language: Language, filename: String, translation: String) {
        if (translation.isBlank()) {
            logger.warn("$filename for ${language.english_name} is empty, skipping...")
            return
        }

        val languageDir = project.androidResDir.resolve("values-${language.androidDirName}")
        languageDir.mkdirs()

        val translationFile = File(languageDir, filename)
        translationFile.sink().buffer().use { sink ->
            sink.writeUtf8(translation)
        }
    }

    private fun reportTranslationFailure(
        language: Language,
        filename: String,
        throwable: Throwable
    ) {
        logger.warn(
            "Download failed for ${language.english_name} - ${language.code} ($filename), " +
                "reason: ${throwable.message}"
        )
    }

    private fun reportFailure(throwable: Throwable) {
        throw TaskExecutionException(this, throwable)
    }

    private val Language.androidDirName: String
        get() {
            if (code.contains("-")) {
                val (locale, region) = code.split("-")
                return "$locale-r$region"
            }

            // https://developer.android.com/reference/java/util/Locale.html#toLanguageTag()
            if (code.contains("id")) {
                return "in"
            }

            return code
        }

    private val Project.androidResDir: File
        get() = projectDir.resolve(sourcePath)
}
