package dev.slne.discord.listener.whitelist

import dev.minn.jda.ktx.events.listener
import dev.slne.discord.persistence.service.whitelist.WhitelistService
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.kyori.adventure.text.logger.slf4j.ComponentLogger

class WhitelistQuitListener(jda: JDA, whitelistService: WhitelistService) {

    private val logger = ComponentLogger.logger()

    init {
        jda.listener<GuildMemberRemoveEvent> { event ->
            val user = event.user
            val whitelist = whitelistService.findWhitelistByDiscordId(user.id) ?: return@listener

            whitelist.blocked = true
            whitelistService.saveWhitelist(whitelist)

            logger.info("User ${user.name} left the server and was blocked.")
        }
    }
}
