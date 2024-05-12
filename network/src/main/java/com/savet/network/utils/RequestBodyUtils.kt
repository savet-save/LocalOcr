package com.savet.network.utils

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object RequestBodyUtils {

    private val mediaType: MediaType? by lazy {
        "application/json; charset=utf-8".toMediaTypeOrNull()
    }

    /**
     * 根据json文本获取请求的body
     *
     * @param json json文本
     * @return RequestBody
     */
    fun getRequestBody(json: String): RequestBody {
        return json.toRequestBody(mediaType)
    }
}