package com.sollyu.android.bluetooth.helper.fragment

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import cn.maizz.kotlin.extension.android.content.gotoUrl
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView
import com.sollyu.android.bluetooth.helper.BuildConfig
import com.sollyu.android.bluetooth.helper.R
import com.sollyu.android.bluetooth.helper.app.Application
import com.sollyu.android.bluetooth.helper.bean.Constant
import de.psdev.licensesdialog.LicensesDialog
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
        val licenseListItem: QMUICommonListItemView = qmuiGroupListView.createItemView(imageDrawable, "LICENSE", Constant.EMPTY_STRING, QMUICommonListItemView.HORIZONTAL, QMUICommonListItemView.ACCESSORY_TYPE_NONE, listItemHeight)

        if (Application.Instance.apiGithubReleasesBean != null && Application.Instance.apiGithubReleasesBean?.tagName != BuildConfig.VERSION_NAME) {
            versionListItem.setTipPosition(QMUICommonListItemView.TIP_POSITION_LEFT)
            versionListItem.showRedDot(true)
        }

        QMUIGroupListView.Section(context)
            .setTitle(Constant.EMPTY_STRING)
            .addItemView(aboutListItem, null)
            .addItemView(versionListItem, this::onClickListenerVersion)
            .addTo(qmuiGroupListView)

        QMUIGroupListView.Section(context)
            .setTitle(Constant.EMPTY_STRING)
            .addItemView(licenseListItem, this::onClickListenerVersionLicense)
            .addTo(qmuiGroupListView)
    }

    private fun onClickListenerVersion(view: View) {
        if (Application.Instance.apiGithubReleasesBean != null && Application.Instance.apiGithubReleasesBean?.tagName != BuildConfig.VERSION_NAME) {
            view.context.gotoUrl("https://github.com/sollyu/BluetoothHeper/releases/tag/" + Application.Instance.apiGithubReleasesBean?.tagName)
        } else {
            view.context.gotoUrl("https://github.com/sollyu/BluetoothHeper/releases")
        }
    }

    private fun onClickListenerVersionLicense(view: View) {
        LicensesDialog.Builder(view.context).setNotices(R.raw.licenses).build().show()
    }

}