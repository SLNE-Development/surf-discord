package dev.slne.discord.listener.whitelist

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.events.listener
import dev.slne.discord.DiscordBot
import dev.slne.discord.guild.getDiscordGuildByGuildId
import dev.slne.discord.persistence.service.whitelist.WhitelistRepository
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent

object WhitelistJoinListener {

    init {
        DiscordBot.jda.listener<GuildMemberJoinEvent> { event ->
            val user = event.user
            val guild = event.guild
            val whitelist = WhitelistRepository.getWhitelistByDiscordId(user.id) ?: return@listener

            whitelist.blocked = false;
            whitelist.save()

            val guildConfig = getDiscordGuildByGuildId(guild.id)?.discordGuild ?: return@listener
            val whitelistedRole = guildConfig.whitelistRole ?: return@listener
            val member = guild.retrieveMember(user).await() ?: return@listener

            guild.addRoleToMember(member, whitelistedRole).await()
        }
    }
}
