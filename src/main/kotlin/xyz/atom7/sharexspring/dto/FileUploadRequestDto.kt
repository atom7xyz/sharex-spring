package xyz.atom7.sharexspring.dto

import org.springframework.web.multipart.MultipartFile
import xyz.atom7.sharexspring.annotations.validation.ValidFileSize
import xyz.atom7.sharexspring.annotations.validation.ValidFileType

data class FileUploadRequestDto(
    @field:ValidFileSize
    @field:ValidFileType
    val file: MultipartFile
)