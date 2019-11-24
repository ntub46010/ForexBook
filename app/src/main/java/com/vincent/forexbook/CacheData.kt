package com.vincent.forexbook

import java.util.*

class CacheData<T>(val data: T,
                   private val expiredTime: Date) {

    fun isExpired() = Date().after(expiredTime)

}