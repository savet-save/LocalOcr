package com.savet.network.service

import com.savet.network.BaseCallback
import com.savet.network.BaseResponse
import com.savet.network.ServiceApi
import com.savet.network.bean.GetReleaseResponse
import com.savet.network.manager.ApiManager
import retrofit2.Call
import retrofit2.http.GET

/**
 * GitHub上的api
 */
interface GithubApi: ServiceApi {

    /**
     * 获取所有的releases版本
     *
     * [官方文档](https://docs.github.com/en/rest/releases/releases?apiVersion=2022-11-28)
     */
    @GET("./savet-save/LocalOcr/releases")
    fun getReleases(): Call<List<GetReleaseResponse>>

    /**
     * 获取最新的的releases版本
     *
     * [官方文档](https://docs.github.com/en/rest/releases/releases?apiVersion=2022-11-28#get-the-latest-release)
     */
    @GET("./savet-save/LocalOcr/releases/latest")
    fun getReleasesLatest(): Call<GetReleaseResponse>

}

object GithubRequest {
    /**
     * 获取最新的Releases
     */
    fun getReleasesLatest(callback : Callback) {
        ApiManager.createGithub(GithubApi::class.java).getReleasesLatest()
            .enqueue(object :  BaseCallback<GetReleaseResponse> {
                override fun success(response: GetReleaseResponse) {
                    callback.success(response)
                }

                override fun failure(failCode: Int, msg: String) {
                    callback.failure(failCode, msg)
                }

            })
    }
}

interface Callback {
    fun success(response: GetReleaseResponse)
    fun failure(failCode: Int, errorMsg: String)
}