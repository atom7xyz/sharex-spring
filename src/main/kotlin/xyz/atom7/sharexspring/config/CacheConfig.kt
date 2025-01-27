package xyz.atom7.sharexspring.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
class CacheConfig
{
    @Bean
    fun caffeineConfig(): Caffeine<Any, Any>
    {
        return Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(100)
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