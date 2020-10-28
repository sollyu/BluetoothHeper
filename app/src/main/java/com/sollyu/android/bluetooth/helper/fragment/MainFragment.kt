package com.sollyu.android.bluetooth.helper.fragment

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.lifecycle.Observer
import cn.maizz.kotlin.extension.android.content.setClipboardString
import cn.maizz.kotlin.extension.android.widget.postDelayed
import cn.maizz.kotlin.extension.java.util.format
import com.google.android.material.snackbar.Snackbar
import com.google.common.io.BaseEncoding
import com.qmuiteam.qmui.layout.QMUIButton
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet
import com.sollyu.android.bluetooth.helper.BuildConfig
import com.sollyu.android.bluetooth.helper.R
import com.sollyu.android.bluetooth.helper.app.Application
import com.sollyu.android.bluetooth.helper.bean.Constant
import com.sollyu.android.bluetooth.helper.bean.YamlSettingBean
import com.sollyu.android.bluetooth.helper.service.BluetoothService
import kotlinx.android.synthetic.main.fragment_main.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit

class MainFragment : BaseFragment(), ServiceConnection, Observer<BluetoothService.Action>, TextView.OnEditorActionListener {

    private val requestCodeDevice: Int = 932
    private val requestCodeSetting: Int = 821

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    private var mBluetoothServiceBinder: BluetoothService.Binder? = null
    private var mHexMode: Boolean = false;
    private var mShortcutMode: Boolean = false;

    private var mReceiveCount: Long = 0L
    private var mWriteCount: Long = 0L

    private var mColorWriteDefault: Int = 0
    private var mColorWriteHighlight: Int = 0
    private var mColorReaderDefault: Int = 0
    private var mColorReaderHighlight: Int = 0
    private var mColorTextBlack: Int = 0
    private var mColorTextWhite: Int = 0
    private var mCharset: Charset = Charsets.UTF_8

    override fun onCreateView(): View =
        LayoutInflater.from(requireContext()).inflate(R.layout.fragment_main, baseFragmentActivity.fragmentContainerView, false) as View

    override fun onViewCreated(rootView: View) {
        super.onViewCreated(rootView)
        qmuiTopBarLayout.setTitle(R.string.app_name)
        qmuiTopBarLayout.addRightImageButton(R.drawable.ic_more, R.id.menu_more).setOnClickListener(this::onClickListenerMore)

        btnSend.setOnClickListener(this::onClickListenerSend)
        cbHex.setOnCheckedChangeListener(this::onCheckedChangedHex)
        tvReceive.setOnLongClickListener(this::onLongClickListenerOutput)

        val context: Context = requireContext()
        val bindIntent = Intent(context, BluetoothService::class.java)
        requireActivity().bindService(bindIntent, this, Context.BIND_AUTO_CREATE)

        rootView.post {
            mColorReaderDefault = context.resources.getColor(R.color.fragment_main_statistics_receive_default)
            mColorReaderHighlight = context.resources.getColor(R.color.fragment_main_statistics_receive_highlight)
            mColorWriteDefault = context.resources.getColor(R.color.fragment_main_statistics_write_default)
            mColorWriteHighlight = context.resources.getColor(R.color.fragment_main_statistics_write_highlight)
            mColorTextBlack = context.resources.getColor(android.R.color.black)
            mColorTextWhite = context.resources.getColor(android.R.color.white)

            tvReceiveCount.text = context.getString(R.string.fragment_main_statistics_receive, 0)
            tvWriteCount.text = context.getString(R.string.fragment_main_statistics_write, 0)
            this.onReloadSetting()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unbindService(this)
        mBluetoothServiceBinder?.getService()?.getLiveDate()?.removeObservers(this)
    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onFragmentResult(requestCode, resultCode, data)
        if (requestCode == requestCodeDevice && resultCode == Activity.RESULT_OK) {
            val bluetoothDevice: BluetoothDevice = data?.getParcelableExtra(Constant.INTENT_PARAM_1) ?: return
            mBluetoothServiceBinder?.getService()?.connectAsClient(bluetoothDevice)
        }

        if (requestCode == requestCodeSetting) {
            onReloadSetting()
        }


    }

    private fun onClickListenerSend(view: View) {
        if (mBluetoothServiceBinder?.getService()?.isConnect() != true)
            return

        edtMessage.setSingleLine()
        val inputMessage: String = edtMessage.text?.toString() ?: return
        var writeDate: ByteArray = inputMessage.toByteArray()

        // 以十六进制发送
        if (mHexMode) {
            try {
                writeDate = BaseEncoding.base16().decode(inputMessage.replace(oldValue = " ", newValue = ""))
            } catch (e: Exception) {
                Snackbar.make(view, R.string.fragment_main_snackbar_hex_convert_normal_fail, Snackbar.LENGTH_SHORT).show()
            }
        }

        // 尾部插入跟随
        if (Application.Instance.yamlSettingBean.onSendAppend.isNotEmpty()) {
            try {
                val appendByteArray: ByteArray = BaseEncoding.base16().decode(Application.Instance.yamlSettingBean.onSendAppend)
                writeDate = writeDate.plus(appendByteArray)
            } catch (e: Exception) {
                logger.error("LOG:MainFragment:onClickBtnSend", e)
            }
        }

        // 判空
        if (writeDate.isEmpty())
            return

        mBluetoothServiceBinder?.getService()?.write(writeDate)
        if (Application.Instance.yamlSettingBean.onSendClean)
            edtMessage.text = null
    }

    private fun onClickListenerMore(view: View) {
        val context: Context = view.context
        val sheetBuilder: QMUIBottomSheet.BottomListSheetBuilder = QMUIBottomSheet.BottomListSheetBuilder(context)
            .setSkinManager(Application.Instance.qmuiSkinManager)
            .setTitle(context.getString(R.string.fragment_main_menu_title))
            .setAllowDrag(true)
            .setGravityCenter(true)
            .addItem(context.getString(R.string.fragment_main_menu_device_list), "device_list")
            .addItem(context.getString(R.string.fragment_main_menu_settings), "settings")
            .addItem(context.getString(R.string.fragment_main_menu_about), "about")

        if (mBluetoothServiceBinder?.getService()?.isConnect() == true)
            sheetBuilder.addItem(context.getString(R.string.fragment_main_menu_disconnect), "disconnect")

        if (mShortcutMode)
            sheetBuilder.addItem(context.getString(R.string.fragment_main_menu_input), "mode_input")
        else
            sheetBuilder.addItem(context.getString(R.string.fragment_main_menu_shortcut), "mode_shortcut")

        sheetBuilder.setOnSheetItemClickListener(this::onClickMenuMoreItem)
            .build()
            .show()
    }

    private fun onClickListenerShortcut(view: View) {
        val shortcut: YamlSettingBean.Shortcut = view.tag as YamlSettingBean.Shortcut
        if (shortcut.isEmpty())
            return

        var writeDate: ByteArray = shortcut.text.toByteArray()

        // 以十六进制发送
        if (shortcut.hex) {
            try {
                writeDate = BaseEncoding.base16().decode(shortcut.text.replace(oldValue = " ", newValue = ""))
            } catch (e: Exception) {
                Snackbar.make(view, R.string.fragment_main_snackbar_hex_convert_normal_fail, Snackbar.LENGTH_SHORT).show()
                return
            }
        }

        if (shortcut.isEmpty())
            return

        mBluetoothServiceBinder?.getService()?.write(writeDate)
    }

    private fun onCheckedChangedHex(buttonView: CompoundButton, isChecked: Boolean) {
        mHexMode = isChecked
        edtMessage.setHint(if (isChecked) R.string.fragment_main_ui_message_hit_hex else R.string.fragment_main_ui_message_hit_ascii)
        val inputMessage: String = edtMessage.text.toString()
        if (inputMessage.isNotEmpty() && isChecked) {
            val hexString: String = BaseEncoding.base16().encode(inputMessage.toByteArray()).chunked(size = 2).joinToString(separator = " ")
            edtMessage.setText(hexString)
            edtMessage.setSelection(hexString.length)
        } else {
            val normal: String = try {
                String(BaseEncoding.base16().decode(inputMessage.replace(oldValue = " ", newValue = "")), mCharset)
            } catch (e: Exception) {
                Snackbar.make(buttonView, R.string.fragment_main_snackbar_hex_convert_normal_fail, Snackbar.LENGTH_SHORT).show()
                inputMessage
            }
            edtMessage.setText(normal)
            edtMessage.setSelection(normal.length)
        }
    }

    @Suppress(names = ["UNUSED_PARAMETER"])
    private fun onClickMenuMoreItem(qmuiBottomSheet: QMUIBottomSheet, itemView: View, position: Int, tag: String) {
        qmuiBottomSheet.dismiss()
        when (tag) {
            "device_list" -> {
                this.startFragmentForResult(DeviceFragment(), requestCodeDevice)
            }
            "settings" -> {
                this.startFragmentForResult(SettingsFragment(), requestCodeSetting)
            }
            "about" -> {
                this.startFragment(AboutFragment())
            }
            "disconnect" -> {
                mBluetoothServiceBinder?.getService()?.disconnect()
            }
            "mode_input" -> {
                mShortcutMode = false
                onReloadSetting()
            }
            "mode_shortcut" -> {
                mShortcutMode = true
                onReloadSetting()
            }
        }
    }

    private fun onLongClickListenerOutput(view: View): Boolean {
        val context: Context = view.context
        QMUIBottomSheet.BottomListSheetBuilder(context)
            .setSkinManager(Application.Instance.qmuiSkinManager)
            .setTitle(context.getString(R.string.fragment_main_ui_message_menu_title))
            .setAddCancelBtn(true)
            .setAllowDrag(true)
            .setGravityCenter(true)
            .addItem(context.getString(R.string.fragment_main_ui_message_menu_clean), "clean")
            .addItem(context.getString(R.string.fragment_main_ui_message_menu_copy), "copy")
            .addItem(context.getString(R.string.fragment_main_ui_message_menu__save), "save")
            .setOnSheetItemClickListener(this::onClickListenerMenuItemOutput)
            .build()
            .show()

        return true
    }

    @Suppress(names = ["UNUSED_PARAMETER"])
    private fun onClickListenerMenuItemOutput(qmuiBottomSheet: QMUIBottomSheet, itemView: View, position: Int, tag: String) {
        qmuiBottomSheet.dismiss()
        val context: Context = itemView.context
        when (tag) {
            "clean" -> {
                tvReceive.text = ""
                mWriteCount = 0
                mReceiveCount = 0
                tvWriteCount.text = context.getString(R.string.fragment_main_statistics_write, mWriteCount)
                tvReceiveCount.text = context.getString(R.string.fragment_main_statistics_receive, mReceiveCount)
            }
            "copy" -> {
                val outputString: String = tvReceive.text?.toString() ?: return
                if (outputString.isBlank())
                    return
                tvReceive.context.setClipboardString(outputString)
            }
            "save" -> {
                val outputString: String = tvReceive.text?.toString() ?: ""
                val outputFile: File = File(context.getExternalFilesDir("log"), Date().format(format = "yyyy-MM-dd HHmmss").plus(other = ".txt"))
                val outputStringBuilder: StringBuilder = StringBuilder()
                val appName: String = context.getString(R.string.app_name)
                outputStringBuilder.append(context.getString(R.string.fragment_main_log_header_app, appName))
                outputStringBuilder.append(context.getString(R.string.fragment_main_log_header_version, BuildConfig.VERSION_NAME))
                if (mBluetoothServiceBinder?.getService()?.isConnect() == true) {
                    outputStringBuilder.append(context.getString(R.string.fragment_main_log_header_device, mBluetoothServiceBinder?.getService()?.getDevice()?.name))
                    outputStringBuilder.append(context.getString(R.string.fragment_main_log_header_address, mBluetoothServiceBinder?.getService()?.getDevice()?.address))
                }
                outputStringBuilder.append(context.getString(R.string.fragment_main_log_header_time, Date().format()))
                outputStringBuilder.append(outputString)
                outputFile.writeText(outputStringBuilder.toString(), mCharset)
                Snackbar.make(requireView(), context.getString(R.string.fragment_main_snackbar_save_log_success, outputFile), Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        if (service is BluetoothService.Binder) {
            this.mBluetoothServiceBinder = service
            val bluetoothServiceBinder: BluetoothService.Binder = service
            bluetoothServiceBinder.getService().getLiveDate().observe(this, this)

            if (bluetoothServiceBinder.getService().isConnect().not())
                bluetoothServiceBinder.getService().startWaitConnect()
        }
    }

    override fun onServiceDisconnected(name: ComponentName) {
        this.mBluetoothServiceBinder = null
    }

    /**
     * LiveData发生改变
     */
    override fun onChanged(t: BluetoothService.Action) {
        logger.info("LOG:MainFragment:onChanged t={}", t)
        val context: Context = requireContext()
        when (t.action) {
            BluetoothService.ActionType.CONNECTING -> {
                tvStatus.setText(R.string.fragment_main_bluetooth_status_connecting)
            }
            BluetoothService.ActionType.CONNECTED -> {
                tvStatus.setText(R.string.fragment_main_bluetooth_status_connected)
            }
            BluetoothService.ActionType.DISCONNECT -> {
                tvStatus.setText(R.string.fragment_main_bluetooth_status_disconnect)
                mBluetoothServiceBinder?.getService()?.startWaitConnect()
            }
            BluetoothService.ActionType.WAITING -> {
                tvStatus.setText(R.string.fragment_main_bluetooth_status_waiting)
            }
            BluetoothService.ActionType.READ -> {
                val rawByteArray: ByteArray = t.param1 as ByteArray
                val displayString: String = if (mHexMode)
                    BaseEncoding.base16().encode(rawByteArray).chunked(size = 2).joinToString(separator = " ")
                else
                    String(rawByteArray, mCharset)

                val history: String = tvReceive.text.toString()
                tvReceive.text = String.format("%s%s", history, displayString)
                mReceiveCount += rawByteArray.size
                tvReceiveCount.text = context.getString(R.string.fragment_main_statistics_receive, mReceiveCount)

                tvReceiveCount.setTextColor(mColorTextBlack)
                tvReceiveCount.setBackgroundColor(mColorReaderHighlight)
                tvReceiveCount.postDelayed(20, TimeUnit.MILLISECONDS) {
                    tvReceiveCount.setBackgroundColor(mColorReaderDefault)
                    tvReceiveCount.setTextColor(mColorTextWhite)
                }
                tvReceive.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
            }
            BluetoothService.ActionType.WRITE -> {
                val rawByteArray: ByteArray = t.param1 as ByteArray
                mWriteCount += rawByteArray.size
                tvWriteCount.text = context.getString(R.string.fragment_main_statistics_write, mWriteCount)

                tvWriteCount.setTextColor(mColorTextBlack)
                tvWriteCount.setBackgroundColor(mColorWriteHighlight)
                tvWriteCount.postDelayed(20, TimeUnit.MILLISECONDS) {
                    tvWriteCount.setBackgroundColor(mColorWriteDefault)
                    tvWriteCount.setTextColor(mColorTextWhite)
                }
            }
        }
    }

    private fun onReloadSetting() {
        val context: Context = requireContext()

        // 回车发送
        if (Application.Instance.yamlSettingBean.enterKeySend) {
            edtMessage.isSingleLine = true
            edtMessage.maxLines = 1
            edtMessage.imeOptions = EditorInfo.IME_ACTION_SEND
            edtMessage.setOnEditorActionListener(this)
        } else {
            edtMessage.isSingleLine = false
            edtMessage.maxLines = 999
            edtMessage.imeOptions = EditorInfo.IME_ACTION_NONE
            edtMessage.setOnEditorActionListener(null)
        }

        // 快捷键模式
        if (mShortcutMode) {
            llSendLayout.visibility = View.GONE
            svShortcutLayout.visibility = View.VISIBLE
        } else {
            llSendLayout.visibility = View.VISIBLE
            svShortcutLayout.visibility = View.GONE
        }

        mCharset = Charset.forName(Application.Instance.yamlSettingBean.stringCharset)

        llShortcut.removeAllViews()
        Application.Instance.yamlSettingBean.shortcut
            .filter { it.isEmpty().not() }
            .forEach { shortcut: YamlSettingBean.Shortcut ->
                val qmuiButton = QMUIButton(context)
                qmuiButton.text = shortcut.name
                qmuiButton.tag = shortcut
                qmuiButton.setOnClickListener(this::onClickListenerShortcut)
                llShortcut.addView(qmuiButton, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))
            }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            btnSend.performClick()
        }
        return true
    }

}