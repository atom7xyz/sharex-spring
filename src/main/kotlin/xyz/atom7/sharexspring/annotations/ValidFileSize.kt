package xyz.atom7.sharexspring.annotations

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [FileSizeValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidFileSize(
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],

    val message: String = "File size exceeds maximum allowed size",
    val maxSizeInKB: Long = 50,
    val useConfig: Boolean = true
)