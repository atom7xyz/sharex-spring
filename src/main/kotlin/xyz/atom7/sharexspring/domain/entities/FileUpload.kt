package xyz.atom7.sharexspring.domain.entities

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.SourceType
import java.time.LocalDateTime
import java.util.*

@Entity
data class FileUpload(
    @Id
    @GeneratedValue(GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false, unique = true)
    val path: String,

    @Column(nullable = false, unique = true)
    val md5: String,

    @CreationTimestamp(source = SourceType.DB)
    @param:JsonSerialize(using = LocalDateTimeSerializer::class)
    @param:JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val uploadDate: LocalDateTime? = null,

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uploaded_by_id", nullable = false)
    val uploadedBy: Profile?,

    @Column(nullable = true, unique = false)
    val passwordHash: String? = null,

    @Column(nullable = true, unique = false)
    val passwordSalt: String? = null
) {

    override fun toString(): String {
        return "FileUpload(passwordHash=$passwordHash, passwordSalt=$passwordSalt, uploadDate=$uploadDate, md5='$md5', path='$path', id=$id)"
    }

}