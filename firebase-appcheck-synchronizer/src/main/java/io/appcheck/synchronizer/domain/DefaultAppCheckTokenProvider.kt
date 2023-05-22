package io.appcheck.synchronizer.domain

import androidx.annotation.VisibleForTesting
import io.appcheck.synchronizer.data.AppCheckTokenExecutor
import io.appcheck.synchronizer.domain.entity.AppCheckState
import io.appcheck.synchronizer.domain.entity.FirebaseRequestTokenStrategy
import io.appcheck.synchronizer.exceptions.RetryNotAllowedException
import io.appcheck.synchronizer.exceptions.TokenExecutorServiceException
import io.appcheck.synchronizer.utils.Logger
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Class is designed to be used as singleton
 */
internal class DefaultAppCheckTokenProvider(
    private val appCheckTokenExecutor: AppCheckTokenExecutor,
    private val blockedTimeAfterError: Long = 0L
) : AppCheckTokenProvider {

    private var state: AppCheckState = AppCheckState.Idle
    private val mutex = Mutex()

    private val currentTimeMillis: Long
        get() = System.currentTimeMillis()

    @VisibleForTesting
    override val appCheckState: AppCheckState
        get() = state

    override suspend fun provideAppCheckToken(
        strategy: FirebaseRequestTokenStrategy
    ): Result<String> {
        return try {
            mutex.withLock {
                Logger.i(
                    "FirebaseAppCheck",
                    "class: [FirebaseAppCheckManager], " +
                        "action: [token request is executing], " +
                        "thread: [${Thread.currentThread().id}}]"
                )

                if (!allowRetry()) {
                    notifyStateChanged(generateRetryNotAllowedState())
                    return@withLock Result.failure(
                        RetryNotAllowedException("Retry is not allowed")
                    )
                }

                val tokenResult = appCheckTokenExecutor.getToken(strategy)
                    .onSuccess { token ->
                        notifyStateChanged(AppCheckState.Ready(token))
                    }.onFailure { exception ->
                        val errorState = AppCheckState.Error(
                            exception = exception,
                            timestampMillis = currentTimeMillis,
                            unblockRetryTime = calculateUnblockTime()
                        )
                        notifyStateChanged(errorState)
                    }

                val token = tokenResult.getOrNull()

                if (tokenResult.isFailure || token.isNullOrEmpty()) {
                    return@withLock Result.failure(
                        tokenResult.exceptionOrNull()
                            ?: TokenExecutorServiceException()
                    )
                }

                Result.success(token)
            }
        } catch (exception: TimeoutCancellationException) {
            val errorState = AppCheckState.Error(
                exception = exception,
                timestampMillis = currentTimeMillis,
                unblockRetryTime = calculateUnblockTime()
            )
            notifyStateChanged(errorState)
            Result.failure(exception)
        }
    }

    private fun allowRetry(): Boolean {
        return when (val currentState = state) {
            is AppCheckState.Error -> {
                currentTimeMillis >= currentState.unblockRetryTime
            }

            is AppCheckState.RetryNotAllowed -> {
                currentTimeMillis >= currentState.unblockRetryTime
            }

            else -> {
                true
            }
        }
    }

    private fun generateRetryNotAllowedState(): AppCheckState.RetryNotAllowed {
        return when (val currentState = state) {
            is AppCheckState.Error -> {
                AppCheckState.RetryNotAllowed(
                    originalException = currentState.exception,
                    unblockRetryTime = currentState.unblockRetryTime
                )
            }

            is AppCheckState.RetryNotAllowed -> {
                currentState
            }

            else -> {
                throw IllegalStateException("Current state: [$currentState], retry should be allowed")
            }
        }
    }

    private fun calculateUnblockTime(): Long {
        return System.currentTimeMillis() + blockedTimeAfterError
    }

    private fun notifyStateChanged(state: AppCheckState) {
        this.state = state

        Logger.i(
            "FirebaseAppCheck",
            "class: [FirebaseAppCheckManager], " +
                "action: [state changed: [$state] ], " +
                "thread: [${Thread.currentThread().id}}]"
        )
    }
}
