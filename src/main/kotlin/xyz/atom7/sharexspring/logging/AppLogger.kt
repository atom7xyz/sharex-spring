package xyz.atom7.sharexspring.logging

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.atom7.sharexspring.annotations.aspects.Log

@Aspect
class AppLogger {

    private val logger: Logger = LoggerFactory.getLogger(AppLogger::class.java)

    private val DIV_MESSAGE: String = "########################################"

    /**
     * Prints a message to the logger based on the log level.
     * @param logLevel The level of logging (INFO, WARN, ERROR).
     * @param message The message to log.
     */
    fun print(logLevel: LogLevel, message: String) {
        when (logLevel) {
            LogLevel.INFO -> logger.info(message)
            LogLevel.WARN -> logger.warn(message)
            LogLevel.ERROR -> logger.error(message)
        }
    }

    /**
     * Prints a divider message to the logger based on the log level.
     * @param logLevel The level of logging (INFO, WARN, ERROR).
     */
    fun div(logLevel: LogLevel) {
        when (logLevel) {
            LogLevel.INFO -> print(LogLevel.INFO, DIV_MESSAGE)
            LogLevel.WARN -> print(LogLevel.WARN, DIV_MESSAGE)
            LogLevel.ERROR -> print(LogLevel.ERROR, DIV_MESSAGE)
        }
    }

    @After(value = "@annotation(logAnnotation)")
    fun log(joinPoint: JoinPoint, logAnnotation: Log) {
        val methodName = joinPoint.signature.name
        val className = joinPoint.signature.declaringTypeName.substringAfterLast(".")

        // Use custom message if provided, otherwise fall back to method name
        val action = logAnnotation.action.ifBlank {
            "$className.$methodName"
        }

        // Include arguments if requested
        val argsInfo = if (logAnnotation.includeArgs && joinPoint.args.isNotEmpty()) {
            " with args: ${joinPoint.args.joinToString { it?.toString() ?: "null" }}"
        } else ""

        logger.info("$action$argsInfo")
    }

}