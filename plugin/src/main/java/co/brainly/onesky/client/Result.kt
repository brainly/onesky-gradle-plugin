package co.brainly.onesky.client

sealed class Result<T> {
    class Success<T>(val value: T) : Result<T>()
    class Failure<T>(val throwable: Throwable) : Result<T>()

    fun handle(
        onSuccess: (T) -> Unit,
        onFailure: (Throwable) -> Unit
    ): Unit = when (this) {
        is Success -> onSuccess(value)
        is Failure -> onFailure(throwable)
    }

    fun getOrNull(): T? = when (this) {
        is Failure -> null
        is Success -> value
    }

    fun errorOrNull(): Throwable? = when(this) {
        is Failure -> throwable
        is Success -> null
    }
}
