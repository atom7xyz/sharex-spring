package xyz.atom7.sharexspring.security.ratelimit;

import java.time.Duration;

interface RateLimiter<T> {

    /**
     * Consumes the request, returns FALSE if the limit was reached or passed and the request should be blocked,
     * returns TRUE if the request should be allowed.
     *
     * @param target The target for which the rate limit is applied, e.g. an IP address or user ID.
     * @return TRUE if the request is allowed, FALSE if the limit was reached.
     */
    fun consume(target: T): Boolean

    /**
     * Checks if the limit for the target has been reached.
     *
     * @param target The target for which the rate limit is applied.
     * @return TRUE if the limit has been reached, FALSE otherwise.
     */
    fun limitReached(target: T): Boolean

    /**
     * Records a hit for the target, incrementing the request count.
     *
     * @param target The target for which the hit is recorded.
     */
    fun signHit(target: T)

    /**
     * Counts the number of requests made by the target.
     *
     * @param target The target for which the request count is retrieved.
     * @return The number of requests made by the target.
     */
    fun count(target: T): Int

    /**
     * Gets the number of remaining requests allowed for the target.
     *
     * @param target The target for which the remaining requests are calculated.
     * @return The number of remaining requests allowed for the target.
     */
    fun getRemainingRequests(target: T): Int

    /**
     * Gets the time until the rate limit resets for the target.
     *
     * @param target The target for which the reset time is calculated.
     * @return The duration until the rate limit resets for the target.
     */
    fun getTimeUntilReset(target: T): Duration
}
