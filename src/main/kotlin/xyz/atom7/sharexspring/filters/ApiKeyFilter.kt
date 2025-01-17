package xyz.atom7.sharexspring.filters

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import xyz.atom7.sharexspring.logging.AppLogger.div
import xyz.atom7.sharexspring.logging.AppLogger.print
import xyz.atom7.sharexspring.logging.LogLevel
import xyz.atom7.sharexspring.services.RateLimitType
import xyz.atom7.sharexspring.services.RateLimiterService

@Component
@Order(1)
class ApiKeyFilter(
    @Value("\${app.security.api-key}")
    private val validApiKey: String,
    private val rateLimiterService: RateLimiterService
) : Filter
{
    private val HEADER: String = "X-API-KEY"
    private val HEADER_DEFAULT: String = "changeme"

    init {
        if (validApiKey == HEADER_DEFAULT) {
            div(LogLevel.WARN)
            print(LogLevel.WARN, "API Key has default value!")
            print(LogLevel.WARN, "For security reasons, it is recommended to set a unique API key in the config: `app.api-key`")
            div(LogLevel.WARN)
        }
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain)
    {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        val apiKey = httpRequest.getHeader(HEADER)
        val address = httpRequest.remoteAddr

        if (apiKey == validApiKey) {
            chain.doFilter(request, response)
            return
        }

        rateLimiterService.signHit(address, RateLimitType.API_KEY)
        httpResponse.status = HttpServletResponse.SC_UNAUTHORIZED
        httpResponse.writer.write("Invalid or missing API key")
    }
}
