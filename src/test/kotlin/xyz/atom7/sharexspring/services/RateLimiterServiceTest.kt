package xyz.atom7.sharexspring.services

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RateLimiterServiceTest 
{
    @Value("\${app.security.rate-limit-action}")
    private var rateLimitAction: Int = 0

    @Value("\${app.security.rate-limit-wrong-api-key}")
    private var rateLimitWrongApiKey: Int = 0

    private val rateLimiterService by lazy {
        RateLimiterService(rateLimitAction, rateLimitWrongApiKey)
    }

    @Test
    fun `should not reach rate limit initially`()
    {
        val address = "127.0.0.1"
        assertFalse(rateLimiterService.hasReachedRateLimit(address, RateLimitType.ACTION))
    }

    @Test
    fun `should reach rate limit after exceeding threshold`()
    {
        val address = "127.0.0.1"
        
        repeat(rateLimitAction + 1) {
            rateLimiterService.signHit(address, RateLimitType.ACTION)
        }
        
        assertTrue(rateLimiterService.hasReachedRateLimit(address, RateLimitType.ACTION))
    }

    @Test
    fun `should reset counts after scheduled reset`()
    {
        val address = "127.0.0.1"
        
        repeat(rateLimitAction + 1) {
            rateLimiterService.signHit(address, RateLimitType.ACTION)
        }
        
        rateLimiterService.resetCounts()
        assertFalse(rateLimiterService.hasReachedRateLimit(address, RateLimitType.ACTION))
    }
}