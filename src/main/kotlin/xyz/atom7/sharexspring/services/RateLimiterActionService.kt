package xyz.atom7.sharexspring.services

import com.github.benmanes.caffeine.cache.Scheduler
import org.springframework.stereotype.Service
import xyz.atom7.sharexspring.config.properties.app.RateLimitProperties
import xyz.atom7.sharexspring.security.ratelimit.impl.SlidingWindowLogRateLimiter
import java.time.Duration

@Service
class RateLimiterActionService(
    rateLimitProperties: RateLimitProperties,
    scheduler: Scheduler
) : SlidingWindowLogRateLimiter(
    rateLimitProperties.action.maxRequests,
    Duration.ofSeconds(rateLimitProperties.action.windowDuration),
    scheduler
)
