package xyz.atom7.sharexspring.config.init

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import xyz.atom7.sharexspring.logging.AppLogger

abstract class EarlyContextInitializer(
    logger: AppLogger
) : ApplicationContextInitializer<ConfigurableApplicationContext>, BaseInitializer(logger) {

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        startInitialization()
    }

}