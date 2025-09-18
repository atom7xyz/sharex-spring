package xyz.atom7.sharexspring.exception.impl

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN)
class ProfileNotActiveException(user: String): RuntimeException("Profile not active for user $user")