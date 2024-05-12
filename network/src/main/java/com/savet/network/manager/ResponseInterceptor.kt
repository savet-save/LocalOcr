package com.savet.network.manager

import okhttp3.Interceptor
import okhttp3.Request;
import okhttp3.Response;

/**
 * Response统一处理
 */
class ResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        return chain.proceed(request)
    }
}