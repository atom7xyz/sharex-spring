package xyz.atom7.sharexspring.dto.response

data class FileAuthTokenResponseDto(
    val token: String,
    val expiresIn: Long = 1800
)