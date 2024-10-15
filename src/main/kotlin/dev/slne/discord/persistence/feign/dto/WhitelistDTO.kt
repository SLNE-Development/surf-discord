package dev.slne.discord.persistence.feign.dto

import dev.slne.discord.DiscordBot
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.requests.RestAction
import java.time.ZonedDateTime
import java.util.*

class WhitelistDTO(
    val id: Long = 0,
    val uuid: UUID? = null,
    val minecraftName: String? = null,
    val twitchLink: String? = null,
    val discordId: String? = null,
    val addedById: String? = null,
    val addedByName: String? = null,
    val addedByAvatarUrl: String? = null,
    var blocked: Boolean = false,
    val createdAt: ZonedDateTime? = null,
) {
    val addedBy: RestAction<User>?
        get() = addedById?.let { DiscordBot.jda.retrieveUserById(it) }

    val discordUser: RestAction<User>?
        get() = discordId?.let { DiscordBot.jda.retrieveUserById(it) }

    companion object {
        @JvmStatic
        fun createFrom(
            uuid: UUID,
            minecraftName: String?,
            twitchLink: String?,
            user: User?,
            executor: User?
        ): WhitelistDTO = TODO("Implement")
    }
}
