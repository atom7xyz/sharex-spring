package xyz.atom7.sharexspring.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockMultipartFile
import java.nio.file.Path

@SpringBootTest
class UploadServiceTest
{
    private lateinit var uploadService: UploadService
    
    @TempDir
    lateinit var tempDir: Path
    
    @field:Value("\${app.public.uploaded-files}")
    private val uploadedFilesPath: String = ""

    @field:Value("\${app.limits.file-uploader.generated-name-length}")
    private val limitFileNameLength: Int = 0

    private val limitFileSizeKB: Long = 1024

    @BeforeEach
    fun setup()
    {
        uploadService = UploadService(
            tempDir.toString(),
            uploadedFilesPath,
            limitFileNameLength,
            limitFileSizeKB
        )
    }

    @Test
    fun `uploadFile should reject empty files`()
    {
        val emptyFile = MockMultipartFile("file", "test.txt", "text/plain", ByteArray(0))
        val response = uploadService.uploadFile(emptyFile)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `uploadFile should reject files exceeding size limit`()
    {
        val largeFile = MockMultipartFile(
            "file", 
            "test.txt", 
            "text/plain", 
            ByteArray(limitFileSizeKB.toInt() * 1024 + 1)
        )
        val response = uploadService.uploadFile(largeFile)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `uploadFile should successfully upload valid file`()
     {
        val content = "test content".toByteArray()
        val file = MockMultipartFile("file", "test.txt", "text/plain", content)
        
        val response = uploadService.uploadFile(file)
        
        assertEquals(HttpStatus.OK, response.statusCode)
        assertTrue(response.body?.startsWith(uploadedFilesPath) ?: false)
    }
} 