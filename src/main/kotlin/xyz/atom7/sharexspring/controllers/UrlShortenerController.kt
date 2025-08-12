package xyz.atom7.sharexspring.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import xyz.atom7.sharexspring.services.UrlShortenerService

@RestController
@RequestMapping("/share/s")
class UrlShortenerController(
    private val urlShortenerService: UrlShortenerService
) {

    @PostMapping
    fun shortenUrl(@RequestParam url: String): ResponseEntity<String> {
        return urlShortenerService.shortenUrl(url)
    }

    @GetMapping("/{url}")
    fun gotoTargetUrl(@PathVariable url: String): Any {
        return try {
            val target = urlShortenerService.getUrl(url)!!.originUrl
            RedirectView(target)
        } catch (e: Exception) {
            ResponseEntity(e.message, HttpStatus.NOT_FOUND)
        }
    }

}
