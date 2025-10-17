package xyz.atom7.sharexspring.services.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.stereotype.Service
import xyz.atom7.sharexspring.config.properties.app.SecurityProperties
import java.util.*

@Service
class JwtService(
    securityProperties: SecurityProperties
) {
    private val secret = securityProperties.jwt.secret
    private val expirationMillis = securityProperties.jwt.expiration * 60 * 1000
    private val algorithm = securityProperties.jwt.algorithm.block(secret)

    fun validateToken(token: String): DecodedJWT? {
        return try {
            JWT.require(algorithm).build().verify(token)
        } catch (_: Exception) {
            null
        }
    }

    fun generateFilePathToken(user: String, filePath: String): String {
        return generateToken(user, mapOf(Pair("filePath", filePath)))
    }

    fun extractFilePath(token: String): String? {
        return validateToken(token)?.getClaim("filePath")?.asString()
    }

    private fun generateToken(user: String, map: Map<String, String>): String {
        val builder = JWT.create()
            .withSubject(user)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + expirationMillis))

        map.forEach { (key, value) -> builder.withClaim(key, value) }

        return builder.sign(algorithm)
    }

}