package dev.slne.discord.spring.service.punishment

import dev.slne.discord.spring.feign.dto.PunishmentBanDTO

object PunishmentService {

    suspend fun getBanByPunishmentId(punishmentId: String): PunishmentBanDTO? = TODO("Implement")

    suspend fun isValidPunishmentId(punishmentId: String): Boolean = TODO("Implement")
}
