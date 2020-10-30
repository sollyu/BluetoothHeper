package com.sollyu.android.bluetooth.helper.bean

import androidx.annotation.Keep

@Keep
data class YamlSettingBean(
    val enterKeySend: Boolean = true,
    val onSendAppend: String = Constant.EMPTY_STRING,
    val onSendClean: Boolean = true,
    val hideNoNameDevice: Boolean = true,
    val stringCharset: String = "UTF-8",
    val serverUuid: String = Constant.EMPTY_STRING,
    val shortcut: List<Shortcut> = ArrayList()
) {
    @Keep
    data class Shortcut(
        val name: String = Constant.EMPTY_STRING,
        val hex: Boolean = false,
        val text: String = Constant.EMPTY_STRING
    ) {
        fun isEmpty(): Boolean = name.isEmpty() && text.isEmpty()
    }
}
