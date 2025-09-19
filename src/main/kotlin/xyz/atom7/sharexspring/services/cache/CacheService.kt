package xyz.atom7.sharexspring.services.cache

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextClosedEvent
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import xyz.atom7.sharexspring.services.cache.populators.CachePopulator
import kotlin.reflect.KClass

@Service
class CacheService(
    private val cachePopulator: CachePopulator,
    @param:Qualifier("caffeineCacheManager") private val caffeineCacheManager: CacheManager,
    @param:Qualifier("redisCacheManager") private val redisCacheManager: CacheManager,
    private val objectMapper: ObjectMapper,
    private val redisTemplate: RedisTemplate<String, String>
) : ApplicationListener<ContextClosedEvent> {

    @PostConstruct
    fun init() {
    }

    fun destroy() {
        cachePopulator.cleanup()
    }

    override fun onApplicationEvent(event: ContextClosedEvent) {
        destroy()
    }

    fun <K, V> put(
        section: CacheSection,
        key: K,
        value: V
    ) {
        val cache = getCache(section)
            ?: return

        cache.put(key as Any, value)
    }

    @Suppress("UNCHECKED_CAST")
    fun <K, T : Any> get(
        section: CacheSection,
        key: K,
        clazz: KClass<T>
    ): T? {
        val cachedValue = getCache(section)?.get(key as Any)?.get()
            ?: return null

        return when (cachedValue) {
            is LinkedHashMap<*, *> -> objectMapper.convertValue(cachedValue, clazz.java)
            else -> cachedValue as? T?
        }
    }

    fun <V> putSet(
        section: CacheSection,
        value: V
    ) {
        redisTemplate.opsForSet().add(section.value, value as String)
    }

    /**
     * Gets cache based on the CacheSection.
     */
    private fun getCache(section: CacheSection): Cache? {
        return when (section) {
            CacheSection.API_KEY -> caffeineCacheManager.getCache(section.value)
            else -> redisCacheManager.getCache(section.value)
        }
    }

}