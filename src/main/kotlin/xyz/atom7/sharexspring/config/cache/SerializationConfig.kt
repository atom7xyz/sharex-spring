package xyz.atom7.sharexspring.config.cache

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class SerializationConfig {

    @Bean
    fun jacksonValueSerializer(
        objectMapper: ObjectMapper
    ): RedisSerializationContext.SerializationPair<Any> {
        return RedisSerializationContext.SerializationPair.fromSerializer(
            GenericJackson2JsonRedisSerializer(
                objectMapper
            )
        )
    }

    @Bean
    fun keySerializer(): RedisSerializationContext.SerializationPair<String?> {
        return RedisSerializationContext.SerializationPair.fromSerializer(
            StringRedisSerializer()
        )
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

}