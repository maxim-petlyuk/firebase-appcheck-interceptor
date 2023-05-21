package io.appcheck.synchronizer.data

internal interface AppCheckTokenExecutor {

    suspend fun getToken(): Result<String>
}
