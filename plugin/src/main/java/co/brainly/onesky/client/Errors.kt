package co.brainly.onesky.client

import okio.IOException
import java.lang.RuntimeException

class NetworkException(cause: IOException) : RuntimeException(cause)

class HttpException(val code: Int, val errorMessage: String) : RuntimeException(
    "Http Code: $code - Message: $errorMessage"
)