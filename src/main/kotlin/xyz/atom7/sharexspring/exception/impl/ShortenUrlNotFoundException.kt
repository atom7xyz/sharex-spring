package xyz.atom7.sharexspring.exception.impl

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class ShortenUrlNotFoundException : RuntimeException("Redirection for this link brings nowhere!")