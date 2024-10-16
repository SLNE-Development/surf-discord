package dev.slne.discord.listener.whitelist

import dev.minn.jda.ktx.events.listener
import dev.slne.discord.DiscordBot
import dev.slne.discord.persistence.service.whitelist.WhitelistRepository
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.kyori.adventure.text.logger.slf4j.ComponentLogger

object WhitelistQuitListener {

    private val logger = ComponentLogger.logger(WhitelistQuitListener::class.java)

    init {
        DiscordBot.jda.listener<GuildMemberRemoveEvent> { event ->
            val user = event.user
            val whitelist = WhitelistRepository.getWhitelistByDiscordId(user.id) ?: return@listener

            whitelist.blocked = true
            whitelist.save()

            logger.info("User ${user.name} left the server and was blocked.")
        }
    }
}
