package com.sollyu.android.bluetooth.helper.fragment

import android.content.Intent
import com.qmuiteam.qmui.arch.QMUIFragment

abstract class BaseFragment : QMUIFragment() {

    fun popBackStack_(): Unit =
        this.popBackStack()

    fun popBackStackResult(resultCode: Int, data: Intent?) {
        this.setFragmentResult(resultCode, data)
        this.popBackStack()
    }
}