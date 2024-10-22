package dev.slne.discord.persistence.service.punishment

import dev.slne.discord.persistence.sessionFactory
import dev.slne.discord.persistence.withSession

object PunishmentService {
    suspend fun isValidPunishmentId(punishmentId: String) = sessionFactory.withSession { session ->
        val query = session.createNativeQuery(
            "SELECT EXISTS(SELECT 1 FROM punish_bans WHERE punishment_id = :punishmentId)",
            arrayOf<Any>()::class.java
        )
        query.setParameter("punishmentId", punishmentId)
        query.singleResult as Int == 1
    }
}
