package co.brainly.onesky

import co.brainly.onesky.task.DownloadTranslationsTask
import co.brainly.onesky.task.TranslationsProgressTask
import org.gradle.api.Plugin
import org.gradle.api.Project

open class OneSkyPluginExtension(
    var verbose: Boolean = false,
    var apiKey: String = "",
    var apiSecret: String = "",
    var projectId: Int = -1,
    var sourceStringFiles: List<String> = emptyList()
)

class OneSkyPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        val extension = extensions.create("OneSkyPluginExtension", OneSkyPluginExtension::class.java)
        tasks.register("translationsProgress", TranslationsProgressTask::class.java, extension)
        tasks.register("downloadTranslations", DownloadTranslationsTask::class.java, extension)
    }
}
