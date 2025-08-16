package xyz.atom7.sharexspring.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class ShortenUrlRequestDto(
    @field:NotBlank(message = "URL must not be blank")
    @field:Pattern(
        regexp = "^(https?://.+)$",
        message = "Invalid URL format"
    )
    val url: String
)
