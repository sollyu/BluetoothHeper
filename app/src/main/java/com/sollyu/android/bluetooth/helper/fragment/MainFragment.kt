package com.sollyu.android.bluetooth.helper.fragment

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

class MainFragment : BaseFragment(), ServiceConnection, Observer<ByteArray> {

    private val requestCodeDevice: Int = 932
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    private var bluetoothServiceBinder:BluetoothService.Binder ?= null

    override fun onCreateView(): View =
        LayoutInflater.from(requireContext()).inflate(R.layout.fragment_main, baseFragmentActivity.fragmentContainerView, false) as View

    override fun onViewCreated(rootView: View) {
        super.onViewCreated(rootView)
        qmuiTopBarLayout.setTitle(R.string.app_name)
        qmuiTopBarLayout.setTitle(R.string.app_name)
        qmuiTopBarLayout.addRightImageButton(R.drawable.ic_more, R.id.menu_more).setOnClickListener(this::onClickMenuMore)

        btnSend.setOnClickListener(this::onClickBtnSend)
    }

    override fun onStart() {
        super.onStart()
        val context: Context = requireContext()
        val bindIntent = Intent(context, BluetoothService::class.java)
        requireActivity().bindService(bindIntent, this, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unbindService(this)
    }

    private fun onClickBtnSend(view: View) {
        val bluetoothServiceBinder: BluetoothService.Binder = this.bluetoothServiceBinder ?: return
        bluetoothServiceBinder.getService().connect("as")
    }

    private fun onClickMenuMore(view: View) {
        val context: Context = view.context
        QMUIBottomSheet.BottomListSheetBuilder(context)
            .setSkinManager(Application.Instance.qmuiSkinManager)
            .setTitle(context.getString(R.string.fragment_main_menu_title))
            .setAddCancelBtn(true)
            .setAllowDrag(true)
            .setGravityCenter(true)
            .addItem(context.getString(R.string.fragment_main_menu_device_list))
            .addItem(context.getString(R.string.fragment_main_menu_settings))
            .addItem(context.getString(R.string.fragment_main_menu_about))
            .setOnSheetItemClickListener(this::onClickMenuMoreItem)
            .build()
            .show()
    }

    private fun onClickMenuMoreItem(qmuiBottomSheet: QMUIBottomSheet, itemView: View, position: Int, tag: String) {
        qmuiBottomSheet.dismiss()
        when (position) {
            0 -> this.startFragmentForResult(DeviceFragment(), requestCodeDevice)
            1 -> this.startFragment(SettingsFragment())
            2 -> this.startFragment(AboutFragment())
        }
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        logger.info("LOG:MainFragment:onServiceConnected name={} service={}", name, service)
        if (service is BluetoothService.Binder) {
            this.bluetoothServiceBinder = service
            service.getService().getReader().observe(this, this)
        }
    }

    override fun onServiceDisconnected(name: ComponentName) {
        logger.info("LOG:MainFragment:onServiceDisconnected name={}", name)
    }

    override fun onChanged(t: ByteArray?) {
        logger.info("LOG:MainFragment:onChanged t={}", t)
    }
}