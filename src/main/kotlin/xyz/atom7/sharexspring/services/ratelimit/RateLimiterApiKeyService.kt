package xyz.atom7.sharexspring.services.ratelimit

import com.github.benmanes.caffeine.cache.Scheduler
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import xyz.atom7.sharexspring.config.properties.app.RateLimitProperties
import xyz.atom7.sharexspring.security.ratelimit.impl.SlidingWindowLogRateLimiter
import java.time.Duration

@Service
class RateLimiterApiKeyService(
    rateLimitProperties: RateLimitProperties,
    @Qualifier("cacheCleanupScheduler") scheduler: Scheduler
) : SlidingWindowLogRateLimiter(
    rateLimitProperties.wrongApiKey.maxRequests,
    Duration.ofSeconds(rateLimitProperties.wrongApiKey.windowDuration),
    scheduler
)