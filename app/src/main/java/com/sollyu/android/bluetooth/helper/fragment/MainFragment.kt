package com.sollyu.android.bluetooth.helper.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet
import com.sollyu.android.bluetooth.helper.R
import com.sollyu.android.bluetooth.helper.app.Application
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : BaseFragment() {

    private val requestCodeDevice: Int = 932

    override fun onCreateView(): View =
        LayoutInflater.from(requireContext()).inflate(R.layout.fragment_main, baseFragmentActivity.fragmentContainerView, false) as View

    override fun onViewCreated(rootView: View) {
        super.onViewCreated(rootView)
        qmuiTopBarLayout.setTitle(R.string.app_name)
        qmuiTopBarLayout.setTitle(R.string.app_name)
        qmuiTopBarLayout.addRightImageButton(R.drawable.ic_more, R.id.menu_more).setOnClickListener(this::onClickMenuMore)
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
}