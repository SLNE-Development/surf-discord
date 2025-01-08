package dev.slne.discord.persistence.service.whitelist

import dev.slne.discord.persistence.external.Whitelist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.dv8tion.jda.api.entities.User
import org.springframework.stereotype.Service
import java.util.*

@Service
class WhitelistService(private val repository: WhitelistRepository) {

    private fun checkAndThrow(
        uuid: UUID? = null,
        discordId: String? = null,
        twitchLink: String? = null
    ) {
        if (uuid == null && discordId == null && twitchLink == null) {
            throw IllegalArgumentException("At least one parameter must be set")
        }
    }

    suspend fun isWhitelisted(user: User) = isWhitelisted(discordId = user.id)

    suspend fun isWhitelisted(
        uuid: UUID? = null,
        discordId: String? = null,
        twitchLink: String? = null
    ) =
        withContext(Dispatchers.IO) {
            checkAndThrow(uuid, discordId, twitchLink)
            repository.findWhitelists(uuid, discordId, twitchLink).isNotEmpty()
        }

    suspend fun findByDiscordId(discordId: String) = withContext(Dispatchers.IO) {
        repository.findByDiscordId(discordId)
    }

    suspend fun deleteWhitelists(
        uuid: UUID? = null,
        discordId: String? = null,
        twitchLink: String? = null
    ) =
        withContext(Dispatchers.IO) {
            checkAndThrow(uuid, discordId, twitchLink)
            repository.deleteWhitelists(uuid, discordId, twitchLink)
        }

    suspend fun findWhitelists(
        uuid: UUID? = null,
        discordId: String? = null,
        twitchLink: String? = null
    ) =
        withContext(Dispatchers.IO) {
            checkAndThrow(uuid, discordId, twitchLink)
            repository.findWhitelists(uuid, discordId, twitchLink)
        }

    suspend fun findWhitelistByDiscordId(discordId: String) = withContext(Dispatchers.IO) {
        repository.findWhitelistByDiscordId(discordId)
    }

    suspend fun saveWhitelist(whitelist: Whitelist) = withContext(Dispatchers.IO) {
        repository.save(whitelist)
    }

}