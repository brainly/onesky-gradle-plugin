package co.brainly.onesky

import co.brainly.onesky.client.ONESKY_API_URL
import co.brainly.onesky.task.DEPRECATE_STRINGS_FLAG
import co.brainly.onesky.task.DownloadTranslationsTask
import co.brainly.onesky.task.TranslationsProgressTask
import co.brainly.onesky.task.UploadTranslationTask
import co.brainly.onesky.util.Constants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.annotations.TestOnly

/**
 * @property apiKey API key found on OneSky account's settings page
 * @property apiSecret API secret found on OneSky account's settings page
 * @property projectId OneSky's project id for syncing files with
 * @property sourceStringFiles list of files to be synced with OneSky
 * @property downloadLanguages list of languages to be downloaded, an empty list will download all available languages
 * @property downloadBaseLanguage Determines if the plugin should download & replace the base language or not
 * @property moduleName appends a prefix to all [sourceStringFiles] for multi-module support, use null to disable
 */
open class OneSkyPluginExtension(
    var verbose: Boolean = false,
    var apiKey: String = "",
    var apiSecret: String = "",
    var projectId: Int = -1,
    var sourceStringFiles: List<String> = emptyList(),
    var sourcePath: String = "src/main/res",
    var downloadLanguages: List<String> = emptyList(),
    var downloadBaseLanguage: Boolean = false,
    var moduleName: String? = null
) {
    internal var oneSkyApiUrl: String = ONESKY_API_URL

    @TestOnly
    fun overrideOneSkyApiUrl(oneSkyApiUrl: String) {
        this.oneSkyApiUrl = oneSkyApiUrl
    }
}

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

        val deprecateStrings = project.hasProperty(DEPRECATE_STRINGS_FLAG)
        tasks.register("uploadTranslations", UploadTranslationTask::class.java, extension, deprecateStrings)
            .configure {
                group = Constants.TASK_GROUP
                description = "Uploads base \"sourceStringFiles\" to OneSky"
            }
    }
}
