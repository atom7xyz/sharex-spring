package xyz.atom7.sharexspring.utils

import java.io.File

fun getFileExtension(filePath: String?): String
{
    if (filePath == null) {
        return ""
    }

    return File(filePath).extension
}

fun generateRandomString(length: Int): String
{
    val pool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    return (1..length)
        .map { pool.random() }
        .joinToString("")
}
