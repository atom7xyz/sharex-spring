package xyz.atom7.sharexspring.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "server")
class ServerProperties {

    var port: Int = 8443
    var http2: Http2 = Http2()
    var ssl: Ssl = Ssl()

    class Http2 {
        var enabled: Boolean = true
    }

    class Ssl {
        var enabled: Boolean = true

        var mode: Mode = Mode.KEYSTORE

        var certificate: String = ""
        var certificatePrivateKey: String = ""
        var certificateEncryptionKey: String = ""

        var keyStore: String = ""
        var keyStorePassword: String = ""
        var keyStoreType: String = ""
        var keyAlias: String = ""

        var protocol: String = "TLS"
        var enabledProtocols: Array<String> = arrayOf(
            "TLSv1.2",
            "TLSv1.3"
        )
        var ciphers: Array<String> = arrayOf(
            "TLS_AES_256_GCM_SHA384",
            "TLS_AES_128_GCM_SHA256",
            "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
            "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"
        )
        var clientAuth: org.springframework.boot.web.server.Ssl.ClientAuth = org.springframework.boot.web.server.Ssl.ClientAuth.NONE
    }

    enum class Mode {
        CERTIFICATE, KEYSTORE
    }

}