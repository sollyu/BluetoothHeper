package com.sollyu.android.bluetooth.helper.fragment

import android.content.Intent
import android.view.View
import com.qmuiteam.qmui.arch.QMUIFragment
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.LifecycleTransformer
import com.trello.rxlifecycle2.RxLifecycle
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.android.RxLifecycleAndroid
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

abstract class BaseFragment : QMUIFragment(), LifecycleProvider<ActivityEvent> {

    private val lifecycleSubject: BehaviorSubject<ActivityEvent> = BehaviorSubject.create()

    override fun lifecycle(): Observable<ActivityEvent> = lifecycleSubject.hide()

    override fun <T : Any?> bindUntilEvent(event: ActivityEvent): LifecycleTransformer<T> = RxLifecycle.bindUntilEvent(lifecycleSubject, event)

    override fun <T : Any?> bindToLifecycle(): LifecycleTransformer<T> = RxLifecycleAndroid.bindActivity(lifecycleSubject)

    override fun onViewCreated(rootView: View) {
        super.onViewCreated(rootView)
        lifecycleSubject.onNext(ActivityEvent.CREATE)
    }

    override fun onStart() {
        super.onStart()
        lifecycleSubject.onNext(ActivityEvent.START)
    }

    override fun onResume() {
        super.onResume()
        lifecycleSubject.onNext(ActivityEvent.RESUME)
    }

    override fun onPause() {
        super.onPause()
        lifecycleSubject.onNext(ActivityEvent.PAUSE)
    }

    override fun onStop() {
        super.onStop()
        lifecycleSubject.onNext(ActivityEvent.STOP)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleSubject.onNext(ActivityEvent.DESTROY)
    }

    fun popBackStack_(): Unit =
        this.popBackStack()

    fun popBackStackResult(resultCode: Int, data: Intent?): Unit {
        this.setFragmentResult(resultCode, data)
        this.popBackStack()
    }
}