package io.appcheck.interceptor

import android.util.Log
import io.appcheck.synchronizer.domain.AppCheckTokenProvider
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class FirebaseAppCheckInterceptor(
    private val appCheckTokenProvider: AppCheckTokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val decorateRequest = chain.request()
            .newBuilder()

        val appCheckTokenResult = runBlocking {
            appCheckTokenProvider.provideAppCheckToken()
        }
        val appCheckToken = appCheckTokenResult.getOrNull() ?: ""

        Log.i(
            "FirebaseAppCheck",
            "class: [FirebaseAppCheckInterceptor], " +
                "action: [result received, token: $appCheckToken], " +
                "thread: [${Thread.currentThread().id}}]"
        )

//        decorateRequest.addHeader(Api.Header.X_FIREBASE_APP_CHECK, appCheckToken)

        return chain.proceed(decorateRequest.build())
    }
}
