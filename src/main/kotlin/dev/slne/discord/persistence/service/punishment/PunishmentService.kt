package dev.slne.discord.persistence.service.punishment

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PunishmentService(private val punishmentRepository: PunishmentBanRepository) {
    suspend fun isValidPunishmentId(punishmentId: String) = withContext(Dispatchers.IO) {
        punishmentRepository.countByPunishmentId(punishmentId) > 0
    }
}
