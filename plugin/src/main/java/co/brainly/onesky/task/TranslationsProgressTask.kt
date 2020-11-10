package co.brainly.onesky.task

import co.brainly.onesky.OneSkyPluginExtension
import co.brainly.onesky.client.LanguageListResponse
import co.brainly.onesky.client.OneSkyApiClient
import com.jakewharton.picnic.*
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import javax.inject.Inject

open class TranslationsProgressTask @Inject constructor(
    extension: OneSkyPluginExtension
) : DefaultTask() {

    private val projectId = extension.projectId

    private val client = OneSkyApiClient(
        extension.apiKey,
        extension.apiSecret
    )

    @TaskAction
    fun run() {
        val response = client.fetchProjectLanguages(projectId)

        response.handle(
            onSuccess = ::displayProgress,
            onFailure = ::reportFailure
        )
    }

    private fun displayProgress(languageListResponse: LanguageListResponse) {
        val languages = languageListResponse.data
            .filter { !it.is_base_language }
            .map {
                val progress = it.translation_progress.removeSuffix("%").toFloatOrNull() ?: 0f
                LanguageProgress(it.english_name, progress)
            }
            .filter { it.progress < 100 }
            .sortedByDescending { it.progress }

        renderProgressTable(languages)
    }

    private fun renderProgressTable(languages: List<LanguageProgress>) {
        if (languages.isEmpty()) {
            println("All translations are done!")
            return
        }

        val table = table {
            cellStyle {
                border = true
            }
            header {
                row("Language", "Progress")
            }
            languages.forEach {
                row(it.name, it.progress)
            }
        }

        println(table.renderText())
    }

    private fun reportFailure(throwable: Throwable) {
        throw TaskExecutionException(this, throwable)
    }
}

private class LanguageProgress(val name: String, val progress: Float)

