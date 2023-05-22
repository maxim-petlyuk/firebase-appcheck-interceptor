package io.appcheck.synchronizer.data

interface AppCheckTokenExecutor {

    suspend fun getToken(): Result<String>
}
