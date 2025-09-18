package xyz.atom7.sharexspring.services

import org.springframework.stereotype.Service
import xyz.atom7.sharexspring.config.properties.app.LimitsProperties
import xyz.atom7.sharexspring.config.properties.app.PublicProperties
import xyz.atom7.sharexspring.domain.entities.ShortenedUrl
import xyz.atom7.sharexspring.domain.repositories.UrlRepository
import xyz.atom7.sharexspring.dto.request.ShortenedUrlRequestDto
import xyz.atom7.sharexspring.dto.response.ShortenedUrlResponseDto
import xyz.atom7.sharexspring.services.cache.CacheSection
import xyz.atom7.sharexspring.services.cache.CacheService
import xyz.atom7.sharexspring.utils.generateRandomString
import java.util.*

@Service
class UrlShortenerService(
    private val urlRepository: UrlRepository,
    publicProperties: PublicProperties,
    limitsProperties: LimitsProperties,
    private val cacheService: CacheService
) {
    private val shortenedUrls: String = publicProperties.shortenedUrls
    private val generatedNameLength: Int = limitsProperties.urlShortener.generatedNameLength

    fun shortenUrl(
        dto: ShortenedUrlRequestDto
    ): ShortenedUrlResponseDto {
        val cachedValue = cacheService.get(
            CacheSection.SHORTENED_URL,
            "origin->${dto.url}",
            ShortenedUrl::class
        )

        if (cachedValue != null) {
            return ShortenedUrlResponseDto(shortenedUrls + cachedValue.targetUrl)
        }

        val toSave = ShortenedUrl(
            originUrl = dto.url,
            targetUrl = findNonOccupiedUrl(generatedNameLength)
        )
        val savedUrl = urlRepository.saveAndFlush(toSave)
        cacheService.put(CacheSection.SHORTENED_URL, "target->${savedUrl.targetUrl}", savedUrl)
        cacheService.put(CacheSection.SHORTENED_URL, "origin->${savedUrl.originUrl}", savedUrl)

        return ShortenedUrlResponseDto(shortenedUrls + savedUrl.targetUrl)
    }

    fun getUrl(targetUrl: String): Optional<ShortenedUrlResponseDto> {
        val cachedValue = cacheService.get(
            CacheSection.SHORTENED_URL,
            "target->$targetUrl",
            ShortenedUrl::class
        ) ?: return Optional.empty()

        return Optional.of(ShortenedUrlResponseDto(cachedValue.originUrl))
    }

    private fun findNonOccupiedUrl(length: Int): String {
        var generated: String

        do {
            generated = generateRandomString(length)
        } while (getUrl(generated).isPresent)

        return generated
    }

}