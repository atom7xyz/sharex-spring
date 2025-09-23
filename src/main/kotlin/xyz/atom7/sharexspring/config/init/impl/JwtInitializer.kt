package xyz.atom7.sharexspring.config.init.impl

import org.springframework.context.annotation.DependsOn
import xyz.atom7.sharexspring.config.init.PostConstructInitializer
import xyz.atom7.sharexspring.config.properties.app.SecurityProperties
import xyz.atom7.sharexspring.logging.AppLogger
import xyz.atom7.sharexspring.services.DotEnvService
import xyz.atom7.sharexspring.utils.generateRandomString

@DependsOn("dotEnvService")
class JwtInitializer(
    logger: AppLogger,
    private val dotEnvService: DotEnvService,
    private val securityProperties: SecurityProperties
): PostConstructInitializer(logger) {

    val key = "JWT_SECRET"

    override fun init() {
        val token = generateRandomString(32)
        dotEnvService.write(key, token)
        securityProperties.jwt.secret = token
    }

    override fun shouldInit(): Boolean {
        val readValue = dotEnvService.read(key)
        return readValue == "secret-will-be-generated-at-launch!" || readValue.length < 32
    }

}