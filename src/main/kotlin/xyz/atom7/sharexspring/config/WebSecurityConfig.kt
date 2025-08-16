package xyz.atom7.sharexspring.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import xyz.atom7.sharexspring.config.properties.ServerProperties

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val serverProperties: ServerProperties
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .requiresChannel { channel ->
                if (serverProperties.ssl.enabled) {
                    channel.anyRequest().requiresSecure()
                }
            }
            .csrf { csrf -> csrf.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/share/s",
                    "/share/s/*",
                    "/share/u",
                    "/share/u/*",
                    "/actuator/**"
                ).permitAll()

                it.anyRequest().permitAll() // temp
            }
        return http.build()
    }

}