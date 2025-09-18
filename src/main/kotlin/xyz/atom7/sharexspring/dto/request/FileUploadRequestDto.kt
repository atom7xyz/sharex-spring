package xyz.atom7.sharexspring.dto.request

import org.springframework.web.multipart.MultipartFile
import xyz.atom7.sharexspring.annotations.validation.ValidFileSize
import xyz.atom7.sharexspring.annotations.validation.ValidFileType

data class FileUploadRequestDto(
    @field:ValidFileSize
    @field:ValidFileType
    val file: MultipartFile,
    val password: String?
) {

    override fun toString(): String {
        return "FileUploadRequestDto(" +
                "fileName='${file.originalFilename}', " +
                "size=${file.size} bytes, " +
                "contentType='${file.contentType}')"
    }

}