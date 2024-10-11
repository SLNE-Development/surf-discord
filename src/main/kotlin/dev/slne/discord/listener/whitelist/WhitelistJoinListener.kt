package dev.slne.discord.listener.whitelist

import dev.slne.discord.config.discord.GuildConfig
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

/**
 * The type WhitelistDTO join listener.
 */
@DiscordListener
class WhitelistJoinListener @Autowired constructor(whitelistService: WhitelistService) :
    ListenerAdapter() {
    private val whitelistService: WhitelistService

    init {
        this.whitelistService = whitelistService
    }

    override fun onGuildMemberJoin(@Nonnull event: GuildMemberJoinEvent) {
        handleEvent(event)
    }

    @Async
    protected fun handleEvent(event: GuildMemberJoinEvent) {
        val user: User = event.getUser()
        val guild: Guild = event.getGuild()
        val whitelist: WhitelistDTO? = whitelistService.getWhitelistByDiscordId(user.getId()).join()

        if (whitelist == null) {
            return
        }

        val guildConfig: GuildConfig? = GuildConfig.getConfig(guild.getId())
        if (guildConfig == null) {
            return
        }

        val whitelistedRole: Role? = guildConfig.getWhitelistedRole()
        if (whitelistedRole == null) {
            return
        }

        val member: Member? = guild.retrieveMember(user).complete()
        if (member == null) {
            return
        }

        guild.addRoleToMember(member, whitelistedRole).queue()
    }
}
