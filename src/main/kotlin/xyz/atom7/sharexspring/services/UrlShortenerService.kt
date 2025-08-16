package xyz.atom7.sharexspring.services

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import xyz.atom7.sharexspring.config.properties.app.LimitsProperties
import xyz.atom7.sharexspring.config.properties.app.PublicProperties
import xyz.atom7.sharexspring.domain.entities.ShortenedUrl
import xyz.atom7.sharexspring.domain.repositories.UrlRepository
import xyz.atom7.sharexspring.dto.ShortenUrlRequestDto
import xyz.atom7.sharexspring.dto.ShortenUrlResponseDto
import xyz.atom7.sharexspring.utils.generateRandomString
import java.util.*

@Service
class UrlShortenerService(
    private val urlRepository: UrlRepository,
    publicProperties: PublicProperties,
    limitsProperties: LimitsProperties
) {
    private val shortenedUrls: String = publicProperties.shortenedUrls
    private val generatedNameLength: Int = limitsProperties.urlShortener.generatedNameLength

    @Cacheable(value = ["shortenUrlReqDto"], key = "#dto")
    fun shortenUrl(dto: ShortenUrlRequestDto): ShortenUrlResponseDto {
        val urlFound = urlRepository.findShortenedUrlByOriginUrl(dto.url)

        if (urlFound.isPresent) {
            return ShortenUrlResponseDto(
                shortenedUrls + urlFound.get().targetUrl
            )
        }

        val shortenedUrl = ShortenedUrl(
            originUrl = dto.url,
            targetUrl = findNonOccupiedUrl(generatedNameLength)
        )

        urlRepository.save(shortenedUrl)

        return ShortenUrlResponseDto(
            shortenedUrls + shortenedUrl.targetUrl
        )
    }

    @Cacheable(value = ["targetUrls"], key = "#targetUrl")
    @Throws(ResponseStatusException::class)
    fun getUrl(targetUrl: String): Optional<ShortenedUrl> {
        return urlRepository.findShortenedUrlByTargetUrl(targetUrl)
    }

    private fun findNonOccupiedUrl(length: Int): String {
        var generated = generateRandomString(length)

        while (getUrl(generated).isPresent) { // bypass of @Cacheable is intended, no cache pollution!
            generated = generateRandomString(length)
        }

        return generated
    }

}