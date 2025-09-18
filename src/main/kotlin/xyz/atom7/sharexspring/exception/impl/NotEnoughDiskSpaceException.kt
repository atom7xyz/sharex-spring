package xyz.atom7.sharexspring.exception.impl

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.INSUFFICIENT_STORAGE)
class NotEnoughDiskSpaceException: RuntimeException("Not enough disk space left to upload!")