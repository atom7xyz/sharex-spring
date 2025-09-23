package xyz.atom7.sharexspring.config.init.impl

import org.springframework.security.crypto.password.PasswordEncoder
import xyz.atom7.sharexspring.config.init.PostConstructInitializer
import xyz.atom7.sharexspring.logging.AppLogger
import xyz.atom7.sharexspring.services.DotEnvService
import xyz.atom7.sharexspring.utils.generateRandomString

class SpringSecurityInitializer(
    logger: AppLogger,
    private val dotEnvService: DotEnvService,
    private val passwordEncoder: PasswordEncoder
): PostConstructInitializer(logger) {

    val key = "SPRING_SECURITY_PASSWORD"

    override fun init() {
        val token = generateRandomString(32)
        val encryptedToken = passwordEncoder.encode(token)

        logger.printDiv()
        println("IMPORTANT! INTERNAL LOGIN CREDENTIALS => user: admin - password: $token")
        println("SAVE THEM SOMEWHERE SAFE!")
        println("These credentials are used to access internal endpoints (eg. /actuator/).")
        logger.printDiv()
        dotEnvService.write(key, encryptedToken)
    }

    override fun shouldInit(): Boolean {
        val value = dotEnvService.read(key)
        return value == "secret-will-be-generated-at-launch!" || value.isEmpty()
    }

}