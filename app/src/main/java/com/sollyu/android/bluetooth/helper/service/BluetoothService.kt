package com.sollyu.android.bluetooth.helper.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.trello.rxlifecycle4.android.ActivityEvent
import com.trello.rxlifecycle4.kotlin.bindUntilEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.util.*

class BluetoothService : BaseService() {
    private val binder: Binder = Binder()
    private val liveData: MutableLiveData<Action> = MutableLiveData()

    private val SERVER_NAME = "BluetoothHelper"
    private val SERVER_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private val mBluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private var mBluetoothSocket: BluetoothSocket? = null
    private var mClientConnectObserver: ClientConnectObserver? = null
    private var mServerConnectObserver: ServerConnectObserver? = null

    private var mReaderObserver: ReaderObserver? = null

    override fun onBind(intent: Intent?): IBinder = binder

    enum class ActionType { CONNECTING, CONNECTED, DISCONNECT, READ, WRITE, WAITING }

    data class Action(val action: ActionType, val param1: Any? = null, val param2: Any? = null)

    /**
     * 等待被人连接
     */
    fun startWaitConnect() {
        mServerConnectObserver?.dispose()
        mServerConnectObserver = ServerConnectObserver()
        ServerConnectObservable()
            .bindUntilEvent(this, ActivityEvent.DESTROY)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(mServerConnectObserver)
    }


    /**
     * 主动连接
     *
     * @param bluetoothDevice 蓝牙设备
     */
    fun connectAsClient(bluetoothDevice: BluetoothDevice) {
        mServerConnectObserver?.dispose()
        mClientConnectObserver?.dispose()
        mClientConnectObserver = ClientConnectObserver()
        ClientConnectObservable(bluetoothDevice)
            .bindUntilEvent(this, ActivityEvent.DESTROY)
            .subscribeOn(Schedulers.newThread())
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

    fun isConnect(): Boolean = mBluetoothSocket?.inputStream != null && mBluetoothSocket?.outputStream != null

    fun write(byteArray: ByteArray) {
        if (mBluetoothSocket?.outputStream == null)
            return
        mBluetoothSocket?.outputStream?.write(byteArray)
        liveData.postValue(Action(action = ActionType.WRITE, param1 = byteArray))
    }

    fun getDevice(): BluetoothDevice? = mBluetoothSocket?.remoteDevice

    fun getLiveDate(): LiveData<Action> = liveData

    inner class Binder : android.os.Binder() {
        fun getService(): BluetoothService = this@BluetoothService
    }

    private inner class ServerConnectObservable : Observable<BluetoothSocket>() {
        override fun subscribeActual(observer: io.reactivex.rxjava3.core.Observer<in BluetoothSocket>) {
            try {
                val mmServerSocket: BluetoothServerSocket = mBluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(SERVER_NAME, SERVER_UUID) ?: throw IllegalStateException()
                val bluetoothSocket: BluetoothSocket = mmServerSocket.accept()
                if (bluetoothSocket.isConnected.not())
                    bluetoothSocket.connect()
                mmServerSocket.close()
                observer.onNext(bluetoothSocket)
                observer.onComplete()
            } catch (e: Exception) {
                observer.onError(e)
            }
        }
    }

    private inner class ServerConnectObserver : DisposableObserver<BluetoothSocket>() {
        private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

        override fun onStart() {
            super.onStart()
            liveData.postValue(Action(action = ActionType.WAITING))
        }

        override fun onNext(t: BluetoothSocket?) {
            this@BluetoothService.mBluetoothSocket = t
        }

        override fun onError(e: Throwable) {
            liveData.postValue(Action(action = ActionType.DISCONNECT))
        }

        override fun onComplete() {
            liveData.postValue(Action(action = ActionType.CONNECTED))
            liveData.postValue(Action(action = ActionType.CONNECTED))
            mReaderObserver?.dispose()
            mReaderObserver = ReaderObserver()
            ReaderObservable()
                .bindUntilEvent(this@BluetoothService, ActivityEvent.DESTROY)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mReaderObserver)
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

        private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

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

    /**
     * 读取线程
     */
    private inner class ReaderObserver : DisposableObserver<ByteArray>() {
        override fun onNext(t: ByteArray?) {
            liveData.postValue(Action(action = ActionType.READ, param1 = t))
        }

        override fun onError(e: Throwable?) {
            liveData.postValue(Action(action = ActionType.DISCONNECT))
            mBluetoothSocket?.inputStream?.close()
            mBluetoothSocket?.outputStream?.close()
            mBluetoothSocket = null
        }

        override fun onComplete() {
        }
    }

}