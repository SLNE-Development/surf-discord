package dev.slne.discord.persistence.service.punishment

import dev.slne.discord.persistence.external.PunishmentBan
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PunishmentBanRepository : JpaRepository<PunishmentBan, Long> {

    fun countByPunishmentId(punishmentId: String): Long

}