package com.savet.network.manager

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response;


class RequestInterceptor(private val mNetworkRequestInfo: NetworkRequestInfo) : Interceptor {
    companion object {
        const val TAG = "RequestInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {

        val builder: Request.Builder = chain.request().newBuilder()
        // 所有都添加进去
        for (key in mNetworkRequestInfo.headerMap.keys) {
            val value: String? = mNetworkRequestInfo.headerMap[key]
            if (!value.isNullOrEmpty()) {
                builder.addHeader(key, value)
            }
        }
        return chain.proceed(builder.build())
    }

}