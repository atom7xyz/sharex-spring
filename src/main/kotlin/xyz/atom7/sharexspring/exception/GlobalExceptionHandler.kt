package xyz.atom7.sharexspring.exception

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import xyz.atom7.sharexspring.exception.impl.ProfileNotActiveException
import xyz.atom7.sharexspring.exception.impl.ProfileNotFoundException
import xyz.atom7.sharexspring.exception.impl.ShortenUrlNotFoundException
import java.io.FileNotFoundException

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException::class, MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(ex: Exception): ResponseEntity<Map<String, Any>> {
        return ResponseEntity(
            mapOf(
                "message" to "Invalid input provided",
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(
        ShortenUrlNotFoundException::class,
        ProfileNotFoundException::class,
        ProfileNotActiveException::class,
        FileNotFoundException::class
    )
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleShortenUrlNotFoundException(ex: Exception): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    fun handleMethodNotSupportedException(
        ex: HttpRequestMethodNotSupportedException
    ): ResponseEntity<Map<String, String>> {
        return ResponseEntity(
            mapOf(
                "message" to "Method not allowed: ${ex.method}",
            ),
            HttpStatus.METHOD_NOT_ALLOWED
        )
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGenericException(ex: Exception): ResponseEntity<Map<String, String>> {
        return ResponseEntity(
            mapOf(
                "message" to "An unexpected error occurred: ${ex.message}"
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }

}