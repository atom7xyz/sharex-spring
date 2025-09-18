package xyz.atom7.sharexspring.dto.response

import com.fasterxml.jackson.annotation.JsonValue

data class ShortenedUrlResponseDto(
    @JsonValue
    val url: String
)