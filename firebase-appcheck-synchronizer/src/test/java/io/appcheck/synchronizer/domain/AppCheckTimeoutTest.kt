package io.appcheck.synchronizer.domain

import io.appcheck.synchronizer.TestCoroutineRule
import io.appcheck.synchronizer.data.SlowAppCheckTokenExecutor
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

    @Test
    fun `verify dispatch timeout for app token provider`() {
        val responseDelay = 5_000L // 5 seconds
        val dispatchTimeout = 2_000L // 2 seconds

        val timeoutFailureToken = "UniqueFailureToken"

        val slowAppCheckSource = SlowAppCheckTokenExecutor(responseDelay)
        val appCheckTokenProvider = TimeoutAppCheckTokenProvider(
            appCheckTokenProvider = DefaultAppCheckTokenProvider(slowAppCheckSource),
            dispatchTimeoutMillis = dispatchTimeout,
            failureToken = timeoutFailureToken
        )

        runTest {
            val token = appCheckTokenProvider.provideAppCheckToken()
            assert(token == timeoutFailureToken) {
                "Dispatch timeout is over, function call should return hardcoded failure result: $timeoutFailureToken"
            }
        }
    }

    @Test
    fun `verify successful dispatch before timeout interruption`() {
        val responseDelay = 2_000L // 5 seconds
        val dispatchTimeout = 5_000L // 2 seconds

        val timeoutFailureToken = "UniqueFailureToken"
        val mockSuccessToken = "MockToken"

        val slowAppCheckSource = SlowAppCheckTokenExecutor(
            delayTimeMillis = responseDelay,
            mockToken = mockSuccessToken
        )
        val appCheckTokenProvider = TimeoutAppCheckTokenProvider(
            appCheckTokenProvider = DefaultAppCheckTokenProvider(slowAppCheckSource),
            dispatchTimeoutMillis = dispatchTimeout,
            failureToken = timeoutFailureToken
        )

        runTest {
            val token = appCheckTokenProvider.provideAppCheckToken()
            assert(token == mockSuccessToken) {
                "Dispatch timeout is over, function call should return hardcoded failure result: $timeoutFailureToken"
            }
        }
    }

    @Test
    fun `if timeout is 0, dispatching timeout exception should be turned off`() {
        val responseDelay = 2_000L // 5 seconds
        val dispatchTimeout = 0L

        val timeoutFailureToken = "UniqueFailureToken"
        val mockSuccessToken = "MockToken"

        val slowAppCheckSource = SlowAppCheckTokenExecutor(
            delayTimeMillis = responseDelay,
            mockToken = mockSuccessToken
        )
        val appCheckTokenProvider = TimeoutAppCheckTokenProvider(
            appCheckTokenProvider = DefaultAppCheckTokenProvider(slowAppCheckSource),
            dispatchTimeoutMillis = dispatchTimeout,
            failureToken = timeoutFailureToken
        )

        runTest {
            val token = appCheckTokenProvider.provideAppCheckToken()
            assert(token == mockSuccessToken) {
                "Dispatch timeout is over, function call should return hardcoded failure result: $timeoutFailureToken"
            }
        }
    }

    @Test
    fun `verify if scope has been canceled`() = runTest(NonCancellable) {
        val responseDelay = 5_000L // 10 seconds
        val dispatchTimeout = 3_000L // 3 seconds

        val timeoutFailureToken = "UniqueFailureToken"
        val mockSuccessToken = "MockToken"

        val slowAppCheckSource = SlowAppCheckTokenExecutor(
            delayTimeMillis = responseDelay,
            mockToken = mockSuccessToken
        )
        val appCheckTokenProvider = TimeoutAppCheckTokenProvider(
            appCheckTokenProvider = DefaultAppCheckTokenProvider(slowAppCheckSource),
            dispatchTimeoutMillis = dispatchTimeout,
            failureToken = timeoutFailureToken
        )

        val fetchTokenScope = launch(Dispatchers.Default) {
            val token = appCheckTokenProvider.provideAppCheckToken()

            assert(token == timeoutFailureToken) {
                "Job was cancelled and we expect failure backup token: $timeoutFailureToken"
            }
        }

        withContext(Dispatchers.IO) {
            delay(1500L)
            fetchTokenScope.cancel()
        }
    }
}
