package xyz.atom7.sharexspring.dto

import com.fasterxml.jackson.annotation.JsonValue

data class ShortenUrlResponseDto(
    @JsonValue
    val url: String
)