package xyz.atom7.sharexspring.config.init

import xyz.atom7.sharexspring.logging.AppLogger

abstract class BaseInitializer(
    protected val logger: AppLogger
): Initializer {

    fun startInitialization() {
        if (shouldInit()) {
            init()
        }
        then()
    }

}