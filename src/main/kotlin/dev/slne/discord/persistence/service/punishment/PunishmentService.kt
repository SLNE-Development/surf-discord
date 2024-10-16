package dev.slne.discord.persistence.service.punishment

import dev.slne.discord.persistence.external.dto.PunishmentBanDTO

object PunishmentService {

    suspend fun getBanByPunishmentId(punishmentId: String): PunishmentBanDTO? = TODO("Implement")

    suspend fun isValidPunishmentId(punishmentId: String): Boolean = TODO("Implement")
}
