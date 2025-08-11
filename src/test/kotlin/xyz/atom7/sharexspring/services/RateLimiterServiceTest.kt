package xyz.atom7.sharexspring.services

import com.github.benmanes.caffeine.cache.Scheduler
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.Executors

@SpringBootTest
class RateLimiterServiceTest
{
    @field:Value("\${app.security.rate-limit-action}")
    private var rateLimitAction: Int = 0

    @field:Value("\${app.security.rate-limit-wrong-api-key}")
    private var rateLimitWrongApiKey: Int = 0

    private val cacheCleanupScheduler = Scheduler.forScheduledExecutorService(
        Executors.newSingleThreadScheduledExecutor()
    )

    private val rateLimiterActionService by lazy {
        RateLimiterActionService(rateLimitAction, cacheCleanupScheduler)
    }

    private val rateLimiterApiKeyService by lazy {
        RateLimiterApiKeyService(rateLimitWrongApiKey, cacheCleanupScheduler)
    }

    @Test
    fun `should not reach rate limit initially for Actions`()
    {
        val address = "127.0.0.1"
        assertFalse(rateLimiterActionService.limitReached(address))
    }

    @Test
    fun `should reach rate limit after exceeding threshold for Actions`()
    {
        val address = "127.0.0.1"
        var consumed = false;
        
        repeat(rateLimitAction + 1) {
            consumed = rateLimiterActionService.consume(address)
        }
        
        assertTrue(consumed)
        assertTrue(rateLimiterActionService.limitReached(address))
    }

    @Test
    fun `should not reach rate limit initially for ApiKey`()
    {
        val address = "127.0.0.1"
        assertFalse(rateLimiterApiKeyService.limitReached(address))
    }

    @Test
    fun `should reach rate limit after exceeding threshold for ApiKey`()
    {
        val address = "127.0.0.1"
        var consumed = false;

        repeat(rateLimitWrongApiKey + 1) {
            consumed = rateLimiterApiKeyService.consume(address)
        }

        assertTrue(consumed)
        assertTrue(rateLimiterApiKeyService.limitReached(address))
    }
}