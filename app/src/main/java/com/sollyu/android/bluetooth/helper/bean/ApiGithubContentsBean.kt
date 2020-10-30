package com.sollyu.android.bluetooth.helper.bean

import com.google.gson.annotations.SerializedName

data class ApiGithubContentsBean(
    @SerializedName(value = "content")
    val content: String,
    @SerializedName(value = "download_url")
    val downloadUrl: String,
    @SerializedName(value = "encoding")
    val encoding: String,
    @SerializedName(value = "git_url")
    val gitUrl: String,
    @SerializedName(value = "html_url")
    val htmlUrl: String,
    @SerializedName(value = "_links")
    val links: Links,
    @SerializedName(value = "name")
    val name: String,
    @SerializedName(value = "path")
    val path: String,
    @SerializedName(value = "sha")
    val sha: String,
    @SerializedName(value = "size")
    val size: Int,
    @SerializedName(value = "type")
    val type: String,
    @SerializedName(value = "url")
    val url: String
) {
    data class Links(
        @SerializedName(value = "git")
        val git: String,
        @SerializedName(value = "html")
        val html: String,
        @SerializedName(value = "self")
        val self: String
    )
}
