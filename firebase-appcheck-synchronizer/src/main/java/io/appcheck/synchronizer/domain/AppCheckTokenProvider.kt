package io.appcheck.synchronizer.domain

import androidx.annotation.VisibleForTesting
import io.appcheck.synchronizer.domain.entity.AppCheckState
import io.appcheck.synchronizer.domain.entity.FirebaseRequestTokenStrategy

interface AppCheckTokenProvider {

    @VisibleForTesting
    val appCheckState: AppCheckState

    /**
     * Method will provide app-check token in case it's available.
     * If operation call to fetch token was failed - it will return empty string
     */
    suspend fun provideAppCheckToken(strategy: FirebaseRequestTokenStrategy): Result<String>
}
