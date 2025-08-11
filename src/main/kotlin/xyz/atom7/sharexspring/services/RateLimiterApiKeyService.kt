package xyz.atom7.sharexspring.services

import com.github.benmanes.caffeine.cache.Scheduler
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import xyz.atom7.sharexspring.security.ratelimit.SlidingWindowLogRateLimiter
import java.time.Duration

@Service
class RateLimiterApiKeyService(
    @param:Value("\${app.security.rate-limit-wrong-api-key}")
    val rateLimitWrongApiKey: Int,
    scheduler: Scheduler
) : SlidingWindowLogRateLimiter(rateLimitWrongApiKey, Duration.ofMinutes(1), scheduler)