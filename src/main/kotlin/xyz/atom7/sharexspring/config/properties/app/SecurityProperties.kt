package xyz.atom7.sharexspring.config.properties.app

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import xyz.atom7.sharexspring.services.security.jwt.JwtAlgorithm

@Component
@ConfigurationProperties(prefix = "app.security")
class SecurityProperties {

    @Autowired
    lateinit var rateLimitProperties: RateLimitProperties
    var jwt: Jwt = Jwt()

    class Jwt() {
        lateinit var secret: String
        var expiration: Int = 180
        var algorithm: JwtAlgorithm = JwtAlgorithm.HMAC256
    }

}