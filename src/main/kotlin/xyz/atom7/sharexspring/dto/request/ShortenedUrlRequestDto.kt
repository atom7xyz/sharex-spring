package xyz.atom7.sharexspring.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class ShortenedUrlRequestDto(
    @field:NotBlank(message = "URL must not be blank")
    @field:Pattern(
        regexp = "^(https?://.+)$",
        message = "Invalid URL format"
    )
    val url: String
)