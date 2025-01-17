package xyz.atom7.sharexspring.services

import org.springframework.beans.factory.annotation.Value
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
)
{

    fun shortenUrl(originUrl: String): ResponseEntity<String>
    {
        val urlFound = urlRepository.findShortenedUrlByOriginUrl(originUrl)

        if (urlFound.isPresent) {
            return ResponseEntity.ok(shortenedUrlsPath + urlFound.get().targetUrl)
        }

        val shortenedUrl = ShortenedUrl(
            originUrl = originUrl,
            targetUrl = findNonOccupiedUrl(6)
        )

        urlRepository.save(shortenedUrl)

        return ResponseEntity.ok(shortenedUrlsPath + shortenedUrl.targetUrl)
    }

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
            while (getUrl(generated) != null) {
                generated = generateRandomString(length)
            }
        }
        catch (_: Exception) { }

        return generated
    }

}