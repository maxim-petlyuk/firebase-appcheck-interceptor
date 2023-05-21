package io.appcheck.synchronizer.domain

import io.appcheck.synchronizer.data.AppCheckTokenExecutor

class AppCheckTokenProviderFactory {

    internal fun getAppCheckTokenProvider(
        appCheckTokenExecutor: AppCheckTokenExecutor
    ): AppCheckTokenProvider {
        return DefaultAppCheckTokenProvider(
            appCheckTokenExecutor
        )
    }
}