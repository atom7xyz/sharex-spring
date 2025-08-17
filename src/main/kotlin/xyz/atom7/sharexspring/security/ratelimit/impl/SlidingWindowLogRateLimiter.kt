package xyz.atom7.sharexspring.security.ratelimit.impl

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Scheduler
import xyz.atom7.sharexspring.security.ratelimit.RateLimiter
import xyz.atom7.sharexspring.security.ratelimit.WindowState
import java.time.Duration

open class SlidingWindowLogRateLimiter(
    private val limit: Int,
    private val windowDuration: Duration,
    private val scheduler: Scheduler
) : RateLimiter<String> {

    private var windows: Cache<String, WindowState> = Caffeine.newBuilder()
        .expireAfterAccess(windowDuration.multipliedBy(2))
        .scheduler(scheduler)
        .build()

    override fun consume(target: String): Boolean {
        val limitNotReached = !limitReached(target)

        if (limitNotReached) {
            signHit(target)
        }

        return limitNotReached
    }

    override fun limitReached(target: String): Boolean {
        return count(target) >= limit
    }

    override fun signHit(target: String) {
        val currentTime = System.currentTimeMillis()
        val windowState = getOrCreateWindow(target)

        windowState.requestLog.offer(currentTime)
        windowState.requestCounter.incrementAndGet()
    }

    override fun count(target: String): Int {
        val windowState = windows.getIfPresent(target) ?: return 0
        cleanExpiredRequests(windowState, System.currentTimeMillis())
        return windowState.requestCounter.get()
    }

    override fun getRemainingRequests(target: String): Int {
        return maxOf(0, limit - count(target))
    }

    override fun getTimeUntilReset(target: String): Duration {
        val windowState = windows.getIfPresent(target) ?: return Duration.ZERO
        val currentTime = System.currentTimeMillis()

        val oldestRequest = windowState.requestLog.peek() ?: return Duration.ZERO

        val timeUntilExpiry = (oldestRequest + windowDuration.toMillis()) - currentTime
        return Duration.ofMillis(maxOf(0, timeUntilExpiry))
    }

    private fun getOrCreateWindow(target: String): WindowState {
        return windows.get(target) { _ -> WindowState() }
    }

    private fun cleanExpiredRequests(windowState: WindowState, currentTime: Long) {
        val cutoffTime = currentTime - windowDuration.toMillis()

        while (true) {
            val oldestRequest = windowState.requestLog.peek()
            if (oldestRequest == null || oldestRequest > cutoffTime) {
                break
            }
            windowState.requestLog.poll()
            windowState.requestCounter.decrementAndGet()
        }
    }

}