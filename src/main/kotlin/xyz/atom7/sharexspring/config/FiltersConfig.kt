package xyz.atom7.sharexspring.config

import jakarta.servlet.DispatcherType
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.web.filter.ForwardedHeaderFilter
import xyz.atom7.sharexspring.filters.ApiKeyFilter
import xyz.atom7.sharexspring.filters.RateLimitFilter


@Configuration
class FiltersConfig
{

    @Bean
    fun rateLimitFilterRegistration(rateLimitFilter: RateLimitFilter): FilterRegistrationBean<RateLimitFilter>
    {
        val registrationBean = FilterRegistrationBean<RateLimitFilter>()

        registrationBean.filter = rateLimitFilter
        registrationBean.addUrlPatterns("*")

        return registrationBean
    }

    @Bean
    fun apiKeyFilterRegistration(apiKeyFilter: ApiKeyFilter): FilterRegistrationBean<ApiKeyFilter>
    {
        val registrationBean = FilterRegistrationBean<ApiKeyFilter>()

        registrationBean.filter = apiKeyFilter
        registrationBean.addUrlPatterns("/api/*")

        return registrationBean
    }

    @Bean
    fun forwardedHeaderFilter(): FilterRegistrationBean<ForwardedHeaderFilter>
    {
        val filter = ForwardedHeaderFilter()
        val registration = FilterRegistrationBean(filter)
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR)
        registration.order = Ordered.HIGHEST_PRECEDENCE
        registration.urlPatterns = listOf("*")
        return registration
    }

}