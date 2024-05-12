package com.savet.network.manager


import com.savet.local.baselibrary.utils.LogUtils
import com.savet.network.NetworkConstant
import com.savet.network.ServiceApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * 所有的Api
 *
 * 使用需要声明<uses-permission android:name="android.permission.INTERNET" />权限
 * @author Savet
 */
object ApiManager {
    private const val TAG = "ApiManager"
    private var mGithubRetrofit: Retrofit? = null

    private fun buildHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val builder = OkHttpClient.Builder()
        /* 可以统一添加网络参数到请求头 */
        builder.addInterceptor(RequestInterceptor(NetworkRequestInfo()))
        /* 网络请求返回的时候的数据处理 */
        builder.addInterceptor(ResponseInterceptor())
        if (com.savet.network.BuildConfig.DEBUG) {
            builder.addInterceptor(loggingInterceptor)
        }
        builder.connectTimeout(NetworkConstant.CONNECT_TIME_OUT, TimeUnit.SECONDS)
        builder.readTimeout(NetworkConstant.CONNECT_TIME_OUT, TimeUnit.SECONDS)
        builder.writeTimeout(NetworkConstant.CONNECT_TIME_OUT, TimeUnit.SECONDS)
        return builder.build()
    }

    private fun buildRetrofit(client: OkHttpClient, baseUrl: String): Retrofit {
        val builder = Retrofit.Builder()
        builder.client(client)
        builder.baseUrl(baseUrl)
        builder.addConverterFactory(GsonConverterFactory.create())
        return builder.build()
    }

    /**
     * 初始化(可以重复初始化)
     *
     */
    fun init() {
        val useUrl: String = NetworkConstant.GITHUB_URL
        LogUtils.d(TAG, "init: baseUrl = $useUrl")
        mGithubRetrofit = buildRetrofit(buildHttpClient(), useUrl)
    }

    /**
     * 创建本机登录对应的api
     */
    fun <T : ServiceApi> createGithub(service: Class<T>): T {
        if (mGithubRetrofit == null) {
            init()
        }
        return mGithubRetrofit!!.create(service)
    }

}