package co.brainly.onesky.client

import co.brainly.onesky.util.SystemTimeProvider
import co.brainly.onesky.util.TimeProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okio.ByteString.Companion.encodeUtf8
import java.io.File
import java.util.concurrent.TimeUnit

internal const val ONESKY_API_URL = "https://platform.api.onesky.io/1/"

class OneSkyApiClient(
    private val apiKey: String,
    private val apiSecret: String,
    private val timeProvider: TimeProvider = SystemTimeProvider,
    apiUrl: String
) {

    private val baseUrl = apiUrl.toHttpUrl()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .build()

    fun fetchProjectLanguages(projectId: Int): Result<LanguageListResponse> {
        val url = baseUrl.newBuilder()
            .addPathSegment("projects")
            .addPathSegment(projectId.toString())
            .addPathSegment("languages")
            .addAuthParams(apiKey, apiSecret)
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        return fetch(request)
    }

    fun fetchTranslation(
        projectId: Int,
        sourceFile: String,
        language: Language,
        fileNamePrefix: String? = null
    ): Result<String> {
        val sourceFileName = if (fileNamePrefix != null) {
            "$fileNamePrefix-$sourceFile"
        } else {
            sourceFile
        }

        val url = baseUrl.newBuilder()
            .addPathSegment("projects")
            .addPathSegment(projectId.toString())
            .addPathSegment("translations")
            .addAuthParams(apiKey, apiSecret)
            .addQueryParameter("locale", language.code)
            .addQueryParameter("source_file_name", sourceFileName)
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        return fetch(request)
    }

    fun uploadTranslation(
        projectId: Int,
        file: File,
        deprecateStrings: Boolean,
        fileNamePrefix: String? = null
    ): Result<String> {
        val isKeepingAllStrings = if (deprecateStrings) {
            "false"
        } else {
            "true"
        }

        val url = baseUrl.newBuilder()
            .addPathSegment("projects")
            .addPathSegment(projectId.toString())
            .addPathSegment("files")
            .addAuthParams(apiKey, apiSecret)
            .addQueryParameter("file_format", "ANDROID_XML")
            .addQueryParameter("is_keeping_all_strings", isKeepingAllStrings)
            .build()

        val fileName = if (fileNamePrefix != null) {
            "$fileNamePrefix-${file.name}"
        } else {
            file.name
        }
        val body = MultipartBody.Builder(boundary = "onesky-gradle-plugin-file")
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", fileName, file.asRequestBody("application/octet-stream".toMediaTypeOrNull()))
            .build()

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        return fetch(request)
    }

    private inline fun <reified T> fetch(request: Request): Result<T> {
        try {
            okHttpClient.newCall(request).execute().use { response ->
                return if (response.isSuccessful) {
                    val data = if (T::class.java.isAssignableFrom(String::class.java)) {
                        response.body!!.string() as T
                    } else {
                        val adapter = moshi.adapter(T::class.java)
                        adapter.fromJson(response.body!!.source()) as T
                    }
                    Result.Success(data)
                } else {
                    val errorBody = response.body?.string() ?: ""
                    Result.Failure(HttpException(response.code, response.message, errorBody))
                }
            }
        } catch (e: Exception) {
            return Result.Failure(e)
        }
    }

    private fun HttpUrl.Builder.addAuthParams(key: String, secret: String): HttpUrl.Builder {
        addQueryParameter("api_key", key)

        val timestamp = (timeProvider.currentTimeMillis() / 1000L).toString()
        addQueryParameter("timestamp", timestamp)

        val devHash = (timestamp + secret).encodeUtf8().md5().hex()
        addQueryParameter("dev_hash", devHash)

        return this
    }
}
