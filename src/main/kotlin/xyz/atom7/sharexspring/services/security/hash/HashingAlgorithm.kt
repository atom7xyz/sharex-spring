package xyz.atom7.sharexspring.services.security.hash

import org.apache.commons.codec.digest.DigestUtils
import java.io.InputStream

enum class HashingAlgorithm(val value: String, val block: (InputStream) -> ByteArray) {
    MD5("MD5", { DigestUtils.md5(it) }),
    SHA1("SHA-1", { DigestUtils.sha1(it) }),
    SHA256("SHA-256", { DigestUtils.sha256(it) })
}
