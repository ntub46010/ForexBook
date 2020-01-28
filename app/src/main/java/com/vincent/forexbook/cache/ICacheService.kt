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

    fun removeCache(key: K) {
        cacheMap.remove(key)
    }

    fun loadAllCache(): List<V> {
        cacheMap
            .filter { it.value.isExpired() }
            .forEach { cacheMap.remove(it.key) }

        return cacheMap.values
            .map { it.data }
            .toList()
    }

    fun loadCache(key: K): V? {
        val cache = cacheMap[key]

        return when {
            cache == null -> null
            cache.isExpired() -> {
                removeCache(key)
                null
            }
            else -> cache.data
        }
    }
}