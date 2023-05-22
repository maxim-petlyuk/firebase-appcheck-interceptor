package io.appcheck.synchronizer.domain

import io.appcheck.synchronizer.data.FailureAppCheckTokenExecutor
import io.appcheck.synchronizer.domain.entity.AppCheckState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
internal class AppCheckRetryDelayTest {

    @Test
    fun `verify retry not allowed after error during blocked time`() {
        /* if error was happened, next retry attempt should be allowed after 5 seconds */
        val blockedTimeAfterError = 5_000L

        val appCheckTokenProvider = AppCheckTokenProviderFactory.getAppCheckTokenProvider(
            appCheckTokenExecutor = FailureAppCheckTokenExecutor(),
            blockedTimeAfterError = blockedTimeAfterError
        )

        runTest {
            appCheckTokenProvider.provideAppCheckToken()

            assert(appCheckTokenProvider.appCheckState is AppCheckState.Error) {
                "Initial token request, with mock failure behavior. We assume that provider should be in error state.\n" +
                    "Expected app check state: ${AppCheckState.Error::class.simpleName}, \n"
                "Actual app check state: ${appCheckTokenProvider.appCheckState::class.simpleName}"
            }

            appCheckTokenProvider.provideAppCheckToken()

            assert(appCheckTokenProvider.appCheckState is AppCheckState.RetryNotAllowed) {
                "Expected app check state: ${AppCheckState.RetryNotAllowed::class.simpleName}, \n"
                "Actual app check state: ${appCheckTokenProvider.appCheckState::class.simpleName}"
            }
        }
    }

    @Test
    fun `retry should be allowed after blocked time has been passed`() = runTest {
        /* if error was happened, next retry attempt should be allowed after 2 seconds */
        val blockedTimeAfterError = 2_000L

        val appCheckTokenProvider = AppCheckTokenProviderFactory.getAppCheckTokenProvider(
            appCheckTokenExecutor = FailureAppCheckTokenExecutor(),
            blockedTimeAfterError = blockedTimeAfterError,
        )

        appCheckTokenProvider.provideAppCheckToken()

        assert(appCheckTokenProvider.appCheckState is AppCheckState.Error) {
            "Initial token request, with mock failure behavior. We assume that provider should be in error state.\n" +
                "Expected app check state: ${AppCheckState.Error::class.simpleName}, \n"
            "Actual app check state: ${appCheckTokenProvider.appCheckState::class.simpleName}"
        }

        withContext(Dispatchers.IO) {
            delay(blockedTimeAfterError)

            appCheckTokenProvider.provideAppCheckToken()

            assert(appCheckTokenProvider.appCheckState is AppCheckState.Error) {
                "Second token request, we have wait for unblocking time, and now retry should be allowed. \n" +
                    "Actual app check state: ${appCheckTokenProvider.appCheckState::class.simpleName}"
            }
        }
    }

    @Test
    fun `retry should be allowed if blocked time is 0`() = runTest {
        /* if error was happened, next retry attempt should be allowed after 2 seconds */
        val blockedTimeAfterError = 0L

        val appCheckTokenProvider = AppCheckTokenProviderFactory.getAppCheckTokenProvider(
            appCheckTokenExecutor = FailureAppCheckTokenExecutor(),
            blockedTimeAfterError = blockedTimeAfterError
        )

        appCheckTokenProvider.provideAppCheckToken()

        assert(appCheckTokenProvider.appCheckState is AppCheckState.Error) {
            "Initial token request, with mock failure behavior. We assume that provider should be in error state.\n" +
                "Expected app check state: ${AppCheckState.Error::class.simpleName}, \n"
            "Actual app check state: ${appCheckTokenProvider.appCheckState::class.simpleName}"
        }

        withContext(Dispatchers.IO) {
            delay(blockedTimeAfterError)

            appCheckTokenProvider.provideAppCheckToken()

            assert(appCheckTokenProvider.appCheckState is AppCheckState.Error) {
                "Second token request, we have wait for unblocking time, and now retry should be allowed. \n" +
                    "Actual app check state: ${appCheckTokenProvider.appCheckState::class.simpleName}"
            }
        }
    }
}
