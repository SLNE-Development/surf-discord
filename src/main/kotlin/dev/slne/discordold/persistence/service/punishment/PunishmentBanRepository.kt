package dev.slne.discordold.persistence.service.punishment

import dev.slne.discordold.persistence.external.PunishmentBan
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PunishmentBanRepository : JpaRepository<PunishmentBan, Long> {

    @Query("select (count(p) > 0) from PunishmentBan p where p.punishmentId = :punishmentId")
    fun existsByPunishmentId(@Param("punishmentId") punishmentId: String): Boolean

    fun getPunishmentBanByPunishmentId(punishmentId: String): PunishmentBan?
}