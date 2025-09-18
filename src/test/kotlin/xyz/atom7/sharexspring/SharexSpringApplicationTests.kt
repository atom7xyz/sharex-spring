package xyz.atom7.sharexspring

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import xyz.atom7.sharexspring.services.cache.CacheService

@SpringBootTest
@ActiveProfiles("test")
class SharexSpringApplicationTests: BaseIntegrationTest() {

    @MockitoBean
    private lateinit var cacheService: CacheService

    @Test
    fun contextLoads() {
    }

}
