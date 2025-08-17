package xyz.atom7.sharexspring.annotations.aspects

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Log(
    val action: String = "",
    val includeArgs: Boolean = false
)