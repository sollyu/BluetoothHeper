package com.sollyu.android.bluetooth.helper.app

import android.app.Application
import android.content.Intent
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager
import com.qmuiteam.qmui.skin.QMUISkinManager
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

    override fun onCreate() {
        super.onCreate()

        Thread.setDefaultUncaughtExceptionHandler(this)
        QMUISwipeBackActivityManager.init(Instance)
        Instance.qmuiSkinManager = QMUISkinManager.defaultInstance(Instance)

        // 启动蓝牙服务
        startService(Intent(Instance, BluetoothService::class.java))
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        logger.error("LOG:Application:uncaughtException t={}", t, e)
        exitProcess(status = -1)
    }
}