package xyz.atom7.sharexspring.security.filters

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import xyz.atom7.sharexspring.services.RateLimiterActionService
import xyz.atom7.sharexspring.services.RateLimiterApiKeyService

@Component
class RateLimitFilter(
    private val rateLimiterActionService: RateLimiterActionService,
    private val rateLimiterApiKeyService: RateLimiterApiKeyService
) : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        val address = httpRequest.remoteAddr

        val actionLimitExceeded = rateLimiterActionService.consume(address)
        val wrongApiKeyLimitExceeded = rateLimiterApiKeyService.limitReached(address)

        if (!actionLimitExceeded && !wrongApiKeyLimitExceeded) {
            chain.doFilter(request, response)
            return
        }

        httpResponse.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Rate limit exceeded. Try again later.")
    }

}
