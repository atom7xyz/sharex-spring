package xyz.atom7.sharexspring.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.kotlin.given
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.bean.override.mockito.MockitoBean
import xyz.atom7.sharexspring.BaseIntegrationTest
import xyz.atom7.sharexspring.config.properties.app.PublicProperties
import xyz.atom7.sharexspring.domain.entities.ShortenedUrl
import xyz.atom7.sharexspring.domain.repositories.UrlRepository
import xyz.atom7.sharexspring.dto.request.ShortenedUrlRequestDto
import xyz.atom7.sharexspring.dto.response.ShortenedUrlResponseDto
import xyz.atom7.sharexspring.services.cache.CacheSection
import xyz.atom7.sharexspring.services.cache.CacheService
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class UrlShortenerServiceTest(
    private val urlShortenerService: UrlShortenerService,
): BaseIntegrationTest() {

    @MockitoBean
    private lateinit var urlRepository: UrlRepository

    @MockitoBean
    private lateinit var cacheService: CacheService

    @MockitoBean
    private lateinit var publicProperties: PublicProperties

    @Test
    fun `shortenUrl should return a new shortened url`() {
        val dto = ShortenedUrlRequestDto(url = "https://example.com")
        val saveAndFlushResult = ShortenedUrl(
            originUrl = dto.url,
            targetUrl = "r4nd0m"
        )

        given {
            cacheService.get(CacheSection.SHORTENED_URL,
                "origin->${dto.url}",
                ShortenedUrl::class)
        }.willReturn(null)

        given {
            urlRepository.saveAndFlush(any())
        }.willReturn(saveAndFlushResult)

        val result = urlShortenerService.shortenUrl(dto)
        val expected = ShortenedUrlResponseDto(
            publicProperties.shortenedUrls + saveAndFlushResult.targetUrl
        )

        assertEquals(expected, result)
    }

    @Test
    fun `shortenUrl should return an existing shortened url from cache`() {
        val dto = ShortenedUrlRequestDto(url = "https://example.com")
        val cacheValue = ShortenedUrl(
            originUrl = dto.url,
            targetUrl = "r4nd0m"
        )

        given {
            cacheService.get(CacheSection.SHORTENED_URL,
                "origin->${dto.url}",
                ShortenedUrl::class)
        }.willReturn(cacheValue)

        val result = urlShortenerService.shortenUrl(dto)
        val expected = ShortenedUrlResponseDto(
            publicProperties.shortenedUrls + cacheValue.targetUrl
        )

        assertEquals(expected, result)
    }

    @Test
    fun `getUrl should return an existing shortened url from cache`() {
        val targetUrl = "r4nd0m"
        val cachedValue = ShortenedUrl(
            originUrl = "https://example.com",
            targetUrl = "r4nd0m"
        )

        given {
            cacheService.get(CacheSection.SHORTENED_URL,
                "target->${targetUrl}",
                ShortenedUrl::class)
        }.willReturn(cachedValue)

        val result = urlShortenerService.getUrl(targetUrl)
        val expected = ShortenedUrlResponseDto(url = "https://example.com")

        assertEquals(expected, result.get())
    }

    @Test
    fun `getUrl should return an empty value`() {
        val targetUrl = "r4nd0m"

        given {
            cacheService.get(CacheSection.SHORTENED_URL,
                "target->${targetUrl}",
                ShortenedUrl::class)
        }.willReturn(null)

        val result = urlShortenerService.getUrl(targetUrl)
        val expected = Optional.empty<ShortenedUrlResponseDto>()

        assertEquals(expected, result)
    }

} 