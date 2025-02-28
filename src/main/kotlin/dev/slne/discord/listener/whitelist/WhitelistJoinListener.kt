package dev.slne.discord.listener.whitelist

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.events.listener
import dev.slne.discord.guild.getDiscordGuildByGuildId
import dev.slne.discord.persistence.service.whitelist.WhitelistService
import jakarta.annotation.PostConstruct
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.springframework.stereotype.Component

@Component
class WhitelistJoinListener(private val jda: JDA, private val whitelistService: WhitelistService) {

    private val logger = ComponentLogger.logger()

    @PostConstruct
    fun registerListener() {
        jda.listener<GuildMemberJoinEvent> { event ->
            val user = event.user
            val guild = event.guild
            val whitelist = whitelistService.findByDiscordId(user.id) ?: return@listener

            whitelist.blocked = false
            whitelistService.saveWhitelist(whitelist)

            val guildConfig = getDiscordGuildByGuildId(guild.id)?.discordGuild ?: return@listener
            val whitelistedRole = guildConfig.whitelistRole ?: return@listener
            val member = guild.retrieveMember(user).await() ?: return@listener

            guild.addRoleToMember(member, whitelistedRole).await()

            logger.info("User ${user.name} left the server and was blocked.")

            user.openPrivateChannel().await()
                .sendMessage("Da du dich auf der Whitelist für den CastCrafter Community Server befindest, wurde deine Whitelist entblockt und dir die Rolle \"${whitelistedRole.name}\" wieder zugeteilt! Viel Spaß auf dem Server!")
                .await()
        }
    }
}
