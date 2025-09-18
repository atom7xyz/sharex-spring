package xyz.atom7.sharexspring.services.security

import org.springframework.stereotype.Service
import xyz.atom7.sharexspring.services.cache.CacheSection
import xyz.atom7.sharexspring.services.cache.CacheService
import xyz.atom7.sharexspring.services.security.hash.DigestService
import kotlin.reflect.KClass

@Service
class ApiKeyValidationService(
    private val cacheService: CacheService,
    private val digestService: DigestService
) {

    @Suppress("UNCHECKED_CAST")
    fun checkUserApiKeyValidity(user: String?, apiKey: String?): Boolean {
        if (user.isNullOrEmpty() || apiKey.isNullOrEmpty()) {
            return false
        }

        val cachedData = cacheService.get(
            CacheSection.API_KEY,
            user,
            Pair::class as KClass<Pair<String, String>>
        ) ?: return false

        val cachedHashedApiKey = cachedData.first
        val cachedSalt = cachedData.second

        val hashedApiKey = digestService.hashSaltApiKey(apiKey, cachedSalt)

        return digestService.equal(hashedApiKey.toByteArray(), cachedHashedApiKey.toByteArray())
    }

}