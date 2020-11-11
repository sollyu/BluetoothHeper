package com.sollyu.android.bluetooth.helper.app

import android.app.Application
import android.content.Intent
import cn.maizz.kotlin.extension.java.io.copy
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager
import com.qmuiteam.qmui.skin.QMUISkinManager
import com.sollyu.android.bluetooth.helper.bean.ApiGithubReleasesBean
import com.sollyu.android.bluetooth.helper.bean.Constant
import com.sollyu.android.bluetooth.helper.bean.YamlSettingBean
import com.sollyu.android.bluetooth.helper.service.BluetoothService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.io.File
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
    lateinit var yamlSettingBean: YamlSettingBean
    var apiGithubReleasesBean: ApiGithubReleasesBean? = null

    override fun onCreate() {
        super.onCreate()

        AppCenter.start(Instance, "7b36cc47-8789-4391-82d0-c3441149f10d", Analytics::class.java, Crashes::class.java)
        Thread.setDefaultUncaughtExceptionHandler(this)
        QMUISwipeBackActivityManager.init(Instance)
        Instance.qmuiSkinManager = QMUISkinManager.defaultInstance(Instance)

        // 复制配置文件
        val yamlSettingsFile = File(Instance.getExternalFilesDir(null), Constant.YAML_SETTINGS_FILE_NAME)
        if (yamlSettingsFile.exists().not())
            assets.open(Constant.YAML_SETTINGS_FILE_NAME).copy(yamlSettingsFile)

        val yaml = Yaml()
        Instance.yamlSettingBean = try {
            yaml.loadAs(yamlSettingsFile.inputStream(), YamlSettingBean::class.java)
        } catch (e: Exception) {
            logger.error("LOG:Application:onCreate", e)
            yaml.loadAs(assets.open(Constant.YAML_SETTINGS_FILE_NAME), YamlSettingBean::class.java)
        }

        // 启动蓝牙服务
        startService(Intent(Instance, BluetoothService::class.java))
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        logger.error("LOG:Application:uncaughtException t={}", t, e)
        exitProcess(status = -1)
    }

}