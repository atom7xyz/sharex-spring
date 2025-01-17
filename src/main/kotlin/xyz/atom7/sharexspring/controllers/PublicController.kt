package xyz.atom7.sharexspring.controllers

import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView
import xyz.atom7.sharexspring.services.UploadService
import xyz.atom7.sharexspring.services.UrlShortenerService
import java.nio.file.Files

@RestController
@RequestMapping("/share/")
class PublicController(
    private val uploadService: UploadService,
    private val urlShortenerService: UrlShortenerService
) {

    @GetMapping("/u/{file}")
    fun getFile(@PathVariable file: String): Any
    {
        return try
        {
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
        }
        catch (e: Exception) {
            ResponseEntity(e.message, HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/s/{url}")
    fun gotoTargetUrl(@PathVariable url: String, response: HttpServletResponse): Any
    {
        return try
        {
            val target = urlShortenerService.getUrl(url)!!.originUrl
            RedirectView(target)
        }
        catch (e: Exception) {
            ResponseEntity(e.message, HttpStatus.NOT_FOUND)
        }
    }

}