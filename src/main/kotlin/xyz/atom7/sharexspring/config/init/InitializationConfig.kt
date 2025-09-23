package xyz.atom7.sharexspring.config.init

import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import xyz.atom7.sharexspring.config.init.impl.DatabaseInitializer
import xyz.atom7.sharexspring.config.init.impl.JwtInitializer
import xyz.atom7.sharexspring.config.init.impl.SpringSecurityInitializer
import xyz.atom7.sharexspring.config.properties.app.SecurityProperties
import xyz.atom7.sharexspring.domain.repositories.ProfileRepository
import xyz.atom7.sharexspring.logging.AppLogger
import xyz.atom7.sharexspring.services.DotEnvService
import xyz.atom7.sharexspring.services.cache.CacheService
import xyz.atom7.sharexspring.services.security.hash.DigestService

@Configuration
class InitializationConfig(
    private val logger: AppLogger,
    private val dotEnvService: DotEnvService
) {

    @Bean
    fun databaseInitializer(
        profileRepository: ProfileRepository,
        digestService: DigestService,
        cacheService: CacheService,
        applicationEventPublisher: ApplicationEventPublisher
    ): DatabaseInitializer {
        return DatabaseInitializer(logger, profileRepository, digestService, cacheService, applicationEventPublisher)
    }

    @Bean
    fun springSecurityInitializer(
        passwordEncoder: PasswordEncoder
    ): SpringSecurityInitializer {
        return SpringSecurityInitializer(logger, dotEnvService, passwordEncoder)
    }

    @Bean
    fun jwtInitializer(
        securityProperties: SecurityProperties
    ): JwtInitializer {
        return JwtInitializer(logger, dotEnvService, securityProperties)
    }

    @Bean
    fun userDetailsService(
        springSecurityInitializer: SpringSecurityInitializer
    ): UserDetailsService {
        val password = dotEnvService.read(springSecurityInitializer.key)

        val user = User.withUsername("admin")
            .password(password)
            .roles("ADMIN")
            .build()

        return InMemoryUserDetailsManager(user)
     }

}