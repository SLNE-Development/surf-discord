package dev.slne.discord.discord.interaction.context

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.annotation.DiscordContextMenuCommandMeta
import dev.slne.discord.discord.interaction.command.getGuildConfigOrThrow
import dev.slne.discord.discord.interaction.command.getGuildOrThrow
import dev.slne.discord.exception.DiscordException
import dev.slne.discord.exception.command.CommandException
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.exception.command.pre.PreCommandCheckException
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.translatable
import dev.slne.discord.util.findAnnotation
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.kyori.adventure.text.logger.slf4j.ComponentLogger

abstract class DiscordContextMenuCommand<T> {

    private val logger = ComponentLogger.logger()

    private val meta: DiscordContextMenuCommandMeta by lazy {
        this::class.findAnnotation<DiscordContextMenuCommandMeta>()
            ?: error("No @DiscordContextMenuCommandMeta annotation found")
    }

    protected val permission: CommandPermission by lazy { meta.permission }
    private val ephemeral: Boolean by lazy { meta.ephemeral }
    private val sendTyping: Boolean by lazy { meta.sendTyping }
    val type: DiscordContextMenuCommandType by lazy { meta.type }

    @Suppress("UNCHECKED_CAST")
    suspend fun execute(interaction: UserContextInteractionEvent) =
        execute(interaction as GenericContextInteractionEvent<T>)

    @Suppress("UNCHECKED_CAST")
    suspend fun execute(interaction: MessageContextInteractionEvent) =
        execute(interaction as GenericContextInteractionEvent<T>)

    suspend fun execute(interaction: GenericContextInteractionEvent<T>) {
        val user = interaction.user
        val guild = interaction.guild ?: error("Execute cannot be called in direct messages")
        val hook = interaction.deferReply(ephemeral).await()
        if (sendTyping) {
            interaction.messageChannel.sendTyping().queue()
        }

        try {
            if (performDiscordCommandChecks(user, guild, interaction, hook)
                && performAdditionalChecks(user, guild, interaction, hook)
            ) {
                internalExecute(interaction, hook)
            }
        } catch (exception: DiscordException) {
            hook.editOriginal("${exception.message}").await()
        } catch (exception: Exception) {
            logger.error("Error while executing command", exception)
            hook.editOriginal(translatable("error.generic")).await()
        }
    }

    @Throws(PreCommandCheckException::class)
    protected open suspend fun performAdditionalChecks(
        user: User,
        guild: Guild,
        interaction: GenericContextInteractionEvent<T>,
        hook: InteractionHook
    ) = true

    private suspend fun performDiscordCommandChecks(
        user: User,
        guild: Guild,
        interaction: GenericContextInteractionEvent<T>,
        hook: InteractionHook
    ): Boolean {
        try {
            interaction.getGuildOrThrow()
        } catch (e: CommandException) {
            throw PreCommandCheckException(e)
        }

        val member = guild.retrieveMember(user).await()
            ?: throw PreCommandCheckException(CommandExceptions.GENERIC.create())

        val guildConfig = guild.getGuildConfigOrThrow()
        val roles = guildConfig.discordGuild.roles

        val memberDiscordRoles = member.roles
        val memberRoles = memberDiscordRoles
            .flatMap { role -> roles.filter { rolePermissions -> role.id in rolePermissions.discordRoleIds } }
        val hasPermission = memberRoles.any { it.hasCommandPermission(permission) }

        if (!hasPermission) {
            hook.editOriginal(translatable("error.command.no-permission")).await()
            return false
        }

        return true
    }

    abstract suspend fun internalExecute(
        interaction: GenericContextInteractionEvent<T>,
        hook: InteractionHook
    )
}