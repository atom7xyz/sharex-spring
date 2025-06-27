package xyz.atom7.sharexspring.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
class CacheConfig(
    @param:Value("\${app.caching.ttl}")
    private val cacheTtl: Long,

    @param:Value("\${app.caching.size}")
    private val cacheSize: Long,
) {
    @Bean
    fun caffeineConfig(): Caffeine<Any, Any>
    {
        return Caffeine.newBuilder()
            .expireAfterWrite(cacheTtl, TimeUnit.MINUTES)
            .maximumSize(cacheSize)
    }

    @Bean
    fun cacheManager(caffeine: Caffeine<Any, Any>): CacheManager
    {
        val cacheManager = CaffeineCacheManager()
        cacheManager.setCaffeine(caffeine)
        cacheManager.setCacheNames(setOf("originUrls", "targetUrls"))
        return cacheManager
    }
}