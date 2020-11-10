package co.brainly.onesky.util

interface TimeProvider {
    fun currentTimeMillis(): Long
}

object SystemTimeProvider : TimeProvider {
    override fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}