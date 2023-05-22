package io.appcheck.synchronizer.domain

import io.appcheck.synchronizer.TestCoroutineRule
import io.appcheck.synchronizer.data.SlowAppCheckTokenExecutor
import io.appcheck.synchronizer.domain.entity.FirebaseRequestTokenStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
internal class AppCheckTimeoutTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val mockTokenStrategy = FirebaseRequestTokenStrategy.Limited

    @Test
    fun `verify dispatch timeout for app token provider`() {
        val responseDelay = 5_000L // 5 seconds
        val dispatchTimeout = 2_000L // 2 seconds

        val appCheckTokenProvider = AppCheckTokenProviderFactory.getAppCheckTokenProvider(
            appCheckTokenExecutor = SlowAppCheckTokenExecutor(responseDelay),
            dispatchTimeoutMillis = dispatchTimeout
        )

        runTest {
            val tokenResult = appCheckTokenProvider.provideAppCheckToken(mockTokenStrategy)
            assert(tokenResult.isFailure) {
                "Dispatch timeout is over, function call should return failure result"
            }
        }
    }

    @Test
    fun `verify successful dispatch before timeout interruption`() {
        val responseDelay = 2_000L // 5 seconds
        val dispatchTimeout = 5_000L // 2 seconds

        val mockSuccessToken = "MockToken"

        val appCheckTokenProvider = AppCheckTokenProviderFactory.getAppCheckTokenProvider(
            appCheckTokenExecutor = SlowAppCheckTokenExecutor(
                delayTimeMillis = responseDelay,
                mockToken = mockSuccessToken
            ),
            dispatchTimeoutMillis = dispatchTimeout
        )

        runTest {
            val tokenResult = appCheckTokenProvider.provideAppCheckToken(mockTokenStrategy)

            assert(tokenResult.isSuccess) {
                "Dispatch timeout is over, function call should return success result"
            }

            assert(tokenResult.getOrNull() == mockSuccessToken) {
                "Dispatch timeout is over, function call should return expected mock token: " +
                    "[${mockSuccessToken}], but was [${tokenResult.getOrNull()}]"
            }
        }
    }

    @Test
    fun `if timeout is 0, dispatching timeout exception should be turned off`() {
        val responseDelay = 2_000L // 5 seconds
        val dispatchTimeout = 0L

        val mockSuccessToken = "MockToken"

        val appCheckTokenProvider = AppCheckTokenProviderFactory.getAppCheckTokenProvider(
            appCheckTokenExecutor = SlowAppCheckTokenExecutor(
                delayTimeMillis = responseDelay,
                mockToken = mockSuccessToken
            ),
            dispatchTimeoutMillis = dispatchTimeout
        )

        runTest {
            val tokenResult = appCheckTokenProvider.provideAppCheckToken(mockTokenStrategy)

            assert(tokenResult.isSuccess) {
                "Dispatch timeout is over, function call should return hardcoded success result"
            }

            assert(tokenResult.getOrNull() == mockSuccessToken) {
                "Dispatch timeout is over, function call should return hardcoded success result: " +
                    "[${mockSuccessToken}], but was [${tokenResult.getOrNull()}]"
            }
        }
    }

    @Test
    fun `verify if scope has been canceled`() = runTest(NonCancellable) {
        val responseDelay = 5_000L // 10 seconds
        val dispatchTimeout = 3_000L // 3 seconds

        val mockSuccessToken = "MockToken"

        val appCheckTokenProvider = AppCheckTokenProviderFactory.getAppCheckTokenProvider(
            appCheckTokenExecutor = SlowAppCheckTokenExecutor(
                delayTimeMillis = responseDelay,
                mockToken = mockSuccessToken
            ),
            dispatchTimeoutMillis = dispatchTimeout
        )

        val fetchTokenScope = launch(Dispatchers.Default) {
            val tokenResult = appCheckTokenProvider.provideAppCheckToken(mockTokenStrategy)

            assert(tokenResult.isFailure) {
                "Job was cancelled and we expect failure result"
            }
        }

        withContext(Dispatchers.IO) {
            delay(1500L)
            fetchTokenScope.cancel()
        }
    }
}
