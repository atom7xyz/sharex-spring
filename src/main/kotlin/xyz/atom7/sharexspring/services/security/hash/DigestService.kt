package xyz.atom7.sharexspring.services.security.hash

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64

@Service
class DigestService : Hashing {

    fun hash(file: File, hashingAlgorithm: HashingAlgorithm): String {
        return hash(file.inputStream(), hashingAlgorithm) { Base64.encode(it) }
    }

    fun hash(file: MultipartFile, hashingAlgorithm: HashingAlgorithm): String {
        return hash(file.inputStream, hashingAlgorithm) { Base64.encode(it) }
    }

    override fun hashSaltApiKey(
        apiKey: String,
        salt: String
    ): String {
        val mac = Mac.getInstance("HmacSHA256")
        val keySpec = SecretKeySpec(salt.toByteArray(), "HmacSHA256")
        mac.init(keySpec)
        val hash = mac.doFinal(apiKey.toByteArray())
        return Base64.encode(hash)
    }

    fun equal(digestA : ByteArray, digestB: ByteArray): Boolean {
        return MessageDigest.isEqual(digestA, digestB)
    }

}