package io.appcheck.synchronizer.data

import kotlinx.coroutines.delay

class SlowAppCheckTokenExecutor(
    private val delayTimeMillis: Long,
    private val mockToken: String = ""
) : AppCheckTokenExecutor {

    override suspend fun getToken(): Result<String> {
        delay(delayTimeMillis)
        return Result.success(mockToken)
    }
}
