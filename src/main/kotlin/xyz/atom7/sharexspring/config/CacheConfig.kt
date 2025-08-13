package xyz.atom7.sharexspring.config

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Scheduler
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.atom7.sharexspring.config.properties.AppProperties
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Configuration
class CacheConfig(
    private val appProperties: AppProperties
) {

    @Bean
    fun caffeineConfig(scheduler: Scheduler): Caffeine<Any, Any> {
        return Caffeine.newBuilder()
            .expireAfterAccess(appProperties.caching.ttl, TimeUnit.MINUTES)
            .scheduler(scheduler)
    }

    @Bean
    fun cacheManager(caffeine: Caffeine<Any, Any>): CacheManager {
        val cacheManager = CaffeineCacheManager()
        cacheManager.setCaffeine(caffeine)
        cacheManager.setCacheNames(setOf("originUrls", "targetUrls"))
        return cacheManager
    }

    @Bean
    fun cacheCleanupScheduler(): Scheduler {
        return Scheduler.forScheduledExecutorService(
            Executors.newSingleThreadScheduledExecutor()
        )
    }

}