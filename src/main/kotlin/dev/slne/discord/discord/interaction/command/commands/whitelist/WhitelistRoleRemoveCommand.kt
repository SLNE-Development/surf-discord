package dev.slne.discord.discord.interaction.command.commands.whitelist

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.DiscordCommand
import dev.slne.discord.discord.interaction.command.getGuildConfigOrThrow
import dev.slne.discord.discord.interaction.command.getGuildOrThrow
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.RawMessages
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
class WhitelistRoleRemoveCommand : DiscordCommand() {

    private val logger: ComponentLogger =
        ComponentLogger.logger(WhitelistRoleRemoveCommand::class.java)

    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        val guild = interaction.getGuildOrThrow()
        val guildConfig = guild.getGuildConfigOrThrow()
        val whitelistedRole = guild.getRoleById(guildConfig.discordGuild.whitelistRoleId)
            ?: throw CommandExceptions.WHITELIST_ROLE_NOT_REGISTERED.create()

        removeRoleFromMembers(guild, whitelistedRole, hook)
    }

    private suspend fun removeRoleFromMembers(
        guild: Guild,
        whitelistedRole: Role,
        hook: InteractionHook
    ) {
        hook.editOriginal(
            RawMessages.get("interaction.command.ticket.whitelist.role.remove.removing")
        ).await()

        guild.findMembersWithRoles(whitelistedRole)
            .onSuccess { members: List<Member> ->
                suspend {
                    for (member in members) {
                        guild.removeRoleFromMember(member, whitelistedRole).await()
                    }

                    hook.editOriginal(
                        RawMessages.get(
                            "interaction.command.ticket.whitelist.role.remove.removed",
                            members.size
                        )
                    ).await()
                }
            }.onError { error ->
                suspend {
                    logger.error("Error while removing role from members", error)
                    hook.editOriginal(RawMessages.get("error.generic")).await()
                }
            }
    }
}
