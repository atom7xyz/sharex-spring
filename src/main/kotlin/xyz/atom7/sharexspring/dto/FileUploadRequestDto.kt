package xyz.atom7.sharexspring.dto

import org.springframework.web.multipart.MultipartFile
import xyz.atom7.sharexspring.annotations.ValidFileSize
import xyz.atom7.sharexspring.annotations.ValidFileType

data class FileUploadRequestDto(
    @field:ValidFileSize
    @field:ValidFileType
    val file: MultipartFile
)