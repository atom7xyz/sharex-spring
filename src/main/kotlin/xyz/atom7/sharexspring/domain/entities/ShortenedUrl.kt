package xyz.atom7.sharexspring.domain.entities

import jakarta.persistence.*

@Entity
data class ShortenedUrl(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val originUrl: String,

    @Column(nullable = false, unique = true)
    val targetUrl: String
)