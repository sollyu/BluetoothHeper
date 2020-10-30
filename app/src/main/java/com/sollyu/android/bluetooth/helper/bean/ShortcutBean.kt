package com.sollyu.android.bluetooth.helper.bean


import com.google.gson.annotations.SerializedName

data class ShortcutBean(
    @SerializedName(value = "name")
    val name: String?,
    @SerializedName(value = "hex")
    val hex: Boolean?,
    @SerializedName(value = "text")
    val text: String?
) {
    fun isBlank(): Boolean = name.isNullOrBlank() && text.isNullOrBlank()
    fun isEmpty(): Boolean = name.isNullOrEmpty() && text.isNullOrEmpty()
}