package xyz.atom7.sharexspring.events

import org.springframework.context.ApplicationEvent
import xyz.atom7.sharexspring.config.init.DatabaseInitializer

class DatabaseInitializedEvent(source: DatabaseInitializer) : ApplicationEvent(source)