package xyz.atom7.sharexspring.entities

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import java.io.Serializable

@Entity
data class ShortenedUrl(
    @Column(nullable = false)
    val originUrl: String,

    @Column(nullable = false)
    val targetUrl: String
) {
    @EmbeddedId
    private val id: ShortenedUrlId = ShortenedUrlId(originUrl, targetUrl)
}

@Embeddable
data class ShortenedUrlId(
    @Column(nullable = false)
    val originUrl: String,

    @Column(nullable = false)
    val targetUrl: String
) : Serializable