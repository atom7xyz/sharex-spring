package xyz.atom7.sharexspring.config.properties.app

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.security")
class SecurityProperties {

    var apiKey: String = "changeme"

    @Autowired
    lateinit var rateLimitProperties: RateLimitProperties

}