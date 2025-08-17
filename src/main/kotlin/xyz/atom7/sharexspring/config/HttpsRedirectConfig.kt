package xyz.atom7.sharexspring.config

import org.apache.catalina.connector.Connector
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.atom7.sharexspring.config.properties.ServerProperties

@Configuration
class HttpsRedirectConfig(
    private val serverProperties: ServerProperties,
    private val sslConfig: SslConfig
) {

    @Bean
    fun containerCustomizer(): WebServerFactoryCustomizer<TomcatServletWebServerFactory>? {
        return WebServerFactoryCustomizer { factory ->
            if (serverProperties.ssl.enabled) {
                factory.addAdditionalTomcatConnectors(httpToHttpsRedirectConnector())
                factory.ssl = sslConfig.configureSsl()
            }
        }
    }

    private fun httpToHttpsRedirectConnector(): Connector {
        val connector = Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL)

        return connector.apply {
            scheme = "http"
            port = 8080
            secure = false
            redirectPort = serverProperties.port
        }
    }

}