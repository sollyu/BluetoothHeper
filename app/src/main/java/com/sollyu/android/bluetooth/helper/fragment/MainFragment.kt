package com.sollyu.android.bluetooth.helper.fragment

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet
import com.sollyu.android.bluetooth.helper.R
import com.sollyu.android.bluetooth.helper.app.Application
import com.sollyu.android.bluetooth.helper.service.BluetoothService
import kotlinx.android.synthetic.main.fragment_main.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MainFragment : BaseFragment(), ServiceConnection, Observer<BluetoothService.Action> {

    private val requestCodeDevice: Int = 932
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    private var mBluetoothServiceBinder: BluetoothService.Binder? = null

    override fun onCreateView(): View =
        LayoutInflater.from(requireContext()).inflate(R.layout.fragment_main, baseFragmentActivity.fragmentContainerView, false) as View

    override fun onViewCreated(rootView: View) {
        super.onViewCreated(rootView)
        qmuiTopBarLayout.setTitle(R.string.app_name)
        qmuiTopBarLayout.addRightImageButton(R.drawable.ic_more, R.id.menu_more).setOnClickListener(this::onClickMenuMore)

        btnSend.setOnClickListener(this::onClickBtnSend)

        val bindIntent = Intent(context, BluetoothService::class.java)
        requireActivity().bindService(bindIntent, this, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unbindService(this)
    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onFragmentResult(requestCode, resultCode, data)
        if (requestCode == requestCodeDevice && resultCode == Activity.RESULT_OK) {
            val bluetoothDevice: BluetoothDevice = data?.getParcelableExtra("s") ?: return
            mBluetoothServiceBinder?.getService()?.connectAsClient(bluetoothDevice)
            logger.info("LOG:MainFragment:onFragmentResult:bluetoothDevice={} ", bluetoothDevice.address)
        }
    }

    private fun onClickBtnSend(view: View) {
        val data: ByteArray = edtMessage.text?.toString()?.toByteArray() ?: return
        mBluetoothServiceBinder?.getService()?.write(data)
    }

    private fun onClickMenuMore(view: View) {
        val context: Context = view.context
        val e: QMUIBottomSheet.BottomListSheetBuilder = QMUIBottomSheet.BottomListSheetBuilder(context)
            .setSkinManager(Application.Instance.qmuiSkinManager)
            .setTitle(context.getString(R.string.fragment_main_menu_title))
            .setAddCancelBtn(true)
            .setAllowDrag(true)
            .setGravityCenter(true)
            .addItem(context.getString(R.string.fragment_main_menu_device_list), "device_list")
            .addItem(context.getString(R.string.fragment_main_menu_settings), "settings")
            .addItem(context.getString(R.string.fragment_main_menu_about), "about")

        if (mBluetoothServiceBinder?.getService()?.isConnect() == true)
            e.addItem(context.getString(R.string.fragment_main_menu_disconnect), "disconnect")

        e.setOnSheetItemClickListener(this::onClickMenuMoreItem)
            .build()
            .show()
    }

    private fun onClickMenuMoreItem(qmuiBottomSheet: QMUIBottomSheet, itemView: View, position: Int, tag: String) {
        qmuiBottomSheet.dismiss()
        when (tag) {
            "device_list" -> this.startFragmentForResult(DeviceFragment(), requestCodeDevice)
            "settings" -> this.startFragment(SettingsFragment())
            "about" -> this.startFragment(AboutFragment())
            "disconnect" -> mBluetoothServiceBinder?.getService()?.disconnect()
        }
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        logger.info("LOG:MainFragment:onServiceConnected name={} service={}", name, service)
        if (service is BluetoothService.Binder) {
            this.mBluetoothServiceBinder = service
            val bluetoothServiceBinder: BluetoothService.Binder = service
            bluetoothServiceBinder.getService().getLiveDate().observe(this, this)

            if (bluetoothServiceBinder.getService().isConnect().not())
                bluetoothServiceBinder.getService().startWaitConnect()
        }
    }

    override fun onServiceDisconnected(name: ComponentName) {
        logger.info("LOG:MainFragment:onServiceDisconnected name={}", name)
        this.mBluetoothServiceBinder = null
    }

    override fun onChanged(t: BluetoothService.Action) {
        logger.info("LOG:MainFragment:onChanged t={}", t)
        val context: Context = requireContext()
        when (t.action) {
            BluetoothService.ActionType.CONNECTING -> {
                tvStatus.setText(R.string.fragment_main_bluetooth_status_connecting)
                btnSend.isEnabled = false
            }
            BluetoothService.ActionType.CONNECTED -> {
                tvStatus.setText(R.string.fragment_main_bluetooth_status_connected)
                btnSend.isEnabled = true
            }
            BluetoothService.ActionType.DISCONNECT -> {
                tvStatus.setText(R.string.fragment_main_bluetooth_status_disconnect)
                mBluetoothServiceBinder?.getService()?.startWaitConnect()
                btnSend.isEnabled = false
            }
            BluetoothService.ActionType.READ -> {
                val data: String = String(t.param1 as ByteArray)
                val history: String = tvReceive.text.toString()
                logger.info("LOG:MainFragment:onChanged data={}", data)
                tvReceive.text = history + data
            }
            BluetoothService.ActionType.WRITE -> {

            }
            BluetoothService.ActionType.WAITING -> {
                tvStatus.setText(R.string.fragment_main_bluetooth_status_waiting)
                btnSend.isEnabled = false
            }
        }
    }
}