package dev.slne.discord.discord.interaction.command

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.interactions.commands.*
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.exception.DiscordException
import dev.slne.discord.exception.command.CommandException
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.exception.command.pre.PreCommandCheckException
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.translatable
import dev.slne.discord.util.ExceptionFactory
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel
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

    private val logger = ComponentLogger.logger()
    open val subCommands: List<SubcommandData> = listOf()
    open val options: List<OptionData> = listOf()

    private val meta: DiscordCommandMeta by lazy {
        this::class.findAnnotation<DiscordCommandMeta>()
            ?: error("No @DiscordCommandMeta annotation found")
    }
    protected val permission: CommandPermission by lazy { meta.permission }
    private val ephemeral: Boolean by lazy { meta.ephemeral }

    suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val user = interaction.user
        val guild = interaction.guild ?: error("Execute cannot be called in direct messages")
        val hook = interaction.deferReply(ephemeral).await()

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
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
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

    protected inline fun <reified T> CommandInteractionPayload.getOptionOrThrow(
        name: String,
        exception: ExceptionFactory.CommandExceptionFactory? = null,
        @Language("markdown") exceptionMessage: String? = null
    ): T {
        val except = exception?.create() ?: exceptionMessage?.let { CommandException(it) }

        return when (T::class.java) {
            User::class.java -> getOption(name, OptionMapping::getAsUser) as? T
                ?: throw except ?: CommandExceptions.ARG_MISSING_USER.create()

            Member::class.java -> getOption(name, OptionMapping::getAsMember) as? T
                ?: throw except ?: CommandExceptions.ARG_MISSING_USER.create()

            Role::class.java -> getOption(name, OptionMapping::getAsRole) as? T
                ?: throw except ?: CommandExceptions.ARG_MISSING_ROLE.create()

            Integer::class.java, Int::class.java -> getOption(name, OptionMapping::getAsInt) as? T
                ?: throw except ?: CommandExceptions.ARG_MISSING_NUMBER.create()

            Long::class.java, java.lang.Long::class.java -> getOption(
                name,
                OptionMapping::getAsLong
            ) as? T ?: throw except ?: CommandExceptions.ARG_MISSING_NUMBER.create()

            Double::class.java -> getOption(name, OptionMapping::getAsDouble) as? T
                ?: throw except ?: CommandExceptions.ARG_MISSING_NUMBER.create()

            Boolean::class.java, java.lang.Boolean::class.java -> getOption(
                name,
                OptionMapping::getAsBoolean
            ) as? T ?: throw except ?: CommandExceptions.ARG_MISSING_BOOLEAN.create()

            String::class.java -> getOption(name, OptionMapping::getAsString) as? T ?: throw except
                ?: CommandExceptions.ARG_MISSING_STRING.create()

            Message.Attachment::class.java -> getOption(name, OptionMapping::getAsAttachment) as? T
                ?: throw except ?: CommandExceptions.ARG_MISSING_ATTACHMENT.create()

            IMentionable::class.java -> getOption(name, OptionMapping::getAsMentionable) as? T
                ?: throw except ?: CommandExceptions.ARG_MISSING_USER.create()

            else -> {
                if (GuildChannel::class.java.isAssignableFrom(T::class.java)) {
                    val channel = getOption(name, OptionMapping::getAsChannel) ?: throw except
                        ?: CommandExceptions.ARG_MISSING_USER.create()
                    when (channel) {
                        is T -> channel
                        else -> throw NoSuchElementException("Cannot resolve channel of type ${T::class.java.simpleName}")
                    }
                } else {
                    throw NoSuchElementException("Type ${T::class.java.simpleName} is unsupported for getOption(name) resolution. Try updating or using a different type!")
                }
            }

        }
    }

    protected inline fun <reified T> option(
        name: String,
        description: String,
        required: Boolean = true,
        autocomplete: Boolean = false,
        builder: OptionData.() -> Unit = {}
    ) = Option<T>(name, description, required, autocomplete, builder)

    protected inline fun subcommand(
        name: String,
        description: String,
        builder: SubcommandData.() -> Unit = {}
    ) = Subcommand(name, description, builder)

    protected fun OptionData.length(range: IntRange) = setRequiredLength(range.first, range.last)

}
