package io.appcheck.synchronizer.domain

import kotlinx.coroutines.withTimeout

internal class TimeoutAppCheckTokenProvider(
    private val appCheckTokenProvider: AppCheckTokenProvider,
    private val dispatchTimeoutMillis: Long = 0L
) : AppCheckTokenProvider {

    override suspend fun provideAppCheckToken(): Result<String> {
        return try {
            if (dispatchTimeoutMillis <= 0L) {
                appCheckTokenProvider.provideAppCheckToken()
            } else {
                withTimeout(dispatchTimeoutMillis) {
                    appCheckTokenProvider.provideAppCheckToken()
                }
            }
        } catch (error: Throwable) {
            Result.failure(error)
        }
    }
}
