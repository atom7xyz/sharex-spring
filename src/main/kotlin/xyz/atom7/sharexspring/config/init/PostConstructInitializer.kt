package xyz.atom7.sharexspring.config.init

import jakarta.annotation.PostConstruct
import xyz.atom7.sharexspring.logging.AppLogger

abstract class PostConstructInitializer(
    logger: AppLogger
): BaseInitializer(logger) {

    @PostConstruct
    fun run() {
        startInitialization()
    }

}