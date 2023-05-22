package io.appcheck.synchronizer.data

import kotlinx.coroutines.delay

class FailureAppCheckTokenExecutor : AppCheckTokenExecutor {

    override suspend fun getToken(): Result<String> {
        delay(1000L)
        return Result.failure(IllegalStateException("Design of this app check token source is to failure every time"))
    }
}
