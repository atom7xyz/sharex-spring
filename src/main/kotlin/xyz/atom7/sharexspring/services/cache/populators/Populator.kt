package xyz.atom7.sharexspring.services.cache.populators

import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.SetOperations
import xyz.atom7.sharexspring.exception.impl.CacheNotFoundException
import xyz.atom7.sharexspring.services.cache.CacheSection

interface Populator {

    fun populate()
    fun cleanup()

    @Throws(CacheNotFoundException::class)
    fun <T : Cache> populate(cacheManager: CacheManager, section: CacheSection, block: (T) -> Unit) {
        val cacheName = section.value
        val cache = cacheManager.getCache(cacheName)
            ?: throw CacheNotFoundException(cacheName)

        @Suppress("UNCHECKED_CAST")
        block(cache as T)
    }

    @Throws(CacheNotFoundException::class)
    fun cleanup(cacheManager: CacheManager, section: CacheSection) {
        val cacheName = section.value
        val cache = cacheManager.getCache(cacheName)
            ?: throw CacheNotFoundException(cacheName)

        cache.clear()
    }

    fun <K, V, R : RedisTemplate<K, V>, H : HashOperations<K, String, V>> populateHash(
        redisTemplate: R,
        section: CacheSection,
        block: (H, String) -> Unit
    ) {
        @Suppress("UNCHECKED_CAST")
        block(redisTemplate.opsForHash<K, V>() as H, section.value)
    }

    fun <K, V, R : RedisTemplate<K, V>> cleanupHash(
        redisTemplate: R,
        section: CacheSection
    ) {
        @Suppress("UNCHECKED_CAST")
        val keys = redisTemplate.keys("${section.value}:*" as (K & Any))

        if (keys.isNotEmpty()) {
            redisTemplate.delete(keys)
        }
    }

    fun <K, V, R : RedisTemplate<K, V>, H : SetOperations<K, V>> populateSet(
        redisTemplate: R,
        section: CacheSection,
        block: (H, String) -> Unit
    ) {
        @Suppress("UNCHECKED_CAST")
        block(redisTemplate.opsForSet() as H, section.value)
    }

    fun <K, V, R : RedisTemplate<K, V>> cleanupSet(
        redisTemplate: R,
        section: CacheSection
    ) {
        @Suppress("UNCHECKED_CAST")
        val keys = redisTemplate.keys(section.value as (K & Any))

        if (keys.isNotEmpty()) {
            redisTemplate.delete(keys)
        }
    }


}