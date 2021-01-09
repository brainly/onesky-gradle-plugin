package co.brainly.onesky.client

import okhttp3.Request
import okio.IOException
import java.lang.RuntimeException

class HttpException(
    code: Int,
    errorMessage: String,
    errorBody: String
) : RuntimeException(
    "Http Code: $code - $errorMessage\nBody: $errorBody"
)
