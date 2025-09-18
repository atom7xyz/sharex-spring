package xyz.atom7.sharexspring.config.cache

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Scheduler
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.cache.support.CompositeCacheManager
import org.springframework.context.annotation.*
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.RedisSerializationContext
import xyz.atom7.sharexspring.logging.AppLogger
import xyz.atom7.sharexspring.services.cache.CacheSection
import java.util.concurrent.Executors

@Configuration
@EnableCaching
class CacheConfig(
    private val logger: AppLogger
) {

    @Bean
    @Primary
    fun cacheManager(
        caffeineCacheManager: CacheManager,
        redisCacheManager: CacheManager
    ): CacheManager {
        return CompositeCacheManager(caffeineCacheManager, redisCacheManager)
    }

    @Bean
    @Profile("prod")
    fun caffeineConfigProd(
        @Qualifier("cacheCleanupScheduler") scheduler: Scheduler
    ): Caffeine<Any, Any> {
        return defaultCaffeineConfig(scheduler)
            .recordStats()
    }

    @Bean
    @Scope("prototype")
    @Profile("prod")
    fun caffeineCacheProd(caffeineConfig: Caffeine<Any, Any>): Cache<Any, Any> {
        return caffeineConfig.build()
    }

    @Bean
    @Profile("!prod")
    fun caffeineConfigDev(
        @Qualifier("cacheCleanupScheduler") scheduler: Scheduler
    ): Caffeine<Any, Any> {
        return defaultCaffeineConfig(scheduler)
            .recordStats()
            .removalListener<Any, Any> { key, value, cause ->
                logger.debug("Cache removal of K: $key - V: $value with cause: $cause")
            }
            .evictionListener { key, value, cause ->
                logger.debug("Cache eviction of K: $key - V: $value with cause: $cause")
            }
    }

    @Bean
    @Scope("prototype")
    @Profile("!prod")
    fun caffeineCacheDev(caffeineConfig: Caffeine<Any, Any>): Cache<Any, Any> {
        return caffeineConfig.build()
    }

    @Bean
    fun caffeineCacheManager(caffeineConfig: Caffeine<Any, Any>): CacheManager {
        val cacheManager = CaffeineCacheManager()
        cacheManager.setCaffeine(caffeineConfig)
        cacheManager.setCacheNames(setOf(CacheSection.API_KEY.value))
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
            .scheduler(scheduler)
    }

    @Bean
    fun redisCacheManager(
        redisConnectionFactory: RedisConnectionFactory,
        keySerializer: RedisSerializationContext.SerializationPair<String?>,
        jacksonValueSerializer: RedisSerializationContext.SerializationPair<Any>
    ): CacheManager {
        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(defaultRedisConfig(keySerializer, jacksonValueSerializer))
            .build()
    }

    private fun defaultRedisConfig(
        keySerializer: RedisSerializationContext.SerializationPair<String?>,
        jacksonValueSerializer: RedisSerializationContext.SerializationPair<Any>
    ): RedisCacheConfiguration {
        return RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(keySerializer)
            .serializeValuesWith(jacksonValueSerializer)
    }

}