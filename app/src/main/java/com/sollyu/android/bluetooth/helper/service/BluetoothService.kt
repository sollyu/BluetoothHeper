package com.sollyu.android.bluetooth.helper.service

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BluetoothService : Service() {
    private val binder: Binder = Binder()
    private val readerRunnable: ReaderRunnable = ReaderRunnable()
    private val readerLiveData: MutableLiveData<ByteArray> = MutableLiveData()
    private val writerLiveDate: MutableLiveData<ByteArray> = MutableLiveData()

    private var readSize: Long = 0
    private var writeSize: Long = 0
    private val readerObserver = ReaderObserver()
    private val writerObserver = WriterObserver()

    override fun onBind(intent: Intent?): IBinder = binder


    /**
     * 链接一个设备
     *
     * @param macAddress 蓝牙地址
     */
    fun connect(macAddress: String) {
        Thread(readerRunnable).start()
    }

    /**
     * 断开一个已经链接的设备
     */
    fun disconnect() {
        readerLiveData.removeObserver(readerObserver)
        writerLiveDate.removeObserver(writerObserver)
    }

    /**
     * 写入数据
     *
     * @param byteArray 写入内容
     */
    fun write(byteArray: ByteArray?) {
        writerLiveDate.postValue(byteArray)
    }

    fun getReader(): LiveData<ByteArray> = readerLiveData

    fun getWriter(): LiveData<ByteArray> = writerLiveDate

    inner class Binder : android.os.Binder() {
        fun getService(): BluetoothService = this@BluetoothService
    }

    /**
     * 蓝牙读取线程
     */
    private inner class ReaderRunnable : Runnable {
        override fun run() {
            (0..10).forEach {
                readerLiveData.postValue(it.toString().toByteArray())
                Thread.sleep(1000)
            }
        }
    }

    private inner class ReaderObserver : Observer<ByteArray> {
        override fun onChanged(t: ByteArray?) {
            this@BluetoothService.readSize += t?.size?.toLong() ?: 0L
        }
    }

    private inner class WriterObserver : Observer<ByteArray> {
        override fun onChanged(t: ByteArray?) {
            this@BluetoothService.writeSize += t?.size?.toLong() ?: 0L
        }
    }
}