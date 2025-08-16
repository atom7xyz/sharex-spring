package xyz.atom7.sharexspring.controllers

import jakarta.validation.Valid
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import xyz.atom7.sharexspring.dto.FileUploadRequestDto
import xyz.atom7.sharexspring.services.UploadService
import java.nio.file.Files

@RestController
@RequestMapping("/share/u")
@Validated
class FileShareController(
    private val uploadService: UploadService
) {

    @PostMapping
    fun uploadFile(@Valid @ModelAttribute file: FileUploadRequestDto): ResponseEntity<String> {
        return uploadService.uploadFile(file.file)
    }

    @GetMapping("/{file}")
    fun getFile(@PathVariable file: String): Any {
        return try {
            val filePath = uploadService.getFile(file)
            val fileSize = Files.size(filePath)

            val resource = FileSystemResource(filePath)

            val mimeType = Files.probeContentType(filePath) ?: MediaType.APPLICATION_OCTET_STREAM_VALUE
            val mediaType = MediaType.parseMediaType(mimeType)

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=${filePath.fileName}")
                .contentType(mediaType)
                .contentLength(fileSize)
                .body(resource)
        } catch (e: Exception) {
            ResponseEntity(e.message, HttpStatus.NOT_FOUND)
        }
    }

}