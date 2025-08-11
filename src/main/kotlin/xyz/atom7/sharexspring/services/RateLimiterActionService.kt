package xyz.atom7.sharexspring.services

import com.github.benmanes.caffeine.cache.Scheduler
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import xyz.atom7.sharexspring.security.ratelimit.SlidingWindowLogRateLimiter
import java.time.Duration

@Service
class RateLimiterActionService(
    @param:Value("\${app.security.rate-limit-action}")
    val rateLimitAction: Int,
    scheduler: Scheduler
) : SlidingWindowLogRateLimiter(rateLimitAction, Duration.ofMinutes(1), scheduler)
