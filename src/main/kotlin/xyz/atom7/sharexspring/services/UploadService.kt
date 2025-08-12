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
    @param:Value("\${app.file.upload-directory}")
    private val uploadDirectory: String,

    @param:Value("\${app.public.uploaded-files}")
    private val uploadedFilesPath: String,

    @param:Value("\${app.limits.file-uploader.generated-name-length}")
    private val limitFileNameLength: Int,

    @param:Value("\${app.limits.file-uploader.size}")
    private val limitFileSizeKB: Long
) {

    init {
        val path = Paths.get(uploadDirectory)

        if (!Files.exists(path)) {
            Files.createDirectory(path)
        }
    }

    val limitFileSizeInBytes: Long
        get() = limitFileSizeKB * 1024

    val getBaseDirectory: Path
        get() = Paths.get(uploadDirectory).normalize().toAbsolutePath()

    fun uploadFile(file: MultipartFile): ResponseEntity<String> {
        if (file.isEmpty) {
            return ResponseEntity("File is empty?!", HttpStatus.BAD_REQUEST)
        }

        if (limitFileSizeKB != -1L && file.size > limitFileSizeInBytes) {
            return ResponseEntity(
                "File is too big! (size: ${(file.size / 1024)}KB / max: ${limitFileSizeKB}KB)",
                HttpStatus.BAD_REQUEST
            )
        }

        return try {
            val fileExtension = getFileExtension(file.originalFilename)
            val path = generateUniqueFileName(limitFileNameLength, fileExtension)
            val fileName = path.fileName.toString()

            file.inputStream.use {
                Files.copy(it, path)
            }

            ResponseEntity("$uploadedFilesPath$fileName", HttpStatus.OK)
        } catch (e: Exception) {
            ResponseEntity("Failed to upload file: ${e.message}", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Throws(FileNotFoundException::class)
    fun getFile(file: String): Path {
        val baseDirectory = getBaseDirectory.normalize().toAbsolutePath()
        val filePath = baseDirectory.resolve(file).normalize().toAbsolutePath()

        if (!filePath.startsWith(baseDirectory)) {
            throw IllegalArgumentException("Invalid file path")
        }

        if (!Files.exists(filePath)) {
            throw FileNotFoundException("File not found: $file")
        }

        return filePath
    }

    private fun generateFileName(length: Int, fileExtension: String): Path {
        val fileName = generateRandomString(length) + "." + fileExtension
        val filePath = Paths.get(uploadDirectory, fileName)

        return filePath
    }

    private fun generateUniqueFileName(length: Int, fileExtension: String): Path {
        var generated = generateFileName(length, fileExtension)

        while (Files.exists(generated)) {
            generated = generateFileName(length, fileExtension)
        }

        return generated
    }

}