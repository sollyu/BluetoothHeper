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
    fun isBlank(): Boolean = name?.isBlank() == true && text?.isBlank() == true
    fun isEmpty(): Boolean = name?.isEmpty() == true && text?.isEmpty() == true
}