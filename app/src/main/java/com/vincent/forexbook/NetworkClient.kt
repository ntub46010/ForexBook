package com.vincent.forexbook

import okhttp3.*
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

object NetworkClient {

    private val client: OkHttpClient

    private val cacheInterceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val response = chain.proceed(request)

            val responseBuilder = response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")

            val maxAge = request.cacheControl.maxAgeSeconds
            if (maxAge > 0) {
                responseBuilder.header("Cache-Control", "max-age=$maxAge")
            }

            return responseBuilder.build()
        }
    }

    init {
        val application = MyApplication.INSTANCE
        val cacheFile = File(application.cacheDir, application.packageName)
        val clientCache = Cache(cacheFile, 1024 * 1024)

        client = OkHttpClient().newBuilder()
            .cache(clientCache)
            .addNetworkInterceptor(cacheInterceptor)
            .build()
    }

    fun loadExchangeRate(url: String, cacheMaxAgeTimeMill: Int, callback: GeneralCallback<String>) {
        val timeToLive = if (cacheMaxAgeTimeMill < 0) 0 else cacheMaxAgeTimeMill
        val cacheControl = CacheControl.Builder()
            .maxAge(timeToLive, TimeUnit.MILLISECONDS)
            .build()

        val request = Request.Builder()
            .url(url)
            .cacheControl(cacheControl)
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val responseBodyStr = response.body?.string()
                    callback.onFinish(responseBodyStr)
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onException(e)
                }
            })
    }

}