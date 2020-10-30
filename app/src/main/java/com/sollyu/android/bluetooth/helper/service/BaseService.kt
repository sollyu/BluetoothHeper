package com.sollyu.android.bluetooth.helper.service

import android.app.Service
import com.trello.rxlifecycle4.LifecycleProvider
import com.trello.rxlifecycle4.LifecycleTransformer
import com.trello.rxlifecycle4.RxLifecycle
import com.trello.rxlifecycle4.android.ActivityEvent
import com.trello.rxlifecycle4.android.RxLifecycleAndroid
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

abstract class BaseService: Service(), LifecycleProvider<ActivityEvent> {
    private val lifecycleSubject: BehaviorSubject<ActivityEvent> = BehaviorSubject.create()

    override fun lifecycle(): Observable<ActivityEvent> = lifecycleSubject.hide()

    override fun <T : Any?> bindUntilEvent(event: ActivityEvent): LifecycleTransformer<T> = RxLifecycle.bindUntilEvent(lifecycleSubject, event)

    override fun <T : Any?> bindToLifecycle(): LifecycleTransformer<T> = RxLifecycleAndroid.bindActivity(lifecycleSubject)

    override fun onCreate() {
        super.onCreate()
        lifecycleSubject.onNext(ActivityEvent.CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleSubject.onNext(ActivityEvent.DESTROY)
    }
}