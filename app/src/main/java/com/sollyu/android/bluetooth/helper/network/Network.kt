package com.sollyu.android.bluetooth.helper.network

import com.sollyu.android.bluetooth.helper.BuildConfig
import com.sollyu.android.bluetooth.helper.app.Application
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


class Network private constructor() {

    companion object {
        val Instance = Network()
    }

    private val httpLoggingInterceptorLogger: HttpLoggingInterceptor.Logger = object : HttpLoggingInterceptor.Logger {
        private val logger: Logger = LoggerFactory.getLogger(this.javaClass)
        override fun log(message: String): Unit = logger.info("LOG:Network {}", message)
    }

    /**
     * OkHttp缓存
     */
    private class OkHttpCacheOnlineInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            // 头文件中不存在缓存配置
            val cacheDuration: Long = chain.request().header("Cache-Duration")?.toLong() ?: return chain.proceed(chain.request())

            // 获取返回状态信息
            val response: Response = chain.proceed(chain.request())

            // 如果返回的不成功，将不进行缓存
            if (response.code !in 200..399)
                return response

            // 强行增加缓存信息
            return response.newBuilder().addHeader("Cache-Control", "public, max-age=$cacheDuration").removeHeader("Pragma").build()
        }
    }

    /**
     * OkHttp网络缓存
     */
    private val okHttpCache: okhttp3.Cache = okhttp3.Cache(File(Application.Instance.cacheDir, "okhttp"), 50 * 1024 * 1024)

    @Suppress(names = ["DEPRECATION"])
    val okHttpClient: OkHttpClient = OkHttpClient().newBuilder()
        .addInterceptor(HttpLoggingInterceptor(httpLoggingInterceptorLogger).setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE))
        .addNetworkInterceptor(OkHttpCacheOnlineInterceptor())
        .pingInterval(interval = 15, unit = TimeUnit.SECONDS)
        .connectTimeout(timeout = 2, unit = TimeUnit.HOURS)
        .readTimeout(timeout = 1, unit = TimeUnit.MINUTES)
        .writeTimeout(timeout = 1, unit = TimeUnit.MINUTES)
        .retryOnConnectionFailure(retryOnConnectionFailure = true)
        .cache(okHttpCache)
        .build()

    val github: NetworkGithub = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(okHttpClient)
        .build()
        .create(NetworkGithub::class.java)
}
