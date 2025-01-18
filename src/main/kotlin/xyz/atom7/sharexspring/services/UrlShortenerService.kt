package xyz.atom7.sharexspring.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import xyz.atom7.sharexspring.entities.ShortenedUrl
import xyz.atom7.sharexspring.repositories.UrlRepository
import xyz.atom7.sharexspring.utils.generateRandomString

@Service
class UrlShortenerService(
    private val urlRepository: UrlRepository,

    @Value("\${app.public.shortened-urls}")
    private val shortenedUrlsPath: String
) {

    @Cacheable(value = ["originUrls"], key = "#originUrl")
    fun shortenUrl(originUrl: String): ResponseEntity<String>
    {
        val urlFound = urlRepository.findShortenedUrlByOriginUrl(originUrl)

        if (urlFound.isPresent) {
            return ResponseEntity.ok(shortenedUrlsPath + urlFound.get().targetUrl)
        }

        val shortenedUrl = ShortenedUrl(
            originUrl = originUrl,
            targetUrl = findNonOccupiedUrl(4) // 14_776_336 different possible URLs
        )

        urlRepository.save(shortenedUrl)

        return ResponseEntity.ok(shortenedUrlsPath + shortenedUrl.targetUrl)
    }

    @Cacheable(value = ["targetUrls"], key = "#targetUrl")
    @Throws(ResponseStatusException::class)
    fun getUrl(targetUrl: String): ShortenedUrl?
    {
        return urlRepository.findShortenedUrlByTargetUrl(targetUrl).orElseThrow {
            NullPointerException("Redirection for this link brings nowhere!")
        }
    }

    private fun findNonOccupiedUrl(length: Int): String
    {
        var generated = generateRandomString(length)

        try
        {
            while (getUrl(generated) != null) { // bypass of @Cacheable is intended, no cache pollution!
                generated = generateRandomString(length)
            }
        }
        catch (_: Exception) { }

        return generated
    }

}