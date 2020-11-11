package com.sollyu.android.bluetooth.helper.fragment

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.view.LayoutInflater
import android.view.View
import androidx.core.location.LocationManagerCompat
import com.sollyu.android.bluetooth.helper.R
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.observers.DisposableObserver
import java.util.concurrent.TimeUnit

class SplashFragment : BaseFragment() {
    override fun onCreateView(): View =
        LayoutInflater.from(requireContext()).inflate(R.layout.fragment_splash, baseFragmentActivity.fragmentContainerView, false) as View

    override fun onViewCreated(rootView: View) {
        super.onViewCreated(rootView)

        Observable.timer(2, TimeUnit.SECONDS).subscribe(SplashObserver())
    }

    private inner class SplashObserver : DisposableObserver<Long>() {
        override fun onNext(t: Long) {
        }

        override fun onError(e: Throwable) {
        }

        override fun onComplete() {
            val locationManager: LocationManager = this@SplashFragment.requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!LocationManagerCompat.isLocationEnabled(locationManager))
                this@SplashFragment.requireContext().startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            else
                this@SplashFragment.startFragmentAndDestroyCurrent(MainFragment())
        }
    }
}