package com.vincent.forexbook.cache

import java.util.*

interface ICacheService<K, V> {

    val cacheMap: MutableMap<K, CacheData<V>>

    fun saveCache(key: K, value: V) {
        cacheMap[key] = CacheData(value)
    }

    fun saveCache(key: K, value: V, expiredTime: Date) {
        cacheMap[key] = CacheData(value, expiredTime)
    }

    fun getCache(key: K): V? {
        val cache = cacheMap[key]

        return when {
            cache == null -> null
            cache.isExpired() -> {
                cacheMap.remove(key)
                null
            }
            else -> cache.data
        }
    }
}