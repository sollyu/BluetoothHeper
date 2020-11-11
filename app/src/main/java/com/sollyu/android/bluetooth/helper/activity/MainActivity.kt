package com.sollyu.android.bluetooth.helper.activity

import android.os.Bundle
import com.microsoft.appcenter.analytics.Analytics
import com.qmuiteam.qmui.arch.QMUIFragmentActivity
import com.qmuiteam.qmui.arch.annotation.DefaultFirstFragment
import com.sollyu.android.bluetooth.helper.app.Application
import com.sollyu.android.bluetooth.helper.fragment.SplashFragment

@DefaultFirstFragment(SplashFragment::class)
class MainActivity : QMUIFragmentActivity() {

    companion object {
        lateinit var Instance: MainActivity
            private set
    }

    init {
        Instance = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.skinManager = Application.Instance.qmuiSkinManager
    }

    override fun onStart() {
        super.onStart()
        Application.Instance.qmuiSkinManager.register(this)
    }

    override fun onStop() {
        super.onStop()
        Application.Instance.qmuiSkinManager.unRegister(this)
    }

    override fun onPause() {
        super.onPause()
        Analytics.pause()
    }

    override fun onResume() {
        super.onResume()
        Analytics.resume()
    }

}
