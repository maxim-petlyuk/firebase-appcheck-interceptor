package io.appcheck.synchronizer.data

import com.google.android.gms.tasks.Task
import com.google.firebase.appcheck.AppCheckToken
import com.google.firebase.appcheck.FirebaseAppCheck
import io.appcheck.synchronizer.domain.entity.FirebaseRequestTokenStrategy
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

class FirebaseAppCheckTokenExecutor : AppCheckTokenExecutor {

    private val firebaseAppCheck = FirebaseAppCheck.getInstance()
    private val mutex = Mutex()

    override suspend fun getToken(
        strategy: FirebaseRequestTokenStrategy
    ): Result<String> {
        return mutex.withLock {
            executeTokenRequest(getAppCheckTokenTask(strategy))
        }
    }

    private fun getAppCheckTokenTask(strategy: FirebaseRequestTokenStrategy): Task<AppCheckToken> {
        return when (strategy) {
            is FirebaseRequestTokenStrategy.Basic -> {
                firebaseAppCheck.getAppCheckToken(strategy.refresh)
            }

            is FirebaseRequestTokenStrategy.Limited -> {
                firebaseAppCheck.limitedUseAppCheckToken
            }
        }
    }

    private suspend fun executeTokenRequest(tokenTask: Task<AppCheckToken>): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            tokenTask.addOnSuccessListener { appCheckResult ->
                val token = appCheckResult.token

                if (!continuation.isCancelled) {
                    continuation.resume(Result.success(token))
                }
            }.addOnFailureListener { exception ->
                if (!continuation.isCancelled) {
                    continuation.resume(Result.failure(exception))
                }
            }
        }
    }
}
