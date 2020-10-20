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
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sollyu.android.bluetooth.helper.R
import kotlinx.android.synthetic.main.fragment_device.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DeviceFragment : BaseFragment() {

    private val searchDevicesBroadcastReceiver: SearchDevicesBroadcastReceiver = SearchDevicesBroadcastReceiver()
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    private val recyclerViewAdapter: RecyclerViewAdapter = RecyclerViewAdapter()


    override fun onCreateView(): View =
        LayoutInflater.from(requireContext()).inflate(R.layout.fragment_device, baseFragmentActivity.fragmentContainerView, false) as View

    override fun onViewCreated(rootView: View) {
        super.onViewCreated(rootView)
        qmuiTopBarLayout.setTitle(R.string.fragment_device_title)
        qmuiTopBarLayout.addLeftBackImageButton().setOnClickListener { this.popBackStackResult(resultCode = Activity.RESULT_CANCELED, data = null) }

        val context: Context = rootView.context
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)                // 搜索发现设备
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)   // 状态改变
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)   //行动扫描模式改变了
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)       //动作状态发生了变化
        context.registerReceiver(searchDevicesBroadcastReceiver, intentFilter)

        bluetoothAdapter?.startDiscovery()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.requireContext().unregisterReceiver(searchDevicesBroadcastReceiver)
    }

    private inner class SearchDevicesBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.toString() == BluetoothDevice.ACTION_FOUND) {
                val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) ?: return
                if (recyclerViewAdapter.deviceList.any { it.address == device.address }.not())
                    recyclerViewAdapter.deviceList.add(device)
                recyclerViewAdapter.notifyDataSetChanged()
            }
        }
    }


    private inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    }

    private inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewHolder>() {
        val deviceList: ArrayList<BluetoothDevice> = ArrayList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
            val itemView: View = LayoutInflater.from(requireContext()).inflate(android.R.layout.simple_list_item_1, baseFragmentActivity.fragmentContainerView, false) as View
            return RecyclerViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            val bluetoothDevice: BluetoothDevice = deviceList[position]

        }

        override fun getItemCount(): Int = deviceList.size
    }

}