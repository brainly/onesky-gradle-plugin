package co.brainly.onesky.client.util

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

fun MockWebServer.enqueueResponseWithFilesContent(resourceFilename: String) {
    val content = javaClass.getResource("/$resourceFilename")
        .readText()

    val response = MockResponse()
        .setResponseCode(200)
        .setBody(content)

    enqueue(response)
}
