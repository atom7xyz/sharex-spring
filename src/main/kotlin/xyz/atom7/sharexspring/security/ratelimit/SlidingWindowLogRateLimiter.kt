package xyz.atom7.sharexspring.security.ratelimit

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Scheduler
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

open class SlidingWindowLogRateLimiter(
    private val limit: Int,
    private val windowDuration: Duration,
    private val scheduler: Scheduler
) : RateLimiter<String, AtomicInteger> {

    private var windows: Cache<String, WindowState> = Caffeine.newBuilder()
        .expireAfterAccess(windowDuration)
        .scheduler(scheduler)
        .build()

    override fun consume(target: String): Boolean {
        val windowState = getOrCreateWindow(target)

        cleanExpiredRequests(windowState, System.currentTimeMillis())
        signHit(target)

        return limitReached(target)
    }

    override fun limitReached(target: String): Boolean {
        val windowState = windows.getIfPresent(target) ?: return false
        cleanExpiredRequests(windowState, System.currentTimeMillis())
        return windowState.requestLog.size > limit
    }

    override fun signHit(target: String) {
        val currentTime = System.currentTimeMillis()
        val windowState = getOrCreateWindow(target)
        windowState.requestLog.offer(currentTime)
    }

    override fun count(target: String): Int {
        val windowState = windows.getIfPresent(target) ?: return -1
        return windowState.requestLog.size
    }

    override fun getRemainingRequests(target: String): Int {
        val currentCount = count(target)
        return maxOf(0, limit - currentCount)
    }

    override fun getTimeUntilReset(target: String): Duration {
        val windowState = windows.getIfPresent(target) ?: return Duration.ZERO
        val currentTime = System.currentTimeMillis()

        val oldestRequest = windowState.requestLog.peek()

        if (oldestRequest == null) {
            return Duration.ZERO
        }

        val timeUntilExpiry = (oldestRequest + windowDuration.toMillis()) - currentTime
        return Duration.ofMillis(maxOf(0, timeUntilExpiry))
    }

    private fun getOrCreateWindow(target: String): WindowState {
        return windows.get(target) { key -> WindowState() }
    }

    private fun cleanExpiredRequests(windowState: WindowState, currentTime: Long) {
        val cutoffTime = currentTime - windowDuration.toMillis()

        while (true) {
            val oldestRequest = windowState.requestLog.peek()
            if (oldestRequest == null || oldestRequest > cutoffTime) {
                break
            }
            windowState.requestLog.poll()
        }
    }
}