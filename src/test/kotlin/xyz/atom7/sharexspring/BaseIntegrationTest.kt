package xyz.atom7.sharexspring

import com.redis.testcontainers.RedisContainer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
abstract class BaseIntegrationTest {

    companion object {
        @Container
        @JvmStatic
        val redis: RedisContainer = RedisContainer("redis:7-alpine")
            .withExposedPorts(6379)
            .withCommand("redis-server", "--appendonly", "yes")
            .withReuse(true)

        @Container
        @JvmStatic
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:17-alpine")
            .withDatabaseName("db_sharex")
            .withUsername("sharex_user")
            .withPassword("sharex_password")
            .withReuse(true)

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host") { redis.host }
            registry.add("spring.data.redis.port") { redis.getMappedPort(6379) }

            registry.add("spring.datasource.url") { postgres.jdbcUrl }
            registry.add("spring.datasource.username") { postgres.username }
            registry.add("spring.datasource.password") { postgres.password }
        }
    }

}