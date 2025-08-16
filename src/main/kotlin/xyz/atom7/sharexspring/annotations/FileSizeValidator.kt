package xyz.atom7.sharexspring.annotations

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.web.multipart.MultipartFile
import xyz.atom7.sharexspring.config.properties.app.LimitsProperties

class FileSizeValidator(
    private val limitsProperties: LimitsProperties
) : ConstraintValidator<ValidFileSize, MultipartFile> {

    private var maxSizeInBytes: Long = 0

    override fun initialize(constraintAnnotation: ValidFileSize) {
        if (constraintAnnotation.useConfig) {
            this.maxSizeInBytes = limitsProperties.fileUploader.size * 1024
            return
        }

        this.maxSizeInBytes = constraintAnnotation.maxSizeInKB * 1024
    }

    override fun isValid(file: MultipartFile?, context: ConstraintValidatorContext?): Boolean {
        if (file == null || file.isEmpty) {
            return false
        }

        return file.size <= maxSizeInBytes
    }

}