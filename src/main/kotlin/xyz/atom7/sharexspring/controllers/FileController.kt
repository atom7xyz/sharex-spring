package xyz.atom7.sharexspring.controllers

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import xyz.atom7.sharexspring.annotations.aspects.Log
import xyz.atom7.sharexspring.dto.request.FileUploadRequestDto
import xyz.atom7.sharexspring.services.UploadService

@RestController
@RequestMapping("/share/u")
@Validated
class FileController(
    private val uploadService: UploadService
) {

    @Log(action = "File uploaded", includeArgs = true)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun uploadFile(
        @Valid @ModelAttribute file: FileUploadRequestDto,
        @RequestHeader("X-API-USER") user: String
    ): ResponseEntity<String> {
        return uploadService.uploadFile(file, user)
    }

    @Log(action = "File served", includeArgs = true)
    @GetMapping("/{file}")
    fun getFile(
        @PathVariable file: String
    ): ResponseEntity<Any> {
        return uploadService.getFile(file)
    }

}