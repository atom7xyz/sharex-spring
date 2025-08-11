package xyz.atom7.sharexspring.security.ratelimit;

import java.time.Duration;

interface RateLimiter<T, G> {

    fun consume(target: T): Boolean

    fun limitReached(target: T): Boolean
    fun signHit(target: T)
    fun count(target: T): Int

    fun getRemainingRequests(target: T): Int
    fun getTimeUntilReset(target: T): Duration
}
