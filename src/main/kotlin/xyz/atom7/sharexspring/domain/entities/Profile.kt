package xyz.atom7.sharexspring.domain.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.util.*

@Entity
data class Profile(
    @Id
    @GeneratedValue(GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val role: UserRole,

    @Column(nullable = false)
    val keyHash: String,

    @Column(nullable = false)
    val keySalt: String,

    @Column(nullable = false)
    val active: Boolean = true,

    @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "uploadedBy", fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE])
    val uploadedFiles: MutableList<FileUpload> = mutableListOf()
) {

    override fun toString(): String {
        return "Profile(id=$id, role=$role, keyHash='$keyHash', keySalt='$keySalt')"
    }

}