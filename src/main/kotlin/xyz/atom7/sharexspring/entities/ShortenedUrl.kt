package xyz.atom7.sharexspring.entities

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
) {

    override fun toString(): String {
        return "ShortenedUrl(id=$id, originUrl='$originUrl', targetUrl='$targetUrl')"
    }

}