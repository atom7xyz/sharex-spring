package xyz.atom7.sharexspring.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class ShortenedUrl(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val originUrl: String,

    val targetUrl: String
) {

    constructor() : this(null, "", "")

    override fun toString(): String {
        return "UrlEntity(id=$id, originUrl='$originUrl', targetUrl='$targetUrl')"
    }

}