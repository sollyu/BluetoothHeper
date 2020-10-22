package com.sollyu.android.bluetooth.helper.app

import android.app.Application
import android.content.Intent
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager
import com.qmuiteam.qmui.skin.QMUISkinManager
import com.sollyu.android.bluetooth.helper.bean.Constant
import com.sollyu.android.bluetooth.helper.service.BluetoothService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

class Application : Application(), Thread.UncaughtExceptionHandler {
    companion object {
        lateinit var Instance: com.sollyu.android.bluetooth.helper.app.Application
            private set
    }

    init {
        Instance = this
    }

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    lateinit var qmuiSkinManager: QMUISkinManager
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        Thread.setDefaultUncaughtExceptionHandler(this)
        QMUISwipeBackActivityManager.init(Instance)
        Instance.qmuiSkinManager = QMUISkinManager.defaultInstance(Instance)
        Instance.sharedPreferences = SharedPreferences()

        // 启动蓝牙服务
        startService(Intent(Instance, BluetoothService::class.java))
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        logger.error("LOG:Application:uncaughtException t={}", t, e)
        exitProcess(status = -1)
    }

    class SharedPreferences {
        private val raw: android.content.SharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(Instance)

        var isSingleLine: Boolean
            get() :Boolean = raw.getBoolean(Constant.PREFERENCES_KEY_SINGLE_LINE, Constant.PREFERENCES_DEFAULT_SINGLE_LINE)
            set(value: Boolean) = raw.edit().putBoolean(Constant.PREFERENCES_KEY_SINGLE_LINE, value).apply()

        var isSendClean: Boolean
            get() : Boolean = raw.getBoolean(Constant.PREFERENCES_KEY_CLEAN_SEND, Constant.PREFERENCES_DEFAULT_CLEAN_SEND)
            set(value: Boolean) = raw.edit().putBoolean(Constant.PREFERENCES_KEY_CLEAN_SEND, value).apply()

        var isHex: Boolean
            get() : Boolean = raw.getBoolean(Constant.PREFERENCES_KEY_HEX, Constant.PREFERENCES_DEFAULT_HEX)
            set(value: Boolean) = raw.edit().putBoolean(Constant.PREFERENCES_KEY_HEX, value).apply()

        var charset: String
            get() : String = raw.getString(Constant.PREFERENCES_KEY_CHARSET, Constant.PREFERENCES_DEFAULT_CHARSET) ?: Constant.PREFERENCES_DEFAULT_CHARSET
            set(value: String) = raw.edit().putString(Constant.PREFERENCES_KEY_CHARSET, value).apply()


    }

}