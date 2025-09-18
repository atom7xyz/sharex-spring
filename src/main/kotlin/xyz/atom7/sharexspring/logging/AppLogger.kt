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
    private val divMessage: String = "#".repeat(64)

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
            " (${joinPoint.args.joinToString { it?.toString() ?: "null" }})"
        } else ""

        logger.info("$action$argsInfo")
    }

    fun error(message: String) {
        logger.error(message)
    }

    fun warning(message: String) {
        logger.warn(message)
    }

    fun info(message: String) {
        logger.info(message)
    }

    fun debug(message: String) {
        logger.debug(message)
    }

    /**
     * Prints a divider message to the logger based on the log level.
     * @param logLevel The level of logging (INFO, WARN, ERROR).
     */
    fun div(logLevel: LogLevel) {
        when (logLevel) {
            LogLevel.INFO -> info(divMessage)
            LogLevel.WARN -> warning(divMessage)
            LogLevel.ERROR -> error(divMessage)
            LogLevel.DEBUG -> debug(divMessage)
        }
    }

}