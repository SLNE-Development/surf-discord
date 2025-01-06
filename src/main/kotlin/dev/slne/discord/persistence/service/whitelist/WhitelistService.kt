package dev.slne.discord.persistence.service.whitelist

import dev.slne.discord.persistence.external.Whitelist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.util.*

@Service
class WhitelistService(private val repository: WhitelistRepository) {

    suspend fun findByDiscordId(discordId: String) = withContext(Dispatchers.IO) {
        repository.findByDiscordId(discordId)
    }

    suspend fun deleteWhitelists(uuid: UUID?, discordId: String?, twitchLink: String?) =
        withContext(Dispatchers.IO) {
            repository.deleteWhitelists(uuid, discordId, twitchLink)
        }

    suspend fun findWhitelists(uuid: UUID?, discordId: String?, twitchLink: String?) =
        withContext(Dispatchers.IO) {
            repository.findWhitelists(uuid, discordId, twitchLink)
        }

    suspend fun findWhitelistByDiscordId(discordId: String) = withContext(Dispatchers.IO) {
        repository.findWhitelistByDiscordId(discordId)
    }

    suspend fun saveWhitelist(whitelist: Whitelist) = withContext(Dispatchers.IO) {
        repository.save(whitelist)
    }

}