package com.savet.network.bean

import com.google.gson.annotations.SerializedName
import com.savet.network.BaseResponse

/**
 * 获得Release的回应头
 */
data class GetReleaseResponse (
    @SerializedName("assets")
    val assets: List<Asset>,

    @SerializedName("assets_url")
    val assetsUrl: String,

    @SerializedName("author")
    val author: Author,

    /**
     * TAG的描述内容
     */
    @SerializedName("body")
    val body: String,

    /**
     * Release创建时间, 格式如'2024-03-09T09:10:39Z'
     */
    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("draft")
    val draft: Boolean,

    /**
     * 网页浏览地址
     */
    @SerializedName("html_url")
    val htmlUrl: String,

    @SerializedName("id")
    val id: Int,
    /**
     * 大部分时都是TAG名字
     */
    @SerializedName("name")
    val name: String,

    @SerializedName("node_id")
    val nodeId: String,

    @SerializedName("prerelease")
    val prerelease: Boolean,

    /**
     * Release发布时间, 格式如'2024-03-09T09:26:03Z'
     */
    @SerializedName("published_at")
    val publishedAt: String,
    /**
     * TAG名字
     */
    @SerializedName("tag_name")
    val tagName: String,

    @SerializedName("tarball_url")
    val tarballUrl: String,

    /**
     * 仓库默认分支
     */
    @SerializedName("target_commitish")
    val targetCommitish: String,

    @SerializedName("upload_url")
    val uploadUrl: String,

    @SerializedName("url")
    val url: String,

    /**
     * 压缩包地址
     */
    @SerializedName("zipball_url")
    val zipballUrl: String
) : BaseResponse()

/**
 * 资产相关数据
 */
data class Asset(
    /**
     * 浏览器下载链接
     */
    val browser_download_url: String,
    val content_type: String,
    /**
     * 创建时间
     */
    val created_at: String,
    /**
     * 下载的总次数
     */
    val download_count: Int,
    val id: Int,
    val label: Any,
    /**
     * 资产的文件名
     */
    val name: String,
    val node_id: String,
    val size: Int,
    val state: String,
    /**
     * 更新时间
     */
    val updated_at: String,
    val uploader: Uploader,
    val url: String
)

/**
 * 作者相关数据
 */
data class Author(
    val avatar_url: String,
    val events_url: String,
    val followers_url: String,
    val following_url: String,
    val gists_url: String,
    val gravatar_id: String,
    /**
     * 作者主页
     */
    val html_url: String,
    /**
     * id
     */
    val id: Int,
    /**
     * 作者Github名字
     */
    val login: String,
    val node_id: String,
    val organizations_url: String,
    val received_events_url: String,
    val repos_url: String,
    val site_admin: Boolean,
    val starred_url: String,
    val subscriptions_url: String,
    val type: String,
    val url: String
)

/**
 * 上传者
 */
data class Uploader(
    val avatar_url: String,
    val events_url: String,
    val followers_url: String,
    val following_url: String,
    val gists_url: String,
    val gravatar_id: String,
    val html_url: String,
    /**
     * id
     */
    val id: Int,
    /**
     * 上传人的Github名字
     */
    val login: String,
    val node_id: String,
    val organizations_url: String,
    val received_events_url: String,
    val repos_url: String,
    val site_admin: Boolean,
    val starred_url: String,
    val subscriptions_url: String,
    /**
     * 类型, 一般为'User'
     */
    val type: String,
    val url: String
)