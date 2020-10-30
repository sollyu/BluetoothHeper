package com.sollyu.android.bluetooth.helper.fragment

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView
import com.sollyu.android.bluetooth.helper.BuildConfig
import com.sollyu.android.bluetooth.helper.R
import com.sollyu.android.bluetooth.helper.bean.Constant
import kotlinx.android.synthetic.main.fragment_about.*

class AboutFragment : BaseFragment() {
    override fun onCreateView(): View =
        LayoutInflater.from(requireContext()).inflate(R.layout.fragment_about, baseFragmentActivity.fragmentContainerView, false) as View

    override fun onViewCreated(rootView: View) {
        super.onViewCreated(rootView)
        qmuiTopBarLayout.setTitle(R.string.fragment_about_title)
        qmuiTopBarLayout.addLeftBackImageButton().setOnClickListener { this.popBackStackResult(resultCode = Activity.RESULT_CANCELED, data = null) }

        val context: Context = requireContext()
        val listItemHeight: Int = com.qmuiteam.qmui.util.QMUIResHelper.getAttrDimen(context, com.qmuiteam.qmui.R.attr.qmui_list_item_height)
        val imageDrawable: Drawable? = null
        val aboutListItem: QMUICommonListItemView = qmuiGroupListView.createItemView(imageDrawable, "作者", "Sollyu", QMUICommonListItemView.HORIZONTAL, QMUICommonListItemView.ACCESSORY_TYPE_NONE, listItemHeight)
        val versionListItem: QMUICommonListItemView = qmuiGroupListView.createItemView(imageDrawable, "版本", BuildConfig.VERSION_NAME, QMUICommonListItemView.HORIZONTAL, QMUICommonListItemView.ACCESSORY_TYPE_NONE, listItemHeight)
        val linceseListItem: QMUICommonListItemView = qmuiGroupListView.createItemView(imageDrawable, "LICENE", BuildConfig.VERSION_NAME, QMUICommonListItemView.HORIZONTAL, QMUICommonListItemView.ACCESSORY_TYPE_NONE, listItemHeight)

        QMUIGroupListView.Section(context)
            .setTitle(Constant.EMPTY_STRING)
            .addItemView(aboutListItem, null)
            .addItemView(versionListItem, null)
            .addItemView(linceseListItem, null)
            .addTo(qmuiGroupListView)
    }

}