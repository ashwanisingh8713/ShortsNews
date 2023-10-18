package com.ns.shortsnews.domain.cache

/**
 * Created by Ashwani Kumar Singh on 18,October,2023.
 */
class CachingLinkedHashMap<K, V>(private val maxCacheSize: Int) : LinkedHashMap<K, V>(maxCacheSize + 1, 1.1f, true) {

    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
        return size > maxCacheSize
    }
}