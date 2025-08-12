package xyz.atom7.sharexspring.controllers

import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import xyz.atom7.sharexspring.services.UploadService
import java.nio.file.Files

@RestController
@RequestMapping("/share/u")
class FileShareController(
    private val uploadService: UploadService
) {

    @PostMapping
    fun uploadFile(@RequestParam file: MultipartFile): ResponseEntity<String> {
        return uploadService.uploadFile(file)
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