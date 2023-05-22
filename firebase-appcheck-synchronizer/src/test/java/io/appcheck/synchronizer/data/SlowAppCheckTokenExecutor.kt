package io.appcheck.synchronizer.data

import io.appcheck.synchronizer.domain.entity.FirebaseRequestTokenStrategy
import kotlinx.coroutines.delay

class SlowAppCheckTokenExecutor(
    private val delayTimeMillis: Long,
    private val mockToken: String = ""
) : AppCheckTokenExecutor {

    override suspend fun getToken(
        strategy: FirebaseRequestTokenStrategy
    ): Result<String> {
        delay(delayTimeMillis)
        return Result.success(mockToken)
    }
}
