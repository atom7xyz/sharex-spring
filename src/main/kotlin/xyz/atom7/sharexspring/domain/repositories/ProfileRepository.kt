package xyz.atom7.sharexspring.domain.repositories

import org.springframework.data.jpa.repository.JpaRepository
import xyz.atom7.sharexspring.domain.entities.Profile
import xyz.atom7.sharexspring.domain.entities.UserRole
import java.util.*

interface ProfileRepository : JpaRepository<Profile, UUID> {
    fun existsUserByRole(role: UserRole): Boolean
    fun findProfileById(id: UUID): Optional<Profile>
    fun findProfileByActiveTrue(): List<Profile>
}