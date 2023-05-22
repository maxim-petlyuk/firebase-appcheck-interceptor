package io.appcheck.synchronizer.domain

import io.appcheck.synchronizer.data.AppCheckTokenExecutor

object AppCheckTokenProviderFactory {

    fun getAppCheckTokenProvider(
        appCheckTokenExecutor: AppCheckTokenExecutor,
        dispatchTimeoutMillis: Long = 0L,
        blockedTimeAfterError: Long = 0L
    ): AppCheckTokenProvider {
        val defaultAppCheckTokenProvider = DefaultAppCheckTokenProvider(
            appCheckTokenExecutor = appCheckTokenExecutor,
            blockedTimeAfterError = blockedTimeAfterError
        )

        return TimeoutAppCheckTokenProvider(
            appCheckTokenProvider = defaultAppCheckTokenProvider,
            dispatchTimeoutMillis = dispatchTimeoutMillis
        )
    }
}
