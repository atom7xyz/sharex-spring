package xyz.atom7.sharexspring.config.init

import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import xyz.atom7.sharexspring.logging.AppLogger
import kotlin.reflect.KClass

abstract class EventListenerInitializer<T: ApplicationEvent>(
    logger: AppLogger
): ApplicationListener<T>, BaseInitializer(logger) {

    abstract val clazz: KClass<T>

    override fun onApplicationEvent(event: T) {
        startInitialization()
    }

}