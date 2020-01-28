package com.vincent.forexbook.cache

import java.util.*

interface ICacheService<K, V> {

    val cacheMap: MutableMap<K, CacheData<V>>

    private fun saveCache(k: K, v: V) {
        cacheMap[k] = CacheData(v)
    }

    private fun saveCache(k: K, v: V, expiredTime: Date) {
        cacheMap[k] = CacheData(v, expiredTime)
    }

    private fun getCache(k: K): V? {
        val cache = cacheMap[k]

        return when {
            cache == null -> null
            cache.isExpired() -> {
                cacheMap.remove(k)
                null
            }
            else -> cache.data
        }
    }
}