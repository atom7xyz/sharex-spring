package xyz.atom7.sharexspring.services

import com.github.benmanes.caffeine.cache.Scheduler
import org.springframework.stereotype.Service
import xyz.atom7.sharexspring.config.properties.app.RateLimitProperties
import xyz.atom7.sharexspring.security.ratelimit.SlidingWindowLogRateLimiter
import java.time.Duration

@Service
class RateLimiterApiKeyService(
    rateLimitProperties: RateLimitProperties,
    scheduler: Scheduler
) : SlidingWindowLogRateLimiter(
    rateLimitProperties.wrongApiKey.maxRequests,
    Duration.ofSeconds(rateLimitProperties.wrongApiKey.windowDuration),
    scheduler
)