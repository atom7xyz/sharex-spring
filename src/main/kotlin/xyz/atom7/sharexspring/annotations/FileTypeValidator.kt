package xyz.atom7.sharexspring.annotations

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.web.multipart.MultipartFile
import xyz.atom7.sharexspring.config.properties.app.LimitsProperties

class FileTypeValidator(
    private val limitsProperties: LimitsProperties
) : ConstraintValidator<ValidFileType, MultipartFile> {

    private lateinit var allowedTypes: Array<String>
    private var allowEverything: Boolean = false

    override fun initialize(constraintAnnotation: ValidFileType) {
        if (constraintAnnotation.useConfig) {
            this.allowedTypes = limitsProperties.fileUploader.allowedTypes
            checkForAllowance()
            return
        }

        allowedTypes = constraintAnnotation.allowedTypes
        checkForAllowance()
    }

    override fun isValid(file: MultipartFile?, context: ConstraintValidatorContext?): Boolean {
        if (file == null || file.isEmpty) {
            return false
        }

        val contentType = file.contentType ?: return false
        return allowEverything || allowedTypes.contains(contentType)
    }

    private fun checkForAllowance() {
        this.allowEverything = allowedTypes.contains("*")
    }

}