package com.sollyu.android.bluetooth.helper.bean

import com.google.gson.annotations.SerializedName

data class ApiGithubReleasesBean(
    @SerializedName(value = "assets")
    val assets: List<Asset>,
    @SerializedName(value = "body")
    val body: String,
    @SerializedName(value = "created_at")
    val createdAt: String,
    @SerializedName(value = "name")
    val name: String,
    @SerializedName(value = "published_at")
    val publishedAt: String,
    @SerializedName(value = "tag_name")
    val tagName: String
) {
    data class Asset(
        @SerializedName(value = "browser_download_url")
        val browserDownloadUrl: String,
        @SerializedName(value = "content_type")
        val contentType: String,
        @SerializedName(value = "created_at")
        val createdAt: String,
        @SerializedName(value = "download_count")
        val downloadCount: Int,
        @SerializedName(value = "id")
        val id: Int,
        @SerializedName(value = "name")
        val name: String,
        @SerializedName(value = "size")
        val size: Long,
        @SerializedName(value = "state")
        val state: String,
        @SerializedName(value = "updated_at")
        val updatedAt: String,
        @SerializedName(value = "url")
        val url: String
    )
}