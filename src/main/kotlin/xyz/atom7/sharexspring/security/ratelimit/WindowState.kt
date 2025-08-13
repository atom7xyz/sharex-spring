package xyz.atom7.sharexspring.security.ratelimit

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * Represents the state of a rate limit window, including the request log and counter.
 *
 * @property requestLog A queue that holds timestamps of requests made within the current window.
 * @property requestCounter An atomic integer that counts the number of requests made in the current window.
 */
data class WindowState(
    val requestLog: ConcurrentLinkedQueue<Long> = ConcurrentLinkedQueue(),
    val requestCounter: AtomicInteger = AtomicInteger(0)
)