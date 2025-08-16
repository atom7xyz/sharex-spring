package xyz.atom7.sharexspring.config.cache

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.CacheControl
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.mvc.WebContentInterceptor
import xyz.atom7.sharexspring.config.properties.AppProperties
import java.time.Duration

@Configuration
class HttpCacheConfig(
    appProperties: AppProperties
) : WebMvcConfigurer {

    private val cacheControl = CacheControl.maxAge(
        Duration.ofMinutes(appProperties.caching.ttl)
    ).cachePublic()

    @Bean
    fun httpCacheInterceptor(): WebContentInterceptor {
        val interceptor = WebContentInterceptor()

        interceptor.addCacheMapping(cacheControl, "/share/s/**", "/share/u/**")

        return interceptor
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(httpCacheInterceptor())
    }

}