package com.vincent.forexbook

interface GeneralCallback<T> {

    fun onFinish(data: T?)

    fun onException(e: Exception)

}