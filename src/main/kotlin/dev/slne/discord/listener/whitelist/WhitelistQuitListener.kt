package dev.slne.discord.listener.whitelist

import dev.slne.discord.spring.feign.dto.WhitelistDTO
import dev.slne.discord.spring.service.whitelist.WhitelistService
import jakarta.annotation.Nonnull
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.kyori.adventure.text.logger.slf4j.ComponentLogger

/**
 * The type WhitelistDTO quit listener.
 */
@DiscordListener
class WhitelistQuitListener(private val whitelistService: WhitelistService) : ListenerAdapter() {
    override fun onGuildMemberRemove(@Nonnull event: GuildMemberRemoveEvent) {
        val user: User = event.getUser()
        val whitelist: WhitelistDTO? = whitelistService.getWhitelistByDiscordId(user.getId()).join()

        if (whitelist == null) {
            return
        }

        whitelist.setBlocked(true)
        val updatedWhitelist: WhitelistDTO? = whitelistService.updateWhitelist(whitelist).join()

        if (updatedWhitelist == null) {
            LOGGER.error("Failed to update whitelist for user {}.", user.getName())
        } else {
            LOGGER.info("User {} left the server and was blocked.", user.getName())
        }
    }

    companion object {
        private val LOGGER: ComponentLogger = ComponentLogger.logger("WhitelistQuitListener")
    }
}
