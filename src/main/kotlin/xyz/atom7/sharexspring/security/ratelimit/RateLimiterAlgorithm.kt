package xyz.atom7.sharexspring.security.ratelimit

enum class RateLimiterAlgorithm {

    SLIDING_WINDOW_LOG, LEAKY_BUCKET, TOKEN_BUCKET

}