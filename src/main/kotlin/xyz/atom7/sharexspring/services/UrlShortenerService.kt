package xyz.atom7.sharexspring.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import xyz.atom7.sharexspring.config.properties.app.LimitsProperties
import xyz.atom7.sharexspring.config.properties.app.PublicProperties
import xyz.atom7.sharexspring.domain.entities.ShortenedUrl
import xyz.atom7.sharexspring.domain.repositories.UrlRepository
import xyz.atom7.sharexspring.utils.generateRandomString
import java.net.URI

@Service
class UrlShortenerService(
    private val urlRepository: UrlRepository,
    publicProperties: PublicProperties,
    limitsProperties: LimitsProperties,
) {
    private val shortenedUrls: String = publicProperties.shortenedUrls
    private val generatedNameLength: Int = limitsProperties.urlShortener.generatedNameLength

    @Cacheable(value = ["originUrls"], key = "#originUrl")
    fun shortenUrl(originUrl: String): ResponseEntity<String> {
        if (!isValidUrl(originUrl)) {
            return ResponseEntity("Invalid URL format", HttpStatus.BAD_REQUEST)
        }

        val urlFound = urlRepository.findShortenedUrlByOriginUrl(originUrl)

        if (urlFound.isPresent) {
            return ResponseEntity.ok(shortenedUrls + urlFound.get().targetUrl)
        }

        val shortenedUrl = ShortenedUrl(
            originUrl = originUrl,
            targetUrl = findNonOccupiedUrl(generatedNameLength)
        )

        urlRepository.save(shortenedUrl)

        return ResponseEntity.ok(shortenedUrls + shortenedUrl.targetUrl)
    }

    @Cacheable(value = ["targetUrls"], key = "#targetUrl")
    @Throws(ResponseStatusException::class)
    fun getUrl(targetUrl: String): ShortenedUrl? {
        return urlRepository.findShortenedUrlByTargetUrl(targetUrl).orElseThrow {
            NullPointerException("Redirection for this link brings nowhere!")
        }
    }

    private fun findNonOccupiedUrl(length: Int): String {
        var generated = generateRandomString(length)

        try {
            while (getUrl(generated) != null) { // bypass of @Cacheable is intended, no cache pollution!
                generated = generateRandomString(length)
            }
        } catch (_: Exception) {
        }

        return generated
    }

    private fun isValidUrl(url: String): Boolean = runCatching {
        URI.create(url).let { uri ->
            uri.scheme?.lowercase() in listOf("http", "https") && uri.host != null
        }
    }.getOrDefault(false)

}