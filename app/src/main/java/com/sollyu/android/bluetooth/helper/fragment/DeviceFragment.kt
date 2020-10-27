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
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.maizz.kotlin.extension.android.widget.postDelayed
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView
import com.sollyu.android.bluetooth.helper.R
import com.sollyu.android.bluetooth.helper.bean.Constant
import kotlinx.android.synthetic.main.fragment_device.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class DeviceFragment : BaseFragment() {

    private val searchDevicesBroadcastReceiver: SearchDevicesBroadcastReceiver = SearchDevicesBroadcastReceiver()
    private val recyclerViewAdapter: RecyclerViewAdapter = RecyclerViewAdapter()
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    override fun onCreateView(): View =
        LayoutInflater.from(requireContext()).inflate(R.layout.fragment_device, baseFragmentActivity.fragmentContainerView, false) as View

    override fun onViewCreated(rootView: View) {
        super.onViewCreated(rootView)
        qmuiTopBarLayout.setTitle(R.string.fragment_device_title)
        qmuiTopBarLayout.addLeftBackImageButton().setOnClickListener { this.popBackStackResult(resultCode = Activity.RESULT_CANCELED, data = null) }
        qmuiTopBarLayout.addRightImageButton(R.drawable.ic_expand, R.id.menu_more).setOnClickListener(this::onClickRefresh)

        val context: Context = rootView.context

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)                // 搜索发现设备
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)   // 状态改变
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)   //行动扫描模式改变了
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)       //动作状态发生了变化
        context.registerReceiver(searchDevicesBroadcastReceiver, intentFilter)

        // 为了避免UI卡顿
        rootView.postDelayed(200, TimeUnit.MILLISECONDS) { bluetoothAdapter?.startDiscovery() }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.requireContext().unregisterReceiver(searchDevicesBroadcastReceiver)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onClickRefresh(view: View) {
        bluetoothAdapter?.startDiscovery()
        recyclerViewAdapter.deviceList.clear()
        recyclerViewAdapter.notifyDataSetChanged()
    }

    private inner class SearchDevicesBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.toString() == BluetoothDevice.ACTION_FOUND) {
                val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) ?: return
                if (device.name?.isNotBlank() == true && recyclerViewAdapter.deviceList.any { it.address == device.address }.not()) {
                    recyclerViewAdapter.deviceList.add(device)
                    recyclerViewAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewHolder>(), View.OnClickListener {
        val deviceList: ArrayList<BluetoothDevice> = ArrayList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
            val context: Context = parent.context
            val itemView = QMUICommonListItemView(context)
            val height: Int = context.resources.getDimension(com.qmuiteam.qmui.R.dimen.qmui_list_item_height).toInt()
            itemView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
            return RecyclerViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            val bluetoothDevice: BluetoothDevice = deviceList[position]
            val itemView: QMUICommonListItemView = holder.itemView as QMUICommonListItemView
            itemView.text = bluetoothDevice.name ?: "无名称"
            itemView.detailText = bluetoothDevice.address?.toString()
            itemView.tag = bluetoothDevice
            itemView.setOnClickListener(this)
        }

        override fun getItemCount(): Int = deviceList.size

        override fun onClick(v: View) {
            val bluetoothDevice: BluetoothDevice = v.tag as BluetoothDevice
            val intent = Intent()
            intent.putExtra(Constant.INTENT_PARAM_1, bluetoothDevice)
            this@DeviceFragment.popBackStackResult(Activity.RESULT_OK, intent)
        }
    }

}