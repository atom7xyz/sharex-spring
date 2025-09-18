package xyz.atom7.sharexspring.services

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.bean.override.mockito.MockitoBean
import xyz.atom7.sharexspring.BaseIntegrationTest
import xyz.atom7.sharexspring.config.properties.app.RateLimitProperties
import xyz.atom7.sharexspring.services.cache.CacheService
import xyz.atom7.sharexspring.services.ratelimit.RateLimiterActionService
import xyz.atom7.sharexspring.services.ratelimit.RateLimiterApiKeyService

@SpringBootTest
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class RateLimiterServiceTest(
    private val rateLimitProperties: RateLimitProperties,
    private val rateLimiterActionService: RateLimiterActionService,
    private val rateLimiterApiKeyService: RateLimiterApiKeyService
): BaseIntegrationTest() {

    @MockitoBean
    private lateinit var cacheService: CacheService

    @Test
    fun `should not reach rate limit initially for Actions`() {
        val address = "127.0.0.1"
        assertFalse(rateLimiterActionService.limitReached(address))
    }

    @Test
    fun `should reach rate limit after exceeding threshold for Actions`() {
        val address = "127.0.0.2"
        var consumed = false

        repeat(rateLimitProperties.action.maxRequests + 1) {
            consumed = rateLimiterActionService.consume(address)
        }

        assertFalse(consumed)
        assertTrue(rateLimiterActionService.limitReached(address))
    }

    @Test
    fun `should not reach rate limit initially for ApiKey`() {
        val address = "127.0.0.3"
        assertFalse(rateLimiterApiKeyService.limitReached(address))
    }

    @Test
    fun `should reach rate limit after exceeding threshold for ApiKey`() {
        val address = "127.0.0.4"
        var consumed = false

        repeat(rateLimitProperties.wrongApiKey.maxRequests + 1) {
            consumed = rateLimiterApiKeyService.consume(address)
        }

        assertFalse(consumed)
        assertTrue(rateLimiterApiKeyService.limitReached(address))
    }
}