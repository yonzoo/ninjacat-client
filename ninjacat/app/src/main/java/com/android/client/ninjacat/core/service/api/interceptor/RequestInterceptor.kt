package com.android.client.ninjacat.core.service.api.interceptor

import com.android.client.ninjacat.AppController
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * This interceptor adds authorization token to the request
 */
class RequestInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url
        val url = originalUrl.newBuilder()
            .build()

        var token = AppController.prefs.accessToken
        if (token == null) {
            token = ""
        }
        val requestBuilder = originalRequest.newBuilder().addHeader("Authorization", token).url(url)
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

