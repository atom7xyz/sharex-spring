package xyz.atom7.sharexspring.config

import jakarta.servlet.DispatcherType
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.web.filter.ForwardedHeaderFilter
import xyz.atom7.sharexspring.security.filters.ApiKeyFilter
import xyz.atom7.sharexspring.security.filters.RateLimitFilter

@Configuration
@ComponentScan("xyz.atom7.sharexspring.security.filters")
class FiltersConfig
{

    @Bean
    fun rateLimitFilterRegistration(rateLimitFilter: RateLimitFilter): FilterRegistrationBean<RateLimitFilter>
    {
        val registrationBean = FilterRegistrationBean<RateLimitFilter>()

        registrationBean.filter = rateLimitFilter
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST)
        registrationBean.order = 1

        return registrationBean
    }

    @Bean
    fun apiKeyFilterRegistration(apiKeyFilter: ApiKeyFilter): FilterRegistrationBean<ApiKeyFilter>
    {
        val registrationBean = FilterRegistrationBean<ApiKeyFilter>()

        registrationBean.filter = apiKeyFilter
        registrationBean.addUrlPatterns("/u/", "/s/")
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST)
        registrationBean.order = 2

        return registrationBean
    }

    /**
     * If the application is behind a reverse-proxy / load-balancer this is needed
     * to get the details of the user via the HTTP Headers.
     */
    @Bean
    fun forwardedHeaderFilter(): FilterRegistrationBean<ForwardedHeaderFilter>
    {
        val registrationBean = FilterRegistrationBean(ForwardedHeaderFilter())

        registrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR)
        registrationBean.order = Ordered.HIGHEST_PRECEDENCE

        return registrationBean
    }

}