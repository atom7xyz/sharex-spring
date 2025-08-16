package xyz.atom7.sharexspring.config.cache

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Scheduler
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.Scope
import xyz.atom7.sharexspring.config.properties.AppProperties
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Configuration
class CacheConfig(
    private val appProperties: AppProperties
) {

    @Bean
    @Profile("prod")
    fun caffeineConfigProd(scheduler: Scheduler): Caffeine<Any, Any> {
        return defaultCaffeineConfig(scheduler)
    }

    @Bean
    @Scope("prototype")
    @Profile("prod")
    fun caffeineCacheProd(caffeineConfig: Caffeine<Any, Any>): Cache<Any, Any> {
        return caffeineConfig.build()
    }

    @Bean
    @Profile("!prod")
    fun caffeineConfigDev(scheduler: Scheduler): Caffeine<Any, Any> {
        return defaultCaffeineConfig(scheduler).recordStats()
    }

    @Bean
    @Scope("prototype")
    @Profile("!prod")
    fun caffeineCacheDev(caffeineConfig: Caffeine<Any, Any>): Cache<Any, Any> {
        return caffeineConfig.build()
    }

    @Bean
    fun cacheManager(caffeineConfig: Caffeine<Any, Any>): CacheManager {
        val cacheManager = CaffeineCacheManager()
        cacheManager.setCaffeine(caffeineConfig)
        cacheManager.setCacheNames(setOf("shortenUrlReqDto", "targetUrls"))
        return cacheManager
    }

    @Bean
    fun cacheCleanupScheduler(): Scheduler {
        return Scheduler.forScheduledExecutorService(
            Executors.newSingleThreadScheduledExecutor()
        )
    }

    private fun defaultCaffeineConfig(scheduler: Scheduler): Caffeine<Any, Any> {
        return Caffeine.newBuilder()
            .expireAfterAccess(appProperties.caching.ttl, TimeUnit.MINUTES)
            .maximumSize(appProperties.caching.maximumSize)
            .scheduler(scheduler)
    }

}