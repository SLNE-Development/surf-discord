package dev.slne.discord.discord.interaction.command

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.exception.DiscordException
import dev.slne.discord.exception.command.CommandException
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.exception.command.pre.PreCommandCheckException
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.RawMessages
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.intellij.lang.annotations.Language
import java.util.*
import kotlin.reflect.full.findAnnotation

abstract class DiscordCommand {

    private var logger = ComponentLogger.logger(DiscordCommand::class.java)

    open val subCommands: List<SubcommandData> = listOf()
    open val options: List<OptionData> = listOf()

    protected val permission: CommandPermission by lazy {
        this::class.findAnnotation<DiscordCommandMeta>()?.permission
            ?: error("No @DiscordCommandMeta annotation found")
    }

    suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val user = interaction.user
        val guild = interaction.guild ?: error("Execute cannot be called in direct messages")
        val hook = interaction.deferReply(true).await()

        try {
            if (performDiscordCommandChecks(user, guild, interaction, hook)
                && performAdditionalChecks(user, guild, interaction, hook)
            ) {
                internalExecute(interaction, hook)
            }
        } catch (exception: DiscordException) {
            logger.error("Error while executing command", exception)
            hook.editOriginal("${exception.message}").await()
        }
    }

    @Throws(PreCommandCheckException::class)
    protected open suspend fun performAdditionalChecks(
        user: User,
        guild: Guild,
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook?
    ) = true

    private suspend fun performDiscordCommandChecks(
        user: User,
        guild: Guild,
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ): Boolean {
        try {
            interaction.getGuildOrThrow()
        } catch (e: CommandException) {
            throw PreCommandCheckException(e)
        }

        val member: Member = guild.retrieveMember(user).await()
            ?: throw PreCommandCheckException(CommandExceptions.GENERIC.create())

        val guildConfig = guild.getGuildConfigOrThrow()
        val roles = guildConfig.discordGuild.roles

        val memberDiscordRoles = member.roles
        val memberRoles = memberDiscordRoles.map { role ->
            roles.filter { rolePermissions -> role.id in rolePermissions.discordRoleIds }
        }.flatten()

        val hasPermission = memberRoles.any { it.hasCommandPermission(permission) }

        if (!hasPermission) {
            hook.editOriginal(RawMessages.get("error.command.no-permission")).await()

            return false
        }

        return true
    }

    abstract suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    )

    protected fun <R> getOption(
        interaction: CommandInteractionPayload,
        name: String,
        mapper: (OptionMapping) -> R
    ): R? = interaction.getOption(name)?.let { mapper(it) }

    protected fun <R> getOptionOrThrow(
        interaction: CommandInteractionPayload,
        name: String,
        mapper: (OptionMapping) -> R,
        @Language("markdown") errorMessage: String?
    ): R = getOption(interaction, name, mapper) ?: throw CommandException(errorMessage)


    protected fun CommandInteractionPayload.getUser(
        name: String
    ) = getOption(name) { it.asUser }


    protected fun CommandInteractionPayload.getUserOrThrow(
        name: String
    ) = getUser(name) ?: throw CommandExceptions.ARG_MISSING_USER.create()

    protected fun CommandInteractionPayload.getString(
        name: String
    ) = getOption(name) { it.asString }


    protected fun CommandInteractionPayload.getStringOrThrow(
        name: String,
        @Language("markdown") errorMessage: String?
    ) = getString(name) ?: throw CommandException(errorMessage)
}
