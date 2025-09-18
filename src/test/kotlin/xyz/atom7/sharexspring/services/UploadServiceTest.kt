package xyz.atom7.sharexspring.services

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.util.FileSystemUtils
import xyz.atom7.sharexspring.BaseIntegrationTest
import xyz.atom7.sharexspring.domain.entities.FileUpload
import xyz.atom7.sharexspring.domain.entities.Profile
import xyz.atom7.sharexspring.domain.entities.UserRole
import xyz.atom7.sharexspring.domain.repositories.FileRepository
import xyz.atom7.sharexspring.domain.repositories.UrlRepository
import xyz.atom7.sharexspring.dto.request.FileUploadRequestDto
import xyz.atom7.sharexspring.exception.impl.NotEnoughDiskSpaceException
import xyz.atom7.sharexspring.exception.impl.ProfileNotActiveException
import xyz.atom7.sharexspring.exception.impl.ProfileNotFoundException
import xyz.atom7.sharexspring.services.cache.CacheSection
import xyz.atom7.sharexspring.services.cache.CacheService
import xyz.atom7.sharexspring.services.health.DiskSpaceMonitorService
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class UploadServiceTest(
    private val uploadService: UploadService
): BaseIntegrationTest() {

    @MockitoBean
    private lateinit var urlRepository: UrlRepository

    @MockitoBean
    private lateinit var cacheService: CacheService

    @MockitoBean
    private lateinit var diskSpaceMonitorService: DiskSpaceMonitorService

    private val uploadDirectory = Path.of(uploadService.uploadDirectory)

    @BeforeEach
    fun init() {
        Files.createDirectories(uploadDirectory)
    }

    @AfterEach
    fun cleanup() {
        val uploadPath = Path.of(uploadService.uploadDirectory)
        if (Files.exists(uploadPath)) {
            FileSystemUtils.deleteRecursively(uploadPath)
        }
    }

    @Test
    fun `uploadFile should reject the request due to not found profile`() {
        val mockMultiPartFile = MockMultipartFile(
            "file", "test.txt", "text/plain", "test".toByteArray()
        )

        val requestDto = FileUploadRequestDto(
            file = mockMultiPartFile,
            password = null
        )

        val userId = "non-existent"

        val exception = assertThrows<ProfileNotFoundException> {
            uploadService.uploadFile(requestDto, userId)
        }
        assertEquals(ProfileNotFoundException(userId).message, exception.message)
    }

    @Test
    fun `uploadFile should reject the request due to not active profile`() {
        val mockMultiPartFile = MockMultipartFile(
            "file", "test.txt", "text/plain", "test".toByteArray()
        )

        val requestDto = FileUploadRequestDto(
            file = mockMultiPartFile,
            password = null
        )

        val userUUID = UUID.randomUUID()
        val userId = userUUID.toString()

        val profile = Profile(
            id = userUUID,
            role = UserRole.ADMIN,
            keyHash = "none",
            keySalt = "none",
            active = false
        )

        given { cacheService.get(
            CacheSection.PROFILE,
            userId,
            Profile::class)
        }.willReturn(profile)

        val exception = assertThrows<ProfileNotActiveException> {
            uploadService.uploadFile(requestDto, userId)
        }
        assertEquals(ProfileNotActiveException(userId).message, exception.message)
    }

    @Test
    fun `uploadFile should reject the request due to not found file in the filesystem`() {
        val mockMultiPartFile = MockMultipartFile(
            "file", "test.txt", "text/plain", "test".toByteArray()
        )

        val requestDto = FileUploadRequestDto(
            file = mockMultiPartFile,
            password = null
        )

        val userUUID = UUID.randomUUID()
        val userId = userUUID.toString()
        val profile = Profile(
            id = userUUID,
            role = UserRole.USER,
            keyHash = "none",
            keySalt = "none",
            active = true
        )

        val fileUploadUUID = UUID.randomUUID()
        val fileId = fileUploadUUID.toString()
        val fileUpload = FileUpload(
            id = fileUploadUUID,
            path = "somePath.txt",
            md5 = "CY9rzUYh03PK3k6DJie09g==",
            uploadDate = null,
            uploadedBy = profile,
            passwordHash = null,
            passwordSalt = null
        )

        given { cacheService.get(
            CacheSection.PROFILE,
            userId,
            Profile::class)
        }.willReturn(profile)

        given { cacheService.get(
            CacheSection.FILE_UPLOAD_MD5,
            fileUpload.md5,
            String::class)
        }.willReturn(fileId)

        given { cacheService.get(
            CacheSection.FILE_UPLOAD,
            fileId,
            FileUpload::class)
        }.willReturn(null)

        val exception = assertThrows<FileNotFoundException> {
            uploadService.uploadFile(requestDto, userId)
        }
        assertEquals(FileNotFoundException(fileId).message, exception.message)
    }

    @Test
    fun `uploadFile should fulfill the request with the in cache link of the file`() {
        val mockMultiPartFile = MockMultipartFile(
            "file", "test.txt", "text/plain", "test".toByteArray()
        )

        val requestDto = FileUploadRequestDto(
            file = mockMultiPartFile,
            password = null
        )

        val userUUID = UUID.randomUUID()
        val userId = userUUID.toString()
        val profile = Profile(
            id = userUUID,
            role = UserRole.USER,
            keyHash = "none",
            keySalt = "none",
            active = true
        )

        val fileUploadUUID = UUID.randomUUID()
        val fileId = fileUploadUUID.toString()
        val fileUpload = FileUpload(
            id = fileUploadUUID,
            path = "somePath.txt",
            md5 = "CY9rzUYh03PK3k6DJie09g==",
            uploadDate = null,
            uploadedBy = profile,
            passwordHash = null,
            passwordSalt = null
        )

        given { cacheService.get(
            CacheSection.PROFILE,
            userId,
            Profile::class)
        }.willReturn(profile)

        given { cacheService.get(
            CacheSection.FILE_UPLOAD_MD5,
            fileUpload.md5,
            String::class)
        }.willReturn(fileId)

        given { cacheService.get(
            CacheSection.FILE_UPLOAD,
            fileId,
            FileUpload::class)
        }.willReturn(fileUpload)

        val result = uploadService.uploadFile(requestDto, userId)
        assertEquals(result.statusCode, HttpStatus.OK)
    }

    @Test
    fun `uploadFile should reject the request due to not enough disk space`() {
        val mockMultiPartFile = MockMultipartFile(
            "file", "test.txt", "text/plain", "test".toByteArray()
        )

        val requestDto = FileUploadRequestDto(
            file = mockMultiPartFile,
            password = null
        )

        val userUUID = UUID.randomUUID()
        val userId = userUUID.toString()
        val profile = Profile(
            id = userUUID,
            role = UserRole.USER,
            keyHash = "none",
            keySalt = "none",
            active = true
        )

        val fileUploadUUID = UUID.randomUUID()
        val fileId = fileUploadUUID.toString()
        val fileUpload = FileUpload(
            id = fileUploadUUID,
            path = "somePath.txt",
            md5 = "CY9rzUYh03PK3k6DJie09g==",
            uploadDate = null,
            uploadedBy = profile,
            passwordHash = null,
            passwordSalt = null
        )

        given { cacheService.get(
            CacheSection.PROFILE,
            userId,
            Profile::class)
        }.willReturn(profile)

        given { cacheService.get(
            CacheSection.FILE_UPLOAD_MD5,
            fileUpload.md5,
            String::class)
        }.willReturn(null)

        val exception = assertThrows<NotEnoughDiskSpaceException> {
            uploadService.uploadFile(requestDto, userId)
        }
        assertEquals(NotEnoughDiskSpaceException().message, exception.message)
    }

    @Test
    fun `uploadFile should successfully upload valid file`() {
        val mockMultiPartFile = MockMultipartFile(
            "file", "test.txt", "text/plain", "test".toByteArray()
        )

        val requestDto = FileUploadRequestDto(
            file = mockMultiPartFile,
            password = null
        )

        val userUUID = UUID.randomUUID()
        val userId = userUUID.toString()
        val profile = Profile(
            id = userUUID,
            role = UserRole.USER,
            keyHash = "none",
            keySalt = "none",
            active = true
        )

        val fileUploadUUID = UUID.randomUUID()
        val fileId = fileUploadUUID.toString()
        val fileUpload = FileUpload(
            id = fileUploadUUID,
            path = "somePath.txt",
            md5 = "CY9rzUYh03PK3k6DJie09g==",
            uploadDate = null,
            uploadedBy = profile,
            passwordHash = null,
            passwordSalt = null
        )

        given { cacheService.get(
            CacheSection.PROFILE,
            userId,
            Profile::class)
        }.willReturn(profile)

        given { cacheService.get(
            CacheSection.FILE_UPLOAD_MD5,
            fileUpload.md5,
            String::class)
        }.willReturn(null)

        given {
            diskSpaceMonitorService.hasEnoughDiskSpace(requestDto.file.size)
        }.willReturn(false)

        val exception = assertThrows<NotEnoughDiskSpaceException> {
            uploadService.uploadFile(requestDto, userId)
        }
        assertEquals(NotEnoughDiskSpaceException().message, exception.message)
    }

    @MockitoBean
    private lateinit var fileRepository: FileRepository

    @Test
    fun `uploadFile should fulfill the request by saving the newly uploaded file`() {
        val mockMultiPartFile = MockMultipartFile(
            "file", "test.txt", "text/plain", "test".toByteArray()
        )

        val requestDto = FileUploadRequestDto(
            file = mockMultiPartFile,
            password = null
        )

        val userUUID = UUID.randomUUID()
        val userId = userUUID.toString()
        val profile = Profile(
            id = userUUID,
            role = UserRole.USER,
            keyHash = "none",
            keySalt = "none",
            active = true
        )

        val fileUploadUUID = UUID.randomUUID()
        val fileId = fileUploadUUID.toString()
        val fileUpload = FileUpload(
            id = fileUploadUUID,
            path = "somePath.txt",
            md5 = "CY9rzUYh03PK3k6DJie09g==",
            uploadDate = null,
            uploadedBy = profile,
            passwordHash = null,
            passwordSalt = null
        )

        given { cacheService.get(
            CacheSection.PROFILE,
            userId,
            Profile::class)
        }.willReturn(profile)

        given { cacheService.get(
            CacheSection.FILE_UPLOAD_MD5,
            fileUpload.md5,
            String::class)
        }.willReturn(null)

        given {
            diskSpaceMonitorService.hasEnoughDiskSpace(requestDto.file.size)
        }.willReturn(true)

        given {
            fileRepository.saveAndFlush(any<FileUpload>())
        }.willReturn(fileUpload)

        val result = uploadService.uploadFile(requestDto, userId)
        assertEquals(result.statusCode, HttpStatus.OK)
    }

    @Test
    fun `getFile should reject the request due to file not found in cache (FILE_UPLOAD_PATH cache)`() {
        val userInput = "example.txt"

        val userUUID = UUID.randomUUID()
        val userId = userUUID.toString()
        val profile = Profile(
            id = userUUID,
            role = UserRole.USER,
            keyHash = "none",
            keySalt = "none",
            active = true
        )

        val fileUploadUUID = UUID.randomUUID()
        val fileId = fileUploadUUID.toString()
        val fileUpload = FileUpload(
            id = fileUploadUUID,
            path = "somePath.txt",
            md5 = "CY9rzUYh03PK3k6DJie09g==",
            uploadDate = null,
            uploadedBy = profile,
            passwordHash = null,
            passwordSalt = null
        )

        given { cacheService.get(
            CacheSection.FILE_UPLOAD_PATH,
            userInput,
            String::class)
        }.willReturn(null)

        val exception = assertThrows<FileNotFoundException> {
            uploadService.getFile(userInput)
        }
        assertEquals(
            FileNotFoundException("File not found in cache: $userInput").message,
            exception.message
        )
    }

    @Test
    fun `getFile should reject the request due to file not found in cache (FILE_UPLOAD cache)`() {
        val userInput = "example.txt"

        val userUUID = UUID.randomUUID()
        val userId = userUUID.toString()
        val profile = Profile(
            id = userUUID,
            role = UserRole.USER,
            keyHash = "none",
            keySalt = "none",
            active = true
        )

        val fileUploadUUID = UUID.randomUUID()
        val fileId = fileUploadUUID.toString()
        val fileUpload = FileUpload(
            id = fileUploadUUID,
            path = "somePath.txt",
            md5 = "CY9rzUYh03PK3k6DJie09g==",
            uploadDate = null,
            uploadedBy = profile,
            passwordHash = null,
            passwordSalt = null
        )

        given { cacheService.get(
            CacheSection.FILE_UPLOAD_PATH,
            userInput,
            String::class)
        }.willReturn(fileUpload.path)

        given { cacheService.get(
                CacheSection.FILE_UPLOAD,
                fileUpload.path,
                FileUpload::class)
        }.willReturn(null)

        val exception = assertThrows<FileNotFoundException> {
            uploadService.getFile(userInput)
        }
        assertEquals(
            FileNotFoundException("File not found in cache: $userInput").message,
            exception.message
        )
    }

    @Test
    fun `getFile should reject the request due to invalid file path`() {
        val userInput = "../../../../example.txt"

        val userUUID = UUID.randomUUID()
        val userId = userUUID.toString()
        val profile = Profile(
            id = userUUID,
            role = UserRole.USER,
            keyHash = "none",
            keySalt = "none",
            active = true
        )

        val fileUploadUUID = UUID.randomUUID()
        val fileId = fileUploadUUID.toString()
        val fileUpload = FileUpload(
            id = fileUploadUUID,
            path = "somePath.txt",
            md5 = "CY9rzUYh03PK3k6DJie09g==",
            uploadDate = null,
            uploadedBy = profile,
            passwordHash = null,
            passwordSalt = null
        )

        given { cacheService.get(
            CacheSection.FILE_UPLOAD_PATH,
            userInput,
            String::class)
        }.willReturn(fileUpload.path)

        given { cacheService.get(
            CacheSection.FILE_UPLOAD,
            fileUpload.path,
            FileUpload::class)
        }.willReturn(fileUpload)

        val exception = assertThrows<IllegalArgumentException> {
            uploadService.getFile(userInput)
        }
        assertEquals(
            IllegalArgumentException("Invalid file path").message,
            exception.message
        )
    }

    @Test
    fun `getFile should reject the request due to file not found in filesystem`() {
        val userInput = "example.txt"

        val userUUID = UUID.randomUUID()
        val userId = userUUID.toString()
        val profile = Profile(
            id = userUUID,
            role = UserRole.USER,
            keyHash = "none",
            keySalt = "none",
            active = true
        )

        val fileUploadUUID = UUID.randomUUID()
        val fileId = fileUploadUUID.toString()
        val fileUpload = FileUpload(
            id = fileUploadUUID,
            path = "somePath.txt",
            md5 = "CY9rzUYh03PK3k6DJie09g==",
            uploadDate = null,
            uploadedBy = profile,
            passwordHash = null,
            passwordSalt = null
        )

        given { cacheService.get(
            CacheSection.FILE_UPLOAD_PATH,
            userInput,
            String::class)
        }.willReturn(fileUpload.path)

        given { cacheService.get(
            CacheSection.FILE_UPLOAD,
            fileUpload.path,
            FileUpload::class)
        }.willReturn(fileUpload)

        val exception = assertThrows<FileNotFoundException> {
            uploadService.getFile(userInput)
        }
        assertEquals(
            FileNotFoundException("File not found in filesystem: $userInput").message,
            exception.message
        )
    }

    @Test
    fun `getFile should return a file`() {
        val userInput = "example.txt"

        val userUUID = UUID.randomUUID()
        val userId = userUUID.toString()
        val profile = Profile(
            id = userUUID,
            role = UserRole.USER,
            keyHash = "none",
            keySalt = "none",
            active = true
        )

        val fileUploadUUID = UUID.randomUUID()
        val fileId = fileUploadUUID.toString()
        val fileUpload = FileUpload(
            id = fileUploadUUID,
            path = "somePath.txt",
            md5 = "CY9rzUYh03PK3k6DJie09g==",
            uploadDate = null,
            uploadedBy = profile,
            passwordHash = null,
            passwordSalt = null
        )

        given { cacheService.get(
                CacheSection.FILE_UPLOAD_PATH,
                userInput,
                String::class)
        }.willReturn(fileUpload.path)

        given { cacheService.get(
                CacheSection.FILE_UPLOAD,
                fileUpload.path,
                FileUpload::class)
        }.willReturn(fileUpload)

        Files.createFile(Path.of("$uploadDirectory/$userInput"))

        val result = uploadService.getFile(userInput)
        assertEquals(result.statusCode, HttpStatus.OK)
    }

} 