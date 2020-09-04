package com.sollyu.android.bluetooth.helper.fragment

import android.view.LayoutInflater
import android.view.View
import com.sollyu.android.bluetooth.helper.R
import io.reactivex.Observable
import io.reactivex.observers.DisposableObserver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class SplashFragment : BaseFragment() {
    override fun onCreateView(): View =
        LayoutInflater.from(requireContext()).inflate(R.layout.fragment_splash, baseFragmentActivity.fragmentContainerView, false) as View

    override fun onViewCreated(rootView: View) {
        super.onViewCreated(rootView)

        Observable.timer(3, TimeUnit.SECONDS).subscribe(T())
    }

    private inner class T : DisposableObserver<Long>() {
        private val logger: Logger = LoggerFactory.getLogger(this.javaClass)
        override fun onNext(t: Long) {
            logger.info("LOG:T:onNext t={}", t)
        }

        override fun onError(e: Throwable) {
            logger.error("LOG:T:onError", e)
        }

        override fun onComplete() {
            logger.info("LOG:T:onComplete")
            this@SplashFragment.startFragmentAndDestroyCurrent(MainFragment())
        }
    }
}