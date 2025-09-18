package xyz.atom7.sharexspring.config.https

import org.springframework.boot.web.server.Ssl
import org.springframework.context.annotation.Configuration
import xyz.atom7.sharexspring.config.properties.ServerProperties
import xyz.atom7.sharexspring.logging.AppLogger
import java.io.File

@Configuration
class SslConfig(
    serverProperties: ServerProperties,
    private val logger: AppLogger
) {
    private val sslProp: ServerProperties.Ssl = serverProperties.ssl

    fun configureSsl(): Ssl? {
        if (!sslProp.enabled) {
            logger.info("SSL is disabled in configuration")
            return null
        }

        val ssl = Ssl()

        when {
            hasPemFilesConfigured() -> {
                logger.debug( "Using PEM certificate files from configuration")
                configurePemSsl(ssl)
            }
            hasKeystoreConfigured() -> {
                logger.debug("Using Java keystore from configuration")
                configureKeystoreSsl(ssl)
            }
            else -> {
                logger.debug("No SSL certificates configured! Please configure either:")
                logger.debug("1. PEM files: server.ssl.certificate and server.ssl.certificate-private-key")
                logger.debug("2. Java keystore: server.ssl.key-store")
                return null
            }
        }

        return ssl
    }

    private fun hasPemFilesConfigured(): Boolean {
        return  sslProp.mode.name == "CERTIFICATE" &&
                sslProp.certificate.isNotEmpty() &&
                sslProp.certificatePrivateKey.isNotEmpty() &&
                fileExistsFromPath(sslProp.certificate) &&
                fileExistsFromPath(sslProp.certificatePrivateKey)
    }

    private fun hasKeystoreConfigured(): Boolean {
        return  sslProp.mode.name == "KEYSTORE" &&
                sslProp.keyStore.isNotEmpty() &&
                fileExistsFromPath(sslProp.keyStore)
    }

    private fun configurePemSsl(ssl: Ssl) {
        ssl.isEnabled = sslProp.enabled
        ssl.certificate = sslProp.certificate
        ssl.certificatePrivateKey = sslProp.certificatePrivateKey

        ssl.protocol = sslProp.protocol
        ssl.enabledProtocols = sslProp.enabledProtocols
        ssl.ciphers = sslProp.ciphers
        ssl.clientAuth = sslProp.clientAuth

        if (sslProp.certificateEncryptionKey.isNotEmpty()) {
            ssl.keyPassword = sslProp.certificateEncryptionKey
        }

        logger.debug("PEM SSL Configuration:")
        logger.debug("  Certificate: ${sslProp.certificate}")
        logger.debug("  Private Key: ${sslProp.certificatePrivateKey}")
        logger.debug("  Protocol: ${sslProp.protocol}")
    }

    private fun configureKeystoreSsl(ssl: Ssl) {
        ssl.isEnabled = sslProp.enabled
        ssl.keyStore = sslProp.keyStore
        ssl.keyStoreType = sslProp.keyStoreType

        val keystorePassword = sslProp.keyStorePassword

        ssl.keyStorePassword = keystorePassword
        ssl.keyPassword = keystorePassword

        if (sslProp.keyAlias.isNotEmpty()) {
            ssl.keyAlias = sslProp.keyAlias
        }

        ssl.protocol = sslProp.protocol
        ssl.enabledProtocols = sslProp.enabledProtocols
        ssl.ciphers = sslProp.ciphers
        ssl.clientAuth = sslProp.clientAuth

        logger.debug("Keystore SSL Configuration:")
        logger.debug("  Keystore: ${sslProp.keyStore}")
        logger.debug("  Type: ${sslProp.keyStoreType}")
        logger.debug("  Alias: ${sslProp.keyAlias}")
        logger.debug("  Protocol: ${sslProp.protocol}")
    }

    private fun fileExistsFromPath(path: String): Boolean {
        return when {
            path.startsWith("classpath:") -> {
                val resourcePath = path.removePrefix("classpath:")
                this::class.java.classLoader.getResource(resourcePath) != null
            }
            path.startsWith("file:") -> {
                val filePath = path.removePrefix("file:")
                File(filePath).exists()
            }
            else -> {
                File(path).exists()
            }
        }
    }

}