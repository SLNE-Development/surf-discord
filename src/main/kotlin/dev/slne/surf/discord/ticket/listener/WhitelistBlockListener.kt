package dev.slne.surf.discord.ticket.listener

import dev.slne.surf.discord.logger
import dev.slne.surf.discord.ticket.database.whitelist.WhitelistService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component

@Component
class WhitelistBlockListener(
    private val discordScope: CoroutineScope,
    private val whitelistService: WhitelistService
) : ListenerAdapter() {
    override fun onGuildMemberRemove(event: GuildMemberRemoveEvent) {
        val userId = event.user.idLong

        discordScope.launch {
            whitelistService.blockWhitelist(userId).let {
                if (it) {
                    logger.info("Blocked whitelist for user ${event.user.asTag} (${userId}) due to leaving the guild.")
                }
            }
        }
    }

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val userId = event.member.idLong

        discordScope.launch {
            whitelistService.unblockWhitelist(userId).let {
                if (it) {
                    logger.info("Unblocked whitelist for user ${event.user.asTag} (${userId}) due to joining the guild.")
                }
            }
        }
    }
}