package xyz.atom7.sharexspring.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import xyz.atom7.sharexspring.utils.generateRandomString
import xyz.atom7.sharexspring.utils.getFileExtension
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Service
class UploadService(
    @Value("\${app.file.upload-directory}")
    private val uploadDirectory: String,

    @Value("\${app.public.uploaded-files}")
    private val uploadedFilesPath: String
)
{
    init {
        val path = Paths.get(uploadDirectory)

        if (!Files.exists(path)) {
            Files.createDirectory(path)
        }
    }

    fun uploadFile(file: MultipartFile): ResponseEntity<String>
    {
        if (file.isEmpty) {
            return ResponseEntity("File is empty?!", HttpStatus.BAD_REQUEST)
        }

        return try {
            val fileExtension = getFileExtension(file.originalFilename)
            val path = generateUniqueFileName(fileExtension)
            val fileName = path.fileName.toString()

            file.inputStream.use {
                Files.copy(it, path)
            }

            ResponseEntity("$uploadedFilesPath$fileName", HttpStatus.OK)
        }
        catch (e: Exception) {
            ResponseEntity("Failed to upload file: ${e.message}", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Throws(FileNotFoundException::class)
    fun getFile(file: String): Path
    {
        val filePath = Paths.get(uploadDirectory, file)

        if (!Files.exists(filePath)) {
            throw FileNotFoundException("File not found: $file")
        }

        return filePath
    }

    private fun generateFileName(fileExtension: String): Path
    {
        val fileName = generateRandomString(16) + "." + fileExtension
        val filePath = Paths.get(uploadDirectory, fileName)

        return filePath
    }

    private fun generateUniqueFileName(fileExtension: String): Path
    {
        var generated = generateFileName(fileExtension)

        while (Files.exists(generated)) {
            generated = generateFileName(fileExtension)
        }

        return generated
    }

}