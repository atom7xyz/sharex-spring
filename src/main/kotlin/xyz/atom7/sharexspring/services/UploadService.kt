package xyz.atom7.sharexspring.services

import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import xyz.atom7.sharexspring.config.properties.AppProperties
import xyz.atom7.sharexspring.domain.entities.FileUpload
import xyz.atom7.sharexspring.domain.entities.Profile
import xyz.atom7.sharexspring.domain.repositories.FileRepository
import xyz.atom7.sharexspring.dto.request.FileUploadRequestDto
import xyz.atom7.sharexspring.exception.impl.NotEnoughDiskSpaceException
import xyz.atom7.sharexspring.exception.impl.ProfileNotActiveException
import xyz.atom7.sharexspring.exception.impl.ProfileNotFoundException
import xyz.atom7.sharexspring.services.cache.CacheSection
import xyz.atom7.sharexspring.services.cache.CacheService
import xyz.atom7.sharexspring.services.health.DiskSpaceMonitorService
import xyz.atom7.sharexspring.services.security.hash.DigestService
import xyz.atom7.sharexspring.services.security.hash.HashingAlgorithm
import xyz.atom7.sharexspring.services.security.jwt.JwtService
import xyz.atom7.sharexspring.utils.generateRandomString
import xyz.atom7.sharexspring.utils.getFileExtension
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Service
class UploadService(
    appProperties: AppProperties,
    private val fileRepository: FileRepository,
    private val digestService: DigestService,
    private val cacheService: CacheService,
    private val diskSpaceMonitorService: DiskSpaceMonitorService,
    private val jwtService: JwtService
) {
    val uploadDirectory: String = appProperties.file.uploadDirectory
    private val uploadedFilesPath: String = appProperties.publicProperties.uploadedFiles
    private val generatedNameLength: Int = appProperties.limitsProperties.fileUploader.generatedNameLength

    init {
        val path = Paths.get(uploadDirectory)

        if (!Files.exists(path)) {
            Files.createDirectory(path)
        }
    }

    private val getBaseDirectory: Path = Paths.get(uploadDirectory)
        .normalize()
        .toAbsolutePath()

    val baseDirectory: Path = getBaseDirectory
        .normalize()
        .toAbsolutePath()

    @Transactional
    fun uploadFile(requestDto: FileUploadRequestDto, user: String): ResponseEntity<String> {
        val profile = cacheService.get(
            CacheSection.PROFILE,
            user,
            Profile::class
        ) ?: throw ProfileNotFoundException(user)

        if (!profile.active) {
            throw ProfileNotActiveException(user)
        }

        val file = requestDto.file
        val fileHash = digestService.hash(file, HashingAlgorithm.MD5)
        val existsInCache = cacheService.get(
            CacheSection.FILE_UPLOAD_HASH,
            fileHash,
            String::class
        )

        if (existsInCache != null) {
            val cachedFile = cacheService.get(
                CacheSection.FILE_UPLOAD,
                existsInCache,
                FileUpload::class
            ) ?: throw FileNotFoundException(existsInCache)

            return ResponseEntity("$uploadedFilesPath${cachedFile.path}", HttpStatus.OK)
        }

        if (!diskSpaceMonitorService.hasEnoughDiskSpace(requestDto.file.size)) {
            throw NotEnoughDiskSpaceException()
        }

        val fileExtension = getFileExtension(file.originalFilename)
        val path = generateUniqueFilePath(generatedNameLength, fileExtension)
        val fileName = path.fileName.toString()

        file.inputStream.use {
            Files.copy(it, path)
        }

        val toSave = FileUpload(
            path = fileName,
            hash = fileHash,
            uploadedBy = profile
        )
        val savedFile = fileRepository.saveAndFlush(toSave)
        cacheService.put(CacheSection.FILE_UPLOAD_HASH, savedFile.hash, savedFile.id)
        cacheService.put(CacheSection.FILE_UPLOAD_PATH, savedFile.path, savedFile.id)
        cacheService.put(CacheSection.FILE_UPLOAD, savedFile.id, savedFile)

        return ResponseEntity("$uploadedFilesPath$fileName", HttpStatus.OK)
    }

    @Throws(
        FileNotFoundException::class,
        IllegalArgumentException::class
    )
    fun getFile(file: String): ResponseEntity<Any> {
        val cachedFileId = cacheService.get(
            CacheSection.FILE_UPLOAD_PATH,
            file,
            String::class
        ) ?: throw FileNotFoundException("File not found in cache: $file")

        val cachedFile = cacheService.get(
            CacheSection.FILE_UPLOAD,
            cachedFileId,
            FileUpload::class
        ) ?: throw FileNotFoundException("File not found in cache: $file")

        if (cachedFile.passwordHash != null) {
            // TODO ask for auth in the future
        }

        val filePath = baseDirectory.resolve(file)
            .normalize()
            .toAbsolutePath()

        if (!filePath.startsWith(baseDirectory)) {
            throw IllegalArgumentException("Invalid file path")
        }

        if (!Files.exists(filePath)) {
            throw FileNotFoundException("File not found in filesystem: $file")
        }

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

    private fun generateFilePath(length: Int, fileExtension: String): Path {
        val fileName = generateRandomString(length) + "." + fileExtension
        val filePath = Paths.get(uploadDirectory, fileName)

        return filePath
    }

    private fun generateUniqueFilePath(length: Int, fileExtension: String): Path {
        var generated: Path

        do {
            generated = generateFilePath(length, fileExtension)
        } while (Files.exists(generated))

        return generated
    }

}