package com.vincent.forexbook

import okhttp3.*
import java.io.IOException

object NetworkClient {

    private val client = OkHttpClient().newBuilder().build()

    fun loadExchangeRate(url: String, callback: GeneralCallback<String>) {
        val request = Request.Builder()
            .url(url)
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