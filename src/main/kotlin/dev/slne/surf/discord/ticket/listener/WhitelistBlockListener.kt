package dev.slne.surf.discord.ticket.listener

import dev.slne.surf.discord.config.botConfig
import dev.slne.surf.discord.logger
import dev.slne.surf.discord.ticket.database.whitelist.SocialService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component

@Component
class WhitelistBlockListener(
    private val discordScope: CoroutineScope,
    private val socialService: SocialService
) : ListenerAdapter() {
    override fun onGuildMemberRemove(event: GuildMemberRemoveEvent) {
        val userId = event.user.idLong

        discordScope.launch {
            socialService.blockWhitelist(userId).let {
                if (it) {
                    logger.info("Blocked whitelist for user ${event.user.asTag} (${userId}) due to leaving the guild.")
                }
            }
        }
    }

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val userId = event.member.idLong

        discordScope.launch {
            socialService.unblockWhitelist(userId).let {
                if (it) {
                    logger.info("Unblocked whitelist for user ${event.user.asTag} (${userId}) due to joining the guild.")

                    event.guild.addRoleToMember(
                        event.member,
                        event.jda.getRoleById(botConfig.whitelistedRoleId)
                            ?: error("Whitelisted role not found")
                    ).queue()
                }
            }
        }
    }
}