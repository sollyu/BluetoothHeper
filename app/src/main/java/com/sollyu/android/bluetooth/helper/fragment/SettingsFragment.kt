package com.sollyu.android.bluetooth.helper.fragment

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import com.sollyu.android.bluetooth.helper.R
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment() {
    override fun onCreateView(): View =
        LayoutInflater.from(requireContext()).inflate(R.layout.fragment_settings, baseFragmentActivity.fragmentContainerView, false) as View

    override fun onViewCreated(rootView: View) {
        super.onViewCreated(rootView)
        qmuiTopBarLayout.setTitle(R.string.fragment_settings_title)
        qmuiTopBarLayout.addLeftBackImageButton().setOnClickListener { this.popBackStackResult(resultCode = Activity.RESULT_CANCELED, data = null) }
    }
}