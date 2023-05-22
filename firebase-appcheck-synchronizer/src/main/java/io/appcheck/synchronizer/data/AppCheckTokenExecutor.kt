package io.appcheck.synchronizer.data

import io.appcheck.synchronizer.domain.entity.FirebaseRequestTokenStrategy

interface AppCheckTokenExecutor {

    suspend fun getToken(strategy: FirebaseRequestTokenStrategy): Result<String>
}
