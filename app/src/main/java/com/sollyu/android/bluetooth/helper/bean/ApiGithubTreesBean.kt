package com.sollyu.android.bluetooth.helper.bean

import com.google.gson.annotations.SerializedName

data class ApiGithubTreesBean(
    @SerializedName(value = "sha")
    val sha: String,
    @SerializedName(value = "tree")
    val tree: List<Tree>,
    @SerializedName(value = "truncated")
    val truncated: Boolean,
    @SerializedName(value = "url")
    val url: String
) {
    data class Tree(
        @SerializedName(value = "mode")
        val mode: String,
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
    )
}