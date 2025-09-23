package xyz.atom7.sharexspring.services.cache.populators

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import xyz.atom7.sharexspring.domain.repositories.FileRepository
import xyz.atom7.sharexspring.domain.repositories.ProfileRepository
import xyz.atom7.sharexspring.domain.repositories.UrlRepository
import xyz.atom7.sharexspring.events.DatabaseInitializedEvent
import xyz.atom7.sharexspring.services.cache.CacheSection

@Service
class CachePopulator(
    private val profileRepository: ProfileRepository,
    private val urlRepository: UrlRepository,
    private val fileRepository: FileRepository,
    @param:Qualifier("caffeineCacheManager") private val caffeineCacheManager: CacheManager,
    @param:Qualifier("redisCacheManager") private val redisCacheManager: CacheManager
) : Populator {

    @EventListener(DatabaseInitializedEvent::class)
    @Transactional(readOnly = true)
    override fun populate() {
        populate<Cache>(redisCacheManager, CacheSection.SHORTENED_URL) { cache ->
            urlRepository.findAllByOrderByHitsAsc().forEach { url ->
                cache.put("target->${url.targetUrl}", url)
                cache.put("origin->${url.originUrl}", url)
            }
        }

        populate<Cache>(redisCacheManager, CacheSection.PROFILE) { cache ->
            profileRepository.findAll().forEach { profile ->
                cache.put(profile.id!!, profile)
            }
        }

        populate<Cache>(redisCacheManager, CacheSection.FILE_UPLOAD) { cache ->
            fileRepository.findAll().forEach { file ->
                cache.put(file.id!!, file)
            }
        }

        populate<Cache>(redisCacheManager, CacheSection.FILE_UPLOAD_HASH) { cache ->
            fileRepository.findAll().forEach { file ->
                cache.put(file.hash, file.id)
            }
        }

        populate<Cache>(redisCacheManager, CacheSection.FILE_UPLOAD_PATH) { cache ->
            fileRepository.findAll().forEach { file ->
                cache.put(file.path, file.id)
            }
        }

        populate<Cache>(caffeineCacheManager, CacheSection.API_KEY) { cache ->
            profileRepository.findProfileByActiveTrue()
                .forEach { profile ->
                    cache.put(profile.id!!.toString(), Pair(profile.keyHash, profile.keySalt))
                }
        }
    }

    override fun cleanup() {
        cleanup(redisCacheManager, CacheSection.SHORTENED_URL)
        cleanup(redisCacheManager, CacheSection.PROFILE)
        cleanup(redisCacheManager, CacheSection.FILE_UPLOAD)
        cleanup(redisCacheManager, CacheSection.FILE_UPLOAD_HASH)
        cleanup(redisCacheManager, CacheSection.FILE_UPLOAD_PATH)
        cleanup(caffeineCacheManager, CacheSection.API_KEY)
    }

}
