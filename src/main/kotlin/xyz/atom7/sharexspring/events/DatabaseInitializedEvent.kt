package xyz.atom7.sharexspring.events

import org.springframework.context.ApplicationEvent
import xyz.atom7.sharexspring.config.init.impl.DatabaseInitializer

class DatabaseInitializedEvent(source: DatabaseInitializer) : ApplicationEvent(source)