package co.brainly.onesky.client

import co.brainly.onesky.util.SystemTimeProvider
import co.brainly.onesky.util.TimeProvider
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okio.ByteString.Companion.encodeUtf8
import java.util.concurrent.TimeUnit

private val ONESKY_API_URL = "https://platform.api.onesky.io/1/".toHttpUrl()

class OneSkyApiClient(
    private val apiKey: String,
    private val apiSecret: String,
    private val timeProvider: TimeProvider = SystemTimeProvider,
    private val baseUrl: HttpUrl = ONESKY_API_URL
) {

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

    fun fetchTranslation(projectId: Int, sourceFile: String, language: Language): Result<String> {
        val url = baseUrl.newBuilder()
            .addPathSegment("projects")
            .addPathSegment(projectId.toString())
            .addPathSegment("translations")
            .addAuthParams(apiKey, apiSecret)
            .addQueryParameter("locale", language.code)
            .addQueryParameter("source_file_name", sourceFile)
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
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
                    Result.Failure(HttpException(response.code, response.message))
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

