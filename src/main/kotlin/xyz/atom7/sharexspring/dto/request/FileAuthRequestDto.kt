package xyz.atom7.sharexspring.dto.request

import jakarta.validation.constraints.NotBlank

data class FileAuthRequestDto(
    @field:NotBlank(message = "Password is required")
    val password: String
)