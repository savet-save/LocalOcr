package com.savet.network

import com.savet.local.baselibrary.utils.LogUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * 将Retrofit的Callback整理成简单的"成功和失败"，并提供抽象方法
 *
 * @author ShangPan
 * @date 2024/4/16
 */
interface BaseCallback<T : BaseResponse> : Callback<T> {

    companion object {
        // 网络错误
        const val FAIL_NETWORK: Int = -100

        // 异常错误
        const val FAIL_EXCEPTIONS: Int = -101

        // body为null
        const val FAIL_NULL_BODY: Int = -102

        // 没有发起请求
        const val FAIL_NOT_REQUEST: Int = -103

        private const val TAG = "BaseCallback"
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            val body = response.body()
            if (body == null) {
                val dataIsNull = "回应数据为null"
                LogUtils.e(TAG, dataIsNull)
                failure(FAIL_NULL_BODY, dataIsNull)
            } else {
                success(body)
            }
        } else {
            // code错误，不在200-300
            LogUtils.e(TAG, response.code().toString() + " " + response.message())
            failure(FAIL_NETWORK, response.code().toString() + " " + response.message())
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        LogUtils.e(TAG, t.toString())
        failure(FAIL_EXCEPTIONS, t.toString())
    }

    fun success(response: T)

    fun failure(failCode: Int, msg: String)

}