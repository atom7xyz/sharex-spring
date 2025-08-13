package xyz.atom7.sharexspring.config.properties.app

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.public")
class PublicProperties {

    var uploadedFiles: String = "http://localhost:9007/share/u/"
    var shortenedUrls: String = "http://localhost:9007/share/s/"

}