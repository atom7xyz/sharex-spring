package xyz.atom7.sharexspring.domain.repositories

import org.springframework.data.jpa.repository.JpaRepository
import xyz.atom7.sharexspring.domain.entities.FileUpload
import java.util.*

interface FileRepository : JpaRepository<FileUpload, Long> {
    fun findFileUploadByMd5(md5: String): Optional<FileUpload>
    fun findFileUploadByPathEquals(path: String): Optional<FileUpload>
}