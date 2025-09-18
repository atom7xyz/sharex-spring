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

    fun generateFileToken(filePath: String, user: String): String {
        return JWT.create()
            .withSubject(user)
            .withClaim("filePath", filePath)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + expirationMillis))
            .sign(algorithm)
    }

    fun extractFileId(token: String): String? {
        return validateToken(token)?.getClaim("filePath")?.asString()
    }

}