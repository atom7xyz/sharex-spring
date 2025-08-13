package xyz.atom7.sharexspring.config.properties.app

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.security.rate-limit")
class RateLimitProperties {

    var action: Action = Action()
    var wrongApiKey: WrongApiKey = WrongApiKey()

    class Action {
        var maxRequests: Int = 15
        var windowDuration: Long = 10
    }

    class WrongApiKey {
        var maxRequests: Int = 1
        var windowDuration: Long = 60
    }

}