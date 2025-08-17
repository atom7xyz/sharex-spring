package xyz.atom7.sharexspring.security.filters

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import xyz.atom7.sharexspring.config.properties.app.SecurityProperties
import xyz.atom7.sharexspring.logging.AppLogger
import xyz.atom7.sharexspring.logging.LogLevel
import xyz.atom7.sharexspring.services.RateLimiterApiKeyService
import java.security.MessageDigest

@Component
class ApiKeyFilter(
    private val securityProperties: SecurityProperties,
    private val rateLimiterApiKeyService: RateLimiterApiKeyService,
    logger: AppLogger
) : Filter {

    companion object {
        const val HEADER: String = "X-API-KEY"
        const val HEADER_DEFAULT: String = "changeme"
    }

    init {
        if (securityProperties.apiKey == HEADER_DEFAULT) {
            logger.div(LogLevel.WARN)
            logger.print(LogLevel.WARN, "API Key has default value!")
            logger.print(LogLevel.WARN, "For security reasons set a unique API key in the env: `APP_SECURITY_API-KEY`")
            logger.div(LogLevel.WARN)
        }
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse
        val address = httpRequest.remoteAddr

        val apiKey = httpRequest.getHeader(HEADER)?.trim()

        if (apiKey.isNullOrEmpty()) {
            httpResponse.status = HttpStatus.UNAUTHORIZED.value()
            httpResponse.contentType = "text/plain"
            httpResponse.writer.write("Missing API Key.")
            return
        }

        if (!MessageDigest.isEqual(apiKey.toByteArray(), securityProperties.apiKey.toByteArray())) {
            rateLimiterApiKeyService.consume(address)
            httpResponse.status = HttpStatus.UNAUTHORIZED.value()
            httpResponse.contentType = "text/plain"
            httpResponse.writer.write("Unauthorized.")
            return
        }

        chain.doFilter(httpRequest, httpResponse)
    }

}
