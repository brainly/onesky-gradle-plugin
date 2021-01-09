package co.brainly.onesky.task

import co.brainly.onesky.OneSkyPluginExtension
import co.brainly.onesky.client.OneSkyApiClient
import co.brainly.onesky.util.Constants
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.internal.logging.progress.ProgressLoggerFactory
import org.slf4j.LoggerFactory
import java.io.File
import javax.inject.Inject

open class UploadTranslationTask @Inject constructor(
    extension: OneSkyPluginExtension
) : DefaultTask() {

    private val projectId = extension.projectId
    private val files = extension.sourceStringFiles

    private val client = OneSkyApiClient(
        extension.apiKey,
        extension.apiSecret
    )

    private val logger = LoggerFactory.getLogger("uploadTranslations")

    private val progressLogger by lazy {
        services.get(ProgressLoggerFactory::class.java)
            .newOperation("Uploading translations")
    }

    override fun getDescription(): String {
        return "Uploads \"sourceStringFiles\" to OneSky"
    }

    override fun getGroup(): String {
        return Constants.TASK_GROUP
    }

    @TaskAction
    fun run() {
        progressLogger.start("Uploading translations", "")

        files.forEachIndexed { index, filename ->
            progressLogger.progress(
                "$filename (${index + 1}/${files.size})"
            )
            val valuesDir = project.androidResDir.resolve("values")
            val baseTranslationFile = File(valuesDir, filename)

            logger.warn(baseTranslationFile.absolutePath)

            val result = client.uploadTranslation(projectId, baseTranslationFile)
            result.handle(
                onSuccess = { /*do nothing*/ },
                onFailure = { error -> onUploadFailure(filename, error) }
            )
        }

        progressLogger.completed()
    }

    private fun onUploadFailure(filename: String, throwable: Throwable) {
        logger.warn(
            "Upload failed for $filename: ${throwable.message}"
        )
        throw TaskExecutionException(this, throwable)
    }

    private val Project.androidResDir: File
        get() = projectDir.resolve("src/main/res")
}
