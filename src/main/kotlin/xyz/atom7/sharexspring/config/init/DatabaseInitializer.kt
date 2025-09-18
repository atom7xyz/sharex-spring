package xyz.atom7.sharexspring.config.init

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
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

@Component
class DatabaseInitializer(
    private val profileRepository: ProfileRepository,
    private val digestService: DigestService,
    private val logger: AppLogger,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val cacheService: CacheService
) {

    @EventListener(ApplicationReadyEvent::class)
    fun initializeDatabase() {
        createAdminUserIfNotExists()
        applicationEventPublisher.publishEvent(DatabaseInitializedEvent(this))
    }

    private fun createAdminUserIfNotExists() {
        if (profileRepository.existsUserByRole(UserRole.ADMIN)) {
            return
        }

        val adminApiKey = generateAdminApiKey()
        val salt = digestService.generateSalt(16)
        val hashedKey = digestService.hashSaltApiKey(adminApiKey, salt)

        val toSave = Profile(
            role = UserRole.ADMIN,
            keyHash = hashedKey,
            keySalt = salt,
        )
        val profile = profileRepository.saveAndFlush(toSave)
        cacheService.put(CacheSection.PROFILE, profile.id, profile)

        logger.div(LogLevel.WARN)
        logger.warning("IMPORTANT! SAVE THESE CREDENTIALS FOR THE ADMIN ACCESS!")
        logger.warning("X-API-USER: \t${profile.id}")
        logger.warning("X-API-KEY: \t$adminApiKey")
        logger.div(LogLevel.WARN)
    }

    private fun generateAdminApiKey(): String {
        return generateRandomString(32)
    }

}