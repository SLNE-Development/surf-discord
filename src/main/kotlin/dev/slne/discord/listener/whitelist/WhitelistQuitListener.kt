package dev.slne.discord.listener.whitelist

import dev.minn.jda.ktx.events.listener
import dev.slne.discord.DiscordBot
import dev.slne.discord.spring.service.whitelist.WhitelistService
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.kyori.adventure.text.logger.slf4j.ComponentLogger

object WhitelistQuitListener {

    private val logger = ComponentLogger.logger(WhitelistQuitListener::class.java)

    init {
        DiscordBot.jda.listener<GuildMemberRemoveEvent> { event ->
            val user = event.user
            val whitelist = WhitelistService.getWhitelistByDiscordId(user.id) ?: return@listener

            whitelist.blocked = true
            val updatedWhitelist = WhitelistService.updateWhitelist(whitelist)

            if (updatedWhitelist == null) {
                logger.error("Failed to update whitelist for user ${user.name}.")
            } else {
                logger.info("User ${user.name} left the server and was blocked.")
            }
        }
    }
}
