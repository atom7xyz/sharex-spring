package xyz.atom7.sharexspring.config.properties

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import xyz.atom7.sharexspring.config.properties.app.LimitsProperties
import xyz.atom7.sharexspring.config.properties.app.PublicProperties
import xyz.atom7.sharexspring.config.properties.app.SecurityProperties

@Component
@ConfigurationProperties(prefix = "app")
class AppProperties {

    @Autowired
    lateinit var securityProperties: SecurityProperties

    @Autowired
    lateinit var publicProperties: PublicProperties

    @Autowired
    lateinit var limitsProperties: LimitsProperties

    var file: File = File()

    var caching: Caching = Caching()

    class File {
        var uploadDirectory: String = "./uploads"
    }

    class Caching {
        var ttl: Long = 60
    }
}