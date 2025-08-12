package xyz.atom7.sharexspring.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import xyz.atom7.sharexspring.domain.entities.ShortenedUrl
import xyz.atom7.sharexspring.domain.repositories.UrlRepository
import java.util.*

@SpringBootTest
class UrlShortenerServiceTest {
    private lateinit var urlShortenerService: UrlShortenerService
    private lateinit var urlRepository: UrlRepository

    @field:Value("\${app.public.shortened-urls}")
    private val shortenedUrlsPath: String = ""

    @field:Value("\${app.limits.url-shortener.generated-name-length}")
    private val limitUrlNameLength: Int = 0

    @BeforeEach
    fun setup() {
        urlRepository = mock(UrlRepository::class.java)
        urlShortenerService = UrlShortenerService(
            urlRepository,
            shortenedUrlsPath,
            limitUrlNameLength
        )
    }

    @Test
    fun `shortenUrl should return bad request for invalid URL`() {
        val response = urlShortenerService.shortenUrl("invalid-url")
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `shortenUrl should return existing URL if already shortened`() {
        val originUrl = "https://example.com"
        val targetUrl = "abcd"
        val shortenedUrl = ShortenedUrl(originUrl = originUrl, targetUrl = targetUrl)

        `when`(urlRepository.findShortenedUrlByOriginUrl(originUrl))
            .thenReturn(Optional.of(shortenedUrl))

        val response = urlShortenerService.shortenUrl(originUrl)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("$shortenedUrlsPath$targetUrl", response.body)
    }

    @Test
    fun `shortenUrl should create new shortened URL if not exists`() {
        val originUrl = "https://example.com"

        `when`(urlRepository.findShortenedUrlByOriginUrl(originUrl))
            .thenReturn(Optional.empty())

        val response = urlShortenerService.shortenUrl(originUrl)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertTrue(response.body?.startsWith(shortenedUrlsPath) ?: false)
        verify(urlRepository).save(any())
    }
} 