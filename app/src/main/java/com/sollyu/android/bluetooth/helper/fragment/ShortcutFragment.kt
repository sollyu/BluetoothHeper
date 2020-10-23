package com.sollyu.android.bluetooth.helper.fragment

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import com.google.gson.Gson
import com.sollyu.android.bluetooth.helper.R
import com.sollyu.android.bluetooth.helper.app.Application
import com.sollyu.android.bluetooth.helper.bean.Constant
import com.sollyu.android.bluetooth.helper.bean.ShortcutBean
import kotlinx.android.synthetic.main.fragment_settings.qmuiTopBarLayout
import kotlinx.android.synthetic.main.fragment_shortcut.*

class ShortcutFragment(private val name: String) : BaseFragment() {
    private val gson: Gson = Gson()

    override fun onCreateView(): View =
        LayoutInflater.from(requireContext()).inflate(R.layout.fragment_shortcut, baseFragmentActivity.fragmentContainerView, false) as View

    override fun onViewCreated(rootView: View) {
        super.onViewCreated(rootView)
        qmuiTopBarLayout.setTitle(R.string.fragment_shortcut_title)
        qmuiTopBarLayout.addLeftBackImageButton().setOnClickListener { this.popBackStackResult(resultCode = Activity.RESULT_CANCELED, data = null) }

        btnSave.setOnClickListener(this::onClickListenerSave)
        val save: String = Application.Instance.sharedPreferences.raw.getString(Constant.PREFERENCES_KEY_SHORTCUT + "_" + name, "{}") ?: "{}"
        val shortcutBean: ShortcutBean = gson.fromJson(save, ShortcutBean::class.java)

        edtName.setText(shortcutBean.name)
        cbHex.isChecked = shortcutBean.hex == true
        edtContext.setText(shortcutBean.text)
    }

    private fun onClickListenerSave(view: View) {
        val keyName: String = Constant.PREFERENCES_KEY_SHORTCUT + "_" + name
        val shortcutBean: ShortcutBean = ShortcutBean(name = edtName.text.toString(), hex = cbHex.isChecked, text = edtContext.text.toString())
        Application.Instance.sharedPreferences.raw.edit().putString(keyName, gson.toJson(shortcutBean)).apply()
        popBackStackResult(resultCode = Activity.RESULT_OK, data = null)
    }
}