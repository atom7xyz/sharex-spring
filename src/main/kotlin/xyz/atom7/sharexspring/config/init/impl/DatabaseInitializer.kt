package xyz.atom7.sharexspring.config.init.impl

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationEventPublisher
import xyz.atom7.sharexspring.config.init.EventListenerInitializer
import xyz.atom7.sharexspring.domain.entities.Profile
import xyz.atom7.sharexspring.domain.entities.UserRole
import xyz.atom7.sharexspring.domain.repositories.ProfileRepository
import xyz.atom7.sharexspring.events.DatabaseInitializedEvent
import xyz.atom7.sharexspring.logging.AppLogger
import xyz.atom7.sharexspring.logging.LogLevel
import xyz.atom7.sharexspring.services.cache.CacheSection
import xyz.atom7.sharexspring.services.cache.CacheService
import xyz.atom7.sharexspring.services.security.hash.DigestService
import xyz.atom7.sharexspring.utils.generateRandomString

class DatabaseInitializer(
    logger: AppLogger,
    private val profileRepository: ProfileRepository,
    private val digestService: DigestService,
    private val cacheService: CacheService,
    private val applicationEventPublisher: ApplicationEventPublisher
): EventListenerInitializer<ApplicationReadyEvent>(logger) {

    override val clazz = ApplicationReadyEvent::class

    override fun init() {
        createAdminUserIfNotExists()
    }

    override fun shouldInit(): Boolean {
        return !profileRepository.existsUserByRole(UserRole.ADMIN)
    }

    override fun then() {
        applicationEventPublisher.publishEvent(DatabaseInitializedEvent(this))
    }

    private fun createAdminUserIfNotExists() {
        val adminApiKey = generateRandomString(32)
        val salt = digestService.generateSalt(16)
        val hashedKey = digestService.hashSaltApiKey(adminApiKey, salt)

        val toSave = Profile(
            role = UserRole.ADMIN,
            keyHash = hashedKey,
            keySalt = salt,
        )
        val profile = profileRepository.saveAndFlush(toSave)

        logger.div(LogLevel.WARN)
        logger.warning("IMPORTANT! SAVE THESE CREDENTIALS FOR THE ADMIN ACCESS!")
        logger.warning("X-API-USER: \t${profile.id}")
        logger.warning("X-API-KEY: \t$adminApiKey")
        logger.div(LogLevel.WARN)
    }

}