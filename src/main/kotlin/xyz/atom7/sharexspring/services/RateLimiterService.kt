package xyz.atom7.sharexspring.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import xyz.atom7.sharexspring.utils.IpActionsMap
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@Service
@EnableScheduling
class RateLimiterService(
    @Value("\${app.security.rate-limit-action}")
    val rateLimitAction: Int,

    @Value("\${app.security.rate-limit-wrong-api-key}")
    val rateLimitWrongApiKey: Int
) {
    private val actionRequests: IpActionsMap = ConcurrentHashMap()
    private val wrongApiKeyRequests: IpActionsMap = ConcurrentHashMap()

    fun hasReachedRateLimit(address: String, rateLimitType: RateLimitType): Boolean
    {
        return when (rateLimitType)
        {
            RateLimitType.ACTION -> checkRateLimit(address, rateLimitAction, actionRequests)
            RateLimitType.API_KEY -> checkRateLimit(address, rateLimitWrongApiKey, wrongApiKeyRequests)
        }
    }

    fun signHit(address: String, rateLimitType: RateLimitType)
    {
        return when (rateLimitType)
        {
            RateLimitType.ACTION -> incrEntry(address, actionRequests)
            RateLimitType.API_KEY -> incrEntry(address, wrongApiKeyRequests)
        }
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    fun resetCounts()
    {
        actionRequests.clear()
        wrongApiKeyRequests.clear()
    }

    private fun <T : IpActionsMap> incrEntry(address: String, map: T)
    {
        map.putIfAbsent(address, AtomicInteger(0))
        map[address]!!.getAndIncrement()
    }

    private fun <T : IpActionsMap> checkRateLimit(address: String, limit: Int, map: T): Boolean
    {
        map.putIfAbsent(address, AtomicInteger(1))
        return map[address]!!.get() > limit
    }
}