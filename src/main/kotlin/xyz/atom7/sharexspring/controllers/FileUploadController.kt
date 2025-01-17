package xyz.atom7.sharexspring.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import xyz.atom7.sharexspring.services.UploadService

@RestController
@RequestMapping("/share/api/upload")
class FileUploadController(private val uploadService: UploadService)
{
    @PostMapping
    fun uploadFile(@RequestParam file: MultipartFile): ResponseEntity<String>
    {
        return uploadService.uploadFile(file)
    }
}
