package xyz.atom7.sharexspring.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import xyz.atom7.sharexspring.logging.AppLogger

@Configuration
@EnableAspectJAutoProxy
class AspectsConfig {

    @Bean
    fun loggingAspect() : AppLogger {
        return AppLogger()
    }

}