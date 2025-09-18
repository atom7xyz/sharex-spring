package xyz.atom7.sharexspring.config.init

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import xyz.atom7.sharexspring.config.properties.app.SecurityProperties
import xyz.atom7.sharexspring.logging.AppLogger
import xyz.atom7.sharexspring.utils.generateRandomString
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.createFile

@Component
class JwtInitializer(
    private val securityProperties: SecurityProperties,
    private val logger: AppLogger
) {

    @PostConstruct
    fun init() {
        if (checkJwtTokenValidity()) {
            return
        }

        val token = generateJwtToken()
        updateEnvFile(token)
        securityProperties.jwt.secret = token
    }

    private fun updateEnvFile(token: String) {
        val envFile = File(".env")
        val properties = Properties()

        if (!envFile.exists()) {
            Path(".env").createFile()
        }

        FileInputStream(envFile).use { input ->
            properties.load(input)
        }

        properties.setProperty("JWT_SECRET", token)

        try {
            FileWriter(envFile, false).use { writer ->
                properties.forEach { (key, value) ->
                    writer.write("$key=$value\n")
                }
            }
            logger.info("JWT secret updated in .env file")
        } catch (e: Exception) {
            logger.error("Failed to write to .env file: ${e.message}")
        }
    }

    private fun generateJwtToken(): String {
        return generateRandomString(32)
    }

    private fun checkJwtTokenValidity(): Boolean {
        return securityProperties.jwt.secret.length == 32
    }

}