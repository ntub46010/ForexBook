package com.vincent.forexbook.cache

import java.util.*

class CacheData<T>(val data: T,
                   private val expiredTime: Date?) {

    constructor(data: T): this(data, null)

    fun isExpired(): Boolean {
        return if (expiredTime == null) false
            else Date().after(expiredTime)
    }

}