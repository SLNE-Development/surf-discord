package dev.slne.discord.persistence.service.punishment

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class PunishmentService(private val punishmentRepository: PunishmentBanRepository) {
    suspend fun isValidPunishmentId(punishmentId: String) =
        withContext(Dispatchers.IO) { punishmentRepository.existsByPunishmentId(punishmentId) }
}
