package com.sollyu.android.bluetooth.helper.service

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.util.*

class BluetoothService : Service() {
    private val binder: Binder = Binder()
    private val liveData: MutableLiveData<Action> = MutableLiveData()

    private var readSize: Long = 0
    private var writeSize: Long = 0

    private val SERVER_NAME = "BluetoothHelper"
    private val SERVER_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private var mBluetoothSocket: BluetoothSocket? = null
    private var connectObserver: ConnectObserver? = null

    private var mClientConnectObserver: ClientConnectObserver? = null
    private var mReaderObserver: ReaderObserver? = null

    override fun onBind(intent: Intent?): IBinder = binder

    enum class ActionType { CONNECTING, CONNECTED, DISCONNECT, READ, WRITE }

    data class Action(val action: ActionType, val param1: Any? = null, val param2: Any? = null)

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        connectObserver?.dispose()
    }

    /**
     * 等待被人连接
     */
    private fun connectAsServer() {
        connectObserver?.dispose()
        connectObserver = ConnectObserver()
        ConnectObservable().subscribe(connectObserver)
    }

    /**
     * 主动连接
     *
     * @param bluetoothDevice 蓝牙设备
     */
    fun connectAsClient(bluetoothDevice: BluetoothDevice) {
        mClientConnectObserver?.dispose()
        mClientConnectObserver = ClientConnectObserver()
        ClientConnectObservable(bluetoothDevice)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(mClientConnectObserver)
    }

    /**
     * 断开一个已经链接的设备
     */
    fun disconnect() {
        mBluetoothSocket?.inputStream?.close()
        mBluetoothSocket?.outputStream?.close()
        mBluetoothSocket = null
    }

    fun write(byteArray: ByteArray) {
        mBluetoothSocket?.outputStream?.write(byteArray)
    }

    fun getLiveDate(): LiveData<Action> = liveData

    inner class Binder : android.os.Binder() {
        fun getService(): BluetoothService = this@BluetoothService
    }

    private inner class ConnectObservable : Observable<Unit>() {
        override fun subscribeActual(observer: io.reactivex.rxjava3.core.Observer<in Unit>) {
            val mmServerSocket: BluetoothServerSocket? = bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(SERVER_NAME, SERVER_UUID)
            mBluetoothSocket = mmServerSocket?.accept()
            observer.onComplete()
        }
    }

    private inner class ConnectObserver : DisposableObserver<Unit>() {
        private val logger: Logger = LoggerFactory.getLogger(this.javaClass)
        override fun onNext(t: Unit?) {
            logger.info("LOG:ConnectObserver:onNext:t={} ", t)
        }

        override fun onError(e: Throwable?) {
            logger.error("LOG:ConnectObserver:onError", e)
        }

        override fun onComplete() {
            logger.info("LOG:ConnectObserver:onComplete:")
        }
    }

    /**
     * 使用客户端尝试连接
     */
    private inner class ClientConnectObservable(private val bluetoothDevice: BluetoothDevice) : Observable<BluetoothSocket>() {
        override fun subscribeActual(observer: io.reactivex.rxjava3.core.Observer<in BluetoothSocket>) {
            try {
                val bluetoothSocket: BluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(SERVER_UUID)
                bluetoothSocket.connect()
                observer.onNext(bluetoothSocket)
                observer.onComplete()
            } catch (e: Exception) {
                observer.onError(e)
            }
        }
    }

    private inner class ClientConnectObserver : DisposableObserver<BluetoothSocket>() {
        override fun onStart() {
            super.onStart()
            liveData.postValue(Action(action = ActionType.CONNECTING))
        }

        override fun onNext(t: BluetoothSocket) {
            this@BluetoothService.mBluetoothSocket = t
        }

        override fun onError(e: Throwable) {
            liveData.postValue(Action(action = ActionType.DISCONNECT))
        }

        override fun onComplete() {
            liveData.postValue(Action(action = ActionType.CONNECTED))
            mReaderObserver?.dispose()
            mReaderObserver = ReaderObserver()
            ReaderObservable()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mReaderObserver)
        }
    }

    private inner class ReaderObservable : Observable<ByteArray>() {
        override fun subscribeActual(observer: io.reactivex.rxjava3.core.Observer<in ByteArray>) {
            try {
                val inputStream: InputStream = mBluetoothSocket?.inputStream ?: throw IllegalStateException()
                val byteArray = ByteArray(1024)
                while (true) {
                    val size: Int = inputStream.read(byteArray)
                    observer.onNext(byteArray.take(size).toByteArray())
                }
            } catch (e: Exception) {
                observer.onError(e)
            }
        }
    }

    private inner class ReaderObserver : DisposableObserver<ByteArray>() {
        override fun onNext(t: ByteArray?) {
            liveData.postValue(Action(action = ActionType.READ, param1 = t))
        }

        override fun onError(e: Throwable?) {
            liveData.postValue(Action(action = ActionType.DISCONNECT))
        }

        override fun onComplete() {
        }
    }
}