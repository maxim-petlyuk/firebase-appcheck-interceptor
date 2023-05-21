package io.appcheck.synchronizer.domain

interface AppCheckTokenProvider {

    /**
     * Method will provide app-check token in case it's available.
     * If operation call to fetch token was failed - it will return empty string
     */
    suspend fun provideAppCheckToken(): String
}