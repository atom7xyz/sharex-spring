package xyz.atom7.sharexspring.repositories

import org.springframework.data.jpa.repository.JpaRepository
import xyz.atom7.sharexspring.entities.ShortenedUrl
import java.util.*

interface UrlRepository : JpaRepository<ShortenedUrl, Long>
{
    fun findShortenedUrlByOriginUrl(originUrl: String): Optional<ShortenedUrl>
    fun findShortenedUrlByTargetUrl(targetUrl: String): Optional<ShortenedUrl>
}