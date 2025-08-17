package xyz.atom7.sharexspring.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import xyz.atom7.sharexspring.config.properties.app.LimitsProperties
import xyz.atom7.sharexspring.config.properties.app.PublicProperties
import xyz.atom7.sharexspring.domain.entities.ShortenedUrl
import xyz.atom7.sharexspring.domain.repositories.UrlRepository
import xyz.atom7.sharexspring.dto.ShortenUrlRequestDto
import java.util.*

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class UrlShortenerServiceTest(
    private val publicProperties: PublicProperties,
    private val limitsProperties: LimitsProperties
) {
    private val shortenedUrlsPath: String = publicProperties.shortenedUrls

    private lateinit var urlShortenerService: UrlShortenerService
    private lateinit var urlRepository: UrlRepository

    @BeforeEach
    fun setup() {
        urlRepository = mock(UrlRepository::class.java)
        urlShortenerService = UrlShortenerService(
            urlRepository,
            publicProperties = publicProperties,
            limitsProperties = limitsProperties
        )
    }

    @Test
    fun `shortenUrl should return existing URL if already shortened`() {
        val originUrl = "https://example.com"
        val targetUrl = "abcd"
        val shortenedUrl = ShortenedUrl(originUrl = originUrl, targetUrl = targetUrl)

        `when`(urlRepository.findShortenedUrlByOriginUrl(originUrl))
            .thenReturn(Optional.of(shortenedUrl))

        val response = urlShortenerService.shortenUrl(ShortenUrlRequestDto(originUrl))

        assertEquals("$shortenedUrlsPath$targetUrl", response.url)
    }

    @Test
    fun `shortenUrl should create new shortened URL if not exists`() {
        val originUrl = "https://example.com"

        `when`(urlRepository.findShortenedUrlByOriginUrl(originUrl))
            .thenReturn(Optional.empty())

        val response = urlShortenerService.shortenUrl(ShortenUrlRequestDto(originUrl))

        assertTrue(response.url.startsWith(shortenedUrlsPath))
        verify(urlRepository).save(any())
    }

} 