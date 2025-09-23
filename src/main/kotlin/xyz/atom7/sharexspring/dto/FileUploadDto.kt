package xyz.atom7.sharexspring.dto

import java.time.LocalDateTime

data class FileUploadDto(
    val id: Long,
    val path: String,
    val hash: String,
    val uploadDate: LocalDateTime,
    val uploadedBy: ProfileDto
)