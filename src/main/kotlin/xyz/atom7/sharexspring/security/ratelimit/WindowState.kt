package xyz.atom7.sharexspring.security.ratelimit

import java.util.concurrent.ConcurrentLinkedQueue

data class WindowState(
    val requestLog: ConcurrentLinkedQueue<Long> = ConcurrentLinkedQueue()
)