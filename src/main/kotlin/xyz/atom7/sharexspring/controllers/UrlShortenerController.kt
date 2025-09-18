package xyz.atom7.sharexspring.controllers

import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import xyz.atom7.sharexspring.annotations.aspects.Log
import xyz.atom7.sharexspring.dto.request.ShortenedUrlRequestDto
import xyz.atom7.sharexspring.dto.response.ShortenedUrlResponseDto
import xyz.atom7.sharexspring.exception.impl.ShortenUrlNotFoundException
import xyz.atom7.sharexspring.services.UrlShortenerService

@RestController
@RequestMapping("/share/s")
@Validated
class UrlShortenerController(
    private val urlShortenerService: UrlShortenerService
) {

    @Log("URL Shortened", includeArgs = true)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun shortenUrl(
        @Valid @RequestBody url: ShortenedUrlRequestDto
    ): ShortenedUrlResponseDto {
        return urlShortenerService.shortenUrl(url)
    }

    @Log(action = "Redirected to target URL", includeArgs = true)
    @GetMapping("/{url}")
    fun gotoTargetUrl(
        @PathVariable
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Invalid URL format")
        url: String
    ): RedirectView {
        val target = urlShortenerService.getUrl(url)
            .orElseThrow { ShortenUrlNotFoundException() }

        return RedirectView(target.url)
    }

}
