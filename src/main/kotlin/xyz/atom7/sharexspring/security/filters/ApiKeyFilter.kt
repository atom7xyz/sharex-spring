package xyz.atom7.sharexspring.security.filters

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import xyz.atom7.sharexspring.services.ratelimit.RateLimiterApiKeyService
import xyz.atom7.sharexspring.services.security.ApiKeyValidationService
import xyz.atom7.sharexspring.utils.AuthHeader

@Component
class ApiKeyFilter(
    private val rateLimiterApiKeyService: RateLimiterApiKeyService,
    private val apiKeyValidationService: ApiKeyValidationService
) : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        val address = httpRequest.remoteAddr

        val user = httpRequest.getHeader(AuthHeader.X_API_USER.value)?.trim()
        val apiKey = httpRequest.getHeader(AuthHeader.X_API_KEY.value)?.trim()

        if (!apiKeyValidationService.checkUserApiKeyValidity(user, apiKey)) {
            rateLimiterApiKeyService.consume(address)
            httpResponse.status = HttpStatus.UNAUTHORIZED.value()
            httpResponse.contentType = "text/plain"
            httpResponse.writer.write("Unauthorized.")
            return
        }

        chain.doFilter(httpRequest, httpResponse)
    }

}
