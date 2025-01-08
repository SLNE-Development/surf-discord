package dev.slne.discord.persistence.service.punishment

import dev.slne.discord.persistence.external.PunishmentBan
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PunishmentBanRepository : CoroutineCrudRepository<PunishmentBan, Long> {

    suspend fun countByPunishmentId(punishmentId: String): Long

}