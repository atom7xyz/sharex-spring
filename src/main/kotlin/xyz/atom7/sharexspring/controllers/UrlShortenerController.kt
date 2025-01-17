package xyz.atom7.sharexspring.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import xyz.atom7.sharexspring.services.UrlShortenerService

@RestController
@RequestMapping("/share/api/shorten")
class UrlShortenerController(private val urlShortenerService: UrlShortenerService)
{
    @PostMapping
    fun shortenUrl(@RequestParam url: String): ResponseEntity<String>
    {
        return urlShortenerService.shortenUrl(url)
    }
}
