package xyz.atom7.sharexspring.dto

import java.time.LocalDateTime

data class FileUploadDto(
    val id: Long,
    val path: String,
    val md5: String,
    val uploadDate: LocalDateTime,
    val uploadedBy: ProfileDto
)