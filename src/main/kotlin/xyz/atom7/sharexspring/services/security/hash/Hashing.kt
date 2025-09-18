package xyz.atom7.sharexspring.services.security.hash

import java.io.InputStream
import java.security.SecureRandom
import kotlin.io.encoding.Base64

interface Hashing {

    /**
     * Generates a salt of the set length and returns the Base64 encoding of it.
     *
     * @param length The length of the salt
     * @return Base64 encoded salt
     */
    fun generateSalt(length: Int): String {
        val random = SecureRandom()
        val salt = ByteArray(length)
        random.nextBytes(salt)
        return Base64.encode(salt)
    }

    /**
     * Generates the hash of the given InputStream and applies a consumer (used for conversions).
     *
     * @param inputStream The input stream to hash
     * @param algorithm The algorithm to hash the InputStream with
     * @param consumer The consumer used to convert the hash
     * @return The converted hash.
     */
    fun <T> hash(inputStream: InputStream, algorithm: HashingAlgorithm, consumer: (ByteArray) -> T): T {
        return inputStream.use { inputStream ->
            val hashBytes = algorithm.block(inputStream)
            consumer(hashBytes)
        }
    }

    fun hashSaltApiKey(apiKey: String, salt: String): String

}