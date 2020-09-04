package com.sollyu.android.bluetooth.helper.fragment

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.LayoutInflater
import android.view.View
import com.sollyu.android.bluetooth.helper.R
import kotlinx.android.synthetic.main.fragment_device.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DeviceFragment : BaseFragment() {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val searchDevicesBroadcastReceiver: SearchDevicesBroadcastReceiver = SearchDevicesBroadcastReceiver()

    override fun onCreateView(): View =
        LayoutInflater.from(requireContext()).inflate(R.layout.fragment_device, baseFragmentActivity.fragmentContainerView, false) as View

    override fun onViewCreated(rootView: View) {
        super.onViewCreated(rootView)
        qmuiTopBarLayout.setTitle(R.string.fragment_device_title)
        qmuiTopBarLayout.addLeftBackImageButton().setOnClickListener { this.popBackStackResult(resultCode = Activity.RESULT_CANCELED, data = null) }

        checkBluetoothSupport()
        checkBluetoothStatus()

        startScan(rootView.context)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.requireContext().unregisterReceiver(searchDevicesBroadcastReceiver)
    }

    private fun checkBluetoothSupport() {
        if (bluetoothAdapter == null) {
            TODO(reason = "此设备不支持蓝牙操作")
        }
    }

    private fun checkBluetoothStatus() {
        val bluetoothAdapter: BluetoothAdapter = bluetoothAdapter ?: return
    }

    private fun startScan(context: Context) {
        val intentFilter: IntentFilter = IntentFilter()
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)                // 搜索发现设备
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)   // 状态改变
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)   //行动扫描模式改变了
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)       //动作状态发生了变化
        context.registerReceiver(searchDevicesBroadcastReceiver, intentFilter)
    }


    private class SearchDevicesBroadcastReceiver : BroadcastReceiver() {
        private val logger: Logger = LoggerFactory.getLogger(this.javaClass)
        override fun onReceive(context: Context, intent: Intent) {
            logger.info("LOG:SearchDevicesBroadcastReceiver:onReceive context={} intent={}", context, intent)
        }
    }
}