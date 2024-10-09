package dev.slne.discord.message

import dev.slne.discord.config.discord.GuildConfig
import dev.slne.discord.exception.command.CommandException
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.extensions.MemberExtensions
import dev.slne.discord.message.EmbedColors.ERROR_COLOR
import dev.slne.discord.message.EmbedColors.WL_QUERY
import dev.slne.discord.spring.feign.dto.WhitelistDTO
import dev.slne.discord.spring.service.whitelist.WhitelistService
import dev.slne.discord.ticket.Ticket
import dev.slne.discord.ticket.member.TicketMember
import dev.slne.discord.util.TimeUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.requests.ErrorResponse
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Function
import javax.annotation.Nonnull

/**
 * The type Message manager.
 */
@Service
@ExtensionMethod([MemberExtensions::class])
class MessageManager private constructor(
    private val embedManager: EmbedManager,
    jda: JDA,
    whitelistService: WhitelistService
) {
    private val jda: JDA = jda
    private val whitelistService: WhitelistService = whitelistService

    @Async
    fun sendTicketClosedMessages(ticket: Ticket): CompletableFuture<Void?> {
        val closeEmbed = embedManager.buildTicketClosedEmbed(ticket).join()
            ?: return CompletableFuture.completedFuture(null)

        val author: User = ticket.ticketAuthorNow
        val guild = ticket.guild
            ?: return CompletableFuture.completedFuture(null)

        val guildConfig: GuildConfig = GuildConfig.getByGuild(guild)
            ?: return CompletableFuture.completedFuture<Void?>(null)

        return CompletableFuture.completedFuture<Void?>(
            CompletableFuture.allOf(ticket.getMembers().stream()
                .filter { obj: TicketMember -> obj.isActivated() }
                .map { ticketMember ->
                    sendTicketClosedMessage(
                        closeEmbed, ticketMember, author, guild,
                        guildConfig
                    )
                }
                .toArray { _Dummy_.__Array__() }).join()
        )
    }

    @Async
    protected fun sendTicketClosedMessage(
      closedEmbed: MessageEmbed,
      receiver: TicketMember,
      author: User,
      guild: Guild,
      guildConfig: GuildConfig?
    ): CompletableFuture<Void?> {
        val receiverUser: User = receiver.getMemberNow()

        if (receiverUser == null || receiverUser == jda.getSelfUser()) {
            return CompletableFuture.completedFuture(null)
        }

        val isAuthor = receiverUser == author
        val receiverMember = guild.retrieveMember(receiverUser).complete()
        val isTeamMember: Boolean = receiverMember.isTeamMember(guildConfig)

        if (!isAuthor && isTeamMember) {
            return CompletableFuture.completedFuture(null)
        }

        try {
            receiverUser.openPrivateChannel()
                .flatMap<Message>(Function { channel: PrivateChannel ->
                    channel.sendMessageEmbeds(
                        closedEmbed
                    )
                })
                .complete()
        } catch (e: ErrorResponseException) {
            if (e.errorResponse == ErrorResponse.CANNOT_SEND_TO_USER) {
                return CompletableFuture.completedFuture(null)
            }

            LOGGER.error(
                "Failed to send ticket closed message to user %s".formatted(receiverUser),
                e
            )
        }

        return CompletableFuture.completedFuture(null)
    }

    @Async
    fun printUserWlQuery(user: User, channel: TextChannel) {
        channel.sendTyping().queue()
        val whitelists: List<WhitelistDTO?> = whitelistService.checkWhitelists(null, user.id, null)
            .join()

        try {
            printUserWlQuery(whitelists, user.name, channel, null)
        } catch (e: CommandException) {
            channel.sendMessage(e.message).queue()
        }
    }

    @Throws(CommandException::class)
    fun printUserWlQuery(
        whitelists: List<WhitelistDTO?>, name: String, channel: TextChannel,
        hook: InteractionHook?
    ) {
        if (whitelists.isEmpty()) {
            throw CommandExceptions.WHITELIST_QUERY_NO_ENTRIES.create(name)
        }

        printWlQuery(channel, "\"" + name + "\"", whitelists)

        if (hook != null) {
            hook.deleteOriginal().queue()
        }
    }

    @Async
    fun printWlQuery(channel: TextChannel, title: String, whitelists: List<WhitelistDTO?>) {
        var title = title
        title = title.replace("\"", "")
        channel.sendMessage(RawMessages.Companion.get("whitelist.query.start", title)).queue()

        for (whitelist in whitelists) {
            val embed = getWhitelistQueryEmbed(whitelist).join()
            channel.sendMessageEmbeds(embed).queue()
        }
    }

    @Async
    fun getWhitelistQueryEmbed(whitelist: WhitelistDTO): CompletableFuture<MessageEmbed> {
        val builder: EmbedBuilder = EmbedBuilder()
            .setTitle(RawMessages.Companion.get("whitelist.query.embed.title"))
            .setFooter(
                RawMessages.Companion.get("whitelist.query.embed.footer"),
                jda.getSelfUser().getAvatarUrl()
            )
            .setDescription(RawMessages.Companion.get("whitelist.query.embed.description"))
            .setColor(WL_QUERY)
            .setTimestamp(TimeUtils.berlinTimeProvider().getCurrentTime())

        val minecraftName: String = whitelist.getMinecraftName()
        val twitchLink: String = whitelist.getTwitchLink()
        val uuid: UUID = whitelist.getUuid()
        val discordUser: User = whitelist.getDiscordUserNow()
        val addedBy: User = whitelist.getAddedByNow()

        if (minecraftName != null) {
            builder.addField(
                RawMessages.Companion.get("whitelist.query.embed.field.minecraft-name"),
                minecraftName,
                true
            )
        }

        if (twitchLink != null) {
            builder.addField(
                RawMessages.Companion.get("whitelist.query.embed.field.twitch-name"), twitchLink,
                true
            )
        }

        if (discordUser != null) {
            builder.addField(
                RawMessages.Companion.get("whitelist.query.embed.field.discord-user"),
                discordUser.asMention, true
            )
        }

        if (addedBy != null) {
            builder.addField(
                RawMessages.Companion.get("whitelist.query.embed.field.added-by"),
                addedBy.asMention, true
            )
        }

        builder.addField(
            RawMessages.Companion.get("whitelist.query.embed.field.uuid"),
            uuid.toString(),
            false
        )

        return CompletableFuture.completedFuture(builder.build())
    }

    companion object {
        private val LOGGER = ComponentLogger.logger("MessageManager")

        /**
         * Returns an error MessageEmbed with the given title and description.
         *
         * @param title       The title of the embed.
         * @param description The description of the embed.
         * @return The MessageEmbed.
         */
        @JvmStatic
        @Nonnull
        fun getErrorEmbed(title: String?, description: String?): MessageEmbed {
            val embedBuilder: EmbedBuilder = EmbedBuilder()

            embedBuilder.setTitle(title)
            embedBuilder.setDescription(description)
            embedBuilder.setColor(ERROR_COLOR)
            embedBuilder.setTimestamp(TimeUtils.berlinTimeProvider().getCurrentTime())

            return embedBuilder.build()
        }
    }
}
