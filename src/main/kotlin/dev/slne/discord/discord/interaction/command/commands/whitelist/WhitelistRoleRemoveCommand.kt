package dev.slne.discord.discord.interaction.command.commands.whitelist

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.DiscordCommand
import dev.slne.discord.discord.interaction.command.getGuildConfigOrThrow
import dev.slne.discord.discord.interaction.command.getGuildOrThrow
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.translatable
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.kyori.adventure.text.logger.slf4j.ComponentLogger

@DiscordCommandMeta(
    name = "wlrole",
    description = "Entfernt alle Benutzer aus der Whitelist Rolle.",
    permission = CommandPermission.WHITELIST_ROLE
)
object WhitelistRoleRemoveCommand : DiscordCommand() {
    private val logger = ComponentLogger.logger()

    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        val guild = interaction.getGuildOrThrow()
        val guildConfig = guild.getGuildConfigOrThrow()
        val whitelistedRole = guild.getRoleById(guildConfig.discordGuild.whitelistRoleId)
            ?: throw CommandExceptions.WHITELIST_ROLE_NOT_REGISTERED()

        removeRoleFromMembers(guild, whitelistedRole, hook)
    }

    private suspend fun removeRoleFromMembers(
        guild: Guild,
        whitelistedRole: Role,
        hook: InteractionHook
    ) {
        hook.editOriginal(translatable("interaction.command.ticket.whitelist.role.remove.removing"))
            .await()

        val failed = mutableListOf<Member>()

        try {
            val members = guild.findMembersWithRoles(whitelistedRole).await()
            logger.info("Removing ${members.size} members from whitelist role")

            try {
                for (member in members) {
                    guild.removeRoleFromMember(member, whitelistedRole).await()
                }
            } catch (error: Throwable) {
                logger.error("Error while removing role from members", error)
                failed.addAll(members)
            }

            hook.editOriginal(
                translatable(
                    "interaction.command.ticket.whitelist.role.remove.removed",
                    members.size - failed.size
                )
            ).await()
        } catch (error: Throwable) {
            logger.error("Error while fetching members with role", error)
            hook.editOriginal(translatable("error.generic")).await()
        }

        if (failed.isNotEmpty()) {
            hook.editOriginal(
                translatable("interaction.command.ticket.whitelist.role.remove.failed",
                    failed.size,
                    failed.joinToString(", ") { it.effectiveName }
                )
            ).await()
        }
    }
}
