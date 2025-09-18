package xyz.atom7.sharexspring.exception.impl

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class CacheNotFoundException(cacheName: String): RuntimeException("Cache not found: $cacheName")