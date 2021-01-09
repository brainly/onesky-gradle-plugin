package co.brainly.onesky

import co.brainly.onesky.task.DownloadTranslationsTask
import co.brainly.onesky.task.TranslationsProgressTask
import co.brainly.onesky.task.UploadTranslationTask
import co.brainly.onesky.util.Constants
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @property apiKey API key found on OneSky account's settings page
 * @property apiSecret API secret found on OneSky account's settings page
 * @property projectId OneSky's project id for syncing files with
 * @property sourceStringFiles list of files to be synced with OneSky
 */
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
            .configure {
                group = Constants.TASK_GROUP
                description = "Downloads \"sourceStringFiles\" from OneSky"
            }

        tasks.register("downloadTranslations", DownloadTranslationsTask::class.java, extension)
            .configure {
                group = Constants.TASK_GROUP
                description = "Displays current progress from OneSky, skips already finished translations"
            }

        tasks.register("uploadTranslations", UploadTranslationTask::class.java, extension)
            .configure {
                group = Constants.TASK_GROUP
                description = "Uploads base \"sourceStringFiles\" to OneSky"
            }
    }
}
