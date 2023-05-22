package io.appcheck.synchronizer.domain

import androidx.annotation.VisibleForTesting
import io.appcheck.synchronizer.domain.entity.AppCheckState
import io.appcheck.synchronizer.domain.entity.FirebaseRequestTokenStrategy
import kotlinx.coroutines.withTimeout

internal class TimeoutAppCheckTokenProvider(
    private val appCheckTokenProvider: AppCheckTokenProvider,
    private val dispatchTimeoutMillis: Long = 0L
) : AppCheckTokenProvider {

    @VisibleForTesting
    override val appCheckState: AppCheckState
        get() = appCheckTokenProvider.appCheckState

    override suspend fun provideAppCheckToken(
        strategy: FirebaseRequestTokenStrategy
    ): Result<String> {
        return try {
            if (dispatchTimeoutMillis <= 0L) {
                appCheckTokenProvider.provideAppCheckToken(strategy)
            } else {
                withTimeout(dispatchTimeoutMillis) {
                    appCheckTokenProvider.provideAppCheckToken(strategy)
                }
            }
        } catch (error: Throwable) {
            Result.failure(error)
        }
    }
}
