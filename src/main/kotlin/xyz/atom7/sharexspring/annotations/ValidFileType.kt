package xyz.atom7.sharexspring.annotations

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [FileTypeValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidFileType(
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],

    val message: String = "Invalid file type",
    val allowedTypes: Array<String> = [],
    val useConfig: Boolean = true
)