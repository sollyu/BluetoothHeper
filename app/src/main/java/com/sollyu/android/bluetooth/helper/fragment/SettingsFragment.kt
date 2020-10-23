package com.sollyu.android.bluetooth.helper.fragment

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView
import com.sollyu.android.bluetooth.helper.R
import com.sollyu.android.bluetooth.helper.app.Application
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment() {


    override fun onCreateView(): View =
        LayoutInflater.from(requireContext()).inflate(R.layout.fragment_settings, baseFragmentActivity.fragmentContainerView, false) as View

    override fun onViewCreated(rootView: View) {
        super.onViewCreated(rootView)
        qmuiTopBarLayout.setTitle(R.string.fragment_settings_title)
        qmuiTopBarLayout.addLeftBackImageButton().setOnClickListener { this.popBackStackResult(resultCode = Activity.RESULT_CANCELED, data = null) }

        onRefreshUi()
    }

    private fun onRefreshUi() {
        val listItemHeight: Int = com.qmuiteam.qmui.util.QMUIResHelper.getAttrDimen(context, com.qmuiteam.qmui.R.attr.qmui_list_item_height)

        val context: Context = requireActivity()
        val imageDrawable: Drawable? = null

        val itemSingleLine: QMUICommonListItemView = qmuiGroupListView.createItemView(imageDrawable, "回车发送", "", QMUICommonListItemView.VERTICAL, QMUICommonListItemView.ACCESSORY_TYPE_SWITCH, listItemHeight)
        itemSingleLine.switch.isChecked = Application.Instance.sharedPreferences.isSingleLine
        itemSingleLine.switch.setOnCheckedChangeListener(this::onSingleLineCheckedChangeListener)

        val itemSendClean: QMUICommonListItemView = qmuiGroupListView.createItemView(imageDrawable, "发送清空", "", QMUICommonListItemView.VERTICAL, QMUICommonListItemView.ACCESSORY_TYPE_SWITCH, listItemHeight)
        itemSendClean.switch.isChecked = Application.Instance.sharedPreferences.isSendClean
        itemSendClean.switch.setOnCheckedChangeListener(this::onSendCleanCheckedChangeListener)

        QMUIGroupListView.Section(context)
            .setTitle("发送策略")
            .addItemView(itemSingleLine, null)
            .addItemView(itemSendClean, null)
            .addTo(qmuiGroupListView)
    }

    @Suppress(names = ["UNUSED_PARAMETER"])
    private fun onSingleLineCheckedChangeListener(buttonView: CompoundButton, isChecked: Boolean) {
        Application.Instance.sharedPreferences.isSingleLine = isChecked
    }

    @Suppress(names = ["UNUSED_PARAMETER"])
    private fun onSendCleanCheckedChangeListener(buttonView: CompoundButton, isChecked: Boolean) {
        Application.Instance.sharedPreferences.isSendClean = isChecked
    }

}