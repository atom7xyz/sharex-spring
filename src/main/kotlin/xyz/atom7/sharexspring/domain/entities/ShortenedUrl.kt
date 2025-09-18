package xyz.atom7.sharexspring.domain.entities

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.SourceType
import java.time.LocalDateTime

@Entity
data class ShortenedUrl(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val originUrl: String,

    @Column(nullable = false, unique = true)
    val targetUrl: String,

    @Column(nullable = false)
    val hits: Int = 0,

    @CreationTimestamp(source = SourceType.DB)
    @param:JsonSerialize(using = LocalDateTimeSerializer::class)
    @param:JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val creationDate: LocalDateTime? = null,
)