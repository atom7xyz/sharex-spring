package xyz.atom7.sharexspring.config.properties.app

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.limits")
class LimitsProperties {

    var fileUploader: FileUploader = FileUploader()
    var urlShortener: UrlShortener = UrlShortener()

    class FileUploader {
        var generatedNameLength: Int = 8
        var size: Long = 51200
        var allowedTypes: Array<String> = arrayOf(
            "image/jpeg",
            "image/png",
            "image/gif",
            "application/pdf",
            "text/plain",
            "video/mp4",
        )
    }

    class UrlShortener {
        var generatedNameLength: Int = 4
    }

}