package com.vincent.forexbook

import java.util.*

class CacheData<T>(val data: T,
                   val expiredTime: Date) {

    fun isExpired() = Date().after(expiredTime)
    fun isExpired(time: Date) = time.after(expiredTime)

}