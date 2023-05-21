package io.appcheck.synchronizer.domain.entity

internal sealed class AppCheckState {

    object Idle : AppCheckState() {

        override fun toString(): String {
            return "Idle"
        }
    }

    class Ready(
        val token: String
    ) : AppCheckState() {

        override fun toString(): String {
            return "Ready [$token]"
        }
    }

    class Error(
        val exception: Throwable,
        val timestampMillis: Long,
        val unblockRetryTime: Long
    ) : AppCheckState() {

        override fun toString(): String {
            return "Error: [${exception.message}]"
        }
    }

    class RetryNotAllowed(
        val originalException: Throwable,
        val unblockRetryTime: Long
    ) : AppCheckState() {

        override fun toString(): String {
            return "RetryNotAllowed: " +
                "error: [${originalException.message}], " +
                "current time: [${System.currentTimeMillis()}], " +
                "unblock time: [$unblockRetryTime]" +
                "left time in block: [${System.currentTimeMillis() - unblockRetryTime}]"
        }
    }
}
