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
            .authorizeHttpRequests {
                it.requestMatchers("/share/u", "/share/u/**").permitAll()
                it.requestMatchers("/share/s", "/share/s/**").permitAll()
                it.requestMatchers("/actuator/**").hasRole("ADMIN")
                it.anyRequest().authenticated()
            }
            .httpBasic { }
            .csrf {
                it.ignoringRequestMatchers(
                    "/share/s/**",
                    "/share/u/**"
                )
            }
        return http.build()
    }

}