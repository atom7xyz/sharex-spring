package xyz.atom7.sharexspring.services.security.jwt

import com.auth0.jwt.algorithms.Algorithm

enum class JwtAlgorithm(val value: String, val block: (String) -> Algorithm) {
    HMAC256("HMAC256", { Algorithm.HMAC256(it) }),
}
