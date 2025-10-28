package dev.slne.discordold.message

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.MessageCreate
import dev.slne.discordold.discord.interaction.command.getGuildConfig
import dev.slne.discordold.exception.command.CommandException
import dev.slne.discordold.exception.command.CommandExceptions
import dev.slne.discordold.message.EmbedColors.ERROR_COLOR
import dev.slne.discordold.message.EmbedColors.WL_QUERY
import dev.slne.discordold.persistence.external.Whitelist
import dev.slne.discordold.persistence.service.user.UserService
import dev.slne.discordold.persistence.service.whitelist.WhitelistService
import dev.slne.discordold.ticket.Ticket
import dev.slne.discordold.util.memberOrNull
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.interactions.InteractionHook
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class MessageManager(
    private val whitelistService: WhitelistService,
    private val userService: UserService,
    private val jda: JDA
) {

    suspend fun sendTicketClosedMessages(ticket: Ticket): Message? =
        ticket.thread?.sendMessage(MessageCreate {
            embeds += EmbedManager.buildTicketClosedEmbed(ticket)
        })?.await()

    suspend fun sendTicketClosedUserPrivateMessage(ticket: Ticket) {
        ticket.author.await()?.openPrivateChannel()?.await()?.sendMessage(MessageCreate {
            embeds += EmbedManager.buildTicketClosedUserPrivateMessageEmbed(
                ticket,
                owner = true
            )
        })?.await()

        val guild = ticket.guild ?: return
        val thread = ticket.thread ?: return

        for (ticketMember in thread.threadMembers) {
            if (hasMemberReceiveClosePmNegatePermission(
                    ticketMember.user,
                    guild
                ) || ticketMember.user.isBot
            ) {
                continue
            }

            ticketMember.user.openPrivateChannel().await()?.sendMessage(MessageCreate {
                embeds += EmbedManager.buildTicketClosedUserPrivateMessageEmbed(ticket)
            })?.await()
        }
    }

    private suspend fun hasMemberReceiveClosePmNegatePermission(user: User, guild: Guild): Boolean {
        val roles = user.memberOrNull(guild).await().roles
        val guildTeamRoles =
            guild.getGuildConfig()?.discordGuild?.roles?.flatMap { it.discordRoleIds }
                ?: return false

        return roles.any { it.id in guildTeamRoles }
    }


    suspend fun printUserWlQuery(user: User, channel: MessageChannel, requester: String?) {
        channel.sendTyping().await()
        val whitelists = whitelistService.findWhitelists(null, user.id, null)

        try {
            printUserWlQuery(whitelists, user.name, channel, null, requester)
        } catch (exception: CommandException) {
            channel.sendMessage("${exception.message}").await()
        }
    }

    suspend fun printUserWlQuery(
        whitelists: List<Whitelist>, name: String, channel: MessageChannel, hook: InteractionHook?, requester: String?
    ) {
        if (whitelists.isEmpty()) {
            throw CommandExceptions.WHITELIST_QUERY_NO_ENTRIES.create(name)
        }

        printWlQuery(channel, "\"" + name + "\"", whitelists, requester)

        hook?.deleteOriginal()?.await()
    }

    private suspend fun printWlQuery(
        channel: MessageChannel,
        title: String,
        whitelists: List<Whitelist>,
        requester: String?
    ) {
        channel.sendMessage(translatable("whitelist.query.start", title.replace("\"", "")))
            .await()

        for (whitelist in whitelists) {
            channel.sendMessageEmbeds(getWhitelistQueryEmbed(whitelist, requester)).await()
        }
    }

    fun getErrorEmbed(title: String?, description: String?) = Embed {
        this.title = title
        this.description = description
        color = ERROR_COLOR
        timestamp = ZonedDateTime.now()
    }

    suspend fun getWhitelistQueryEmbed(whitelist: Whitelist, requester: String?) = Embed {
        title = translatable("whitelist.query.embed.title")
        footer {
            name = if (requester != null) {
                translatable("whitelist.query.embed.footer", requester)
            } else {
                translatable("whitelist.query.embed.footer.no-requester")
            }
            iconUrl = jda.selfUser.avatarUrl
        }
        description = null
        color = WL_QUERY
        timestamp = ZonedDateTime.now()

        val minecraftName = userService.getUsernameByUuid(whitelist.uuid)
        val twitchLink = whitelist.twitchLink
        val uuid = whitelist.uuid
        val discordUser = whitelist.user?.await()
        val addedBy = whitelist.addedBy?.await()

        field {
            name = translatable("whitelist.query.embed.field.uuid")
            value = uuid.toString()
            inline = false
        }

        if (minecraftName != null) {
            field {
                name = translatable("whitelist.query.embed.field.minecraft-name")
                value = "`${minecraftName}`"
            }
        }

        field {
            name = translatable("whitelist.query.embed.field.twitch-name")
            value = "[${twitchLink}](${whitelist.clickableTwitchLink})"
        }

        if (discordUser != null) {
            field {
                name = translatable("whitelist.query.embed.field.discord-user")
                value = discordUser.asMention
            }
        }

        if (addedBy != null) {
            field {
                name = translatable("whitelist.query.embed.field.added-by")
                value = addedBy.asMention
            }
        }

        field {
            name = translatable("whitelist.query.embed.field.blocked")
            value = if (whitelist.blocked) translatable("common.yes") else translatable("common.no")
        }
    }

    fun buildWhitelistDeletedEmbed(
        executor: User,
        deletedUser: String,
        deletedUserUuid: String
    ) = MessageCreate {
        embed {
            title = translatable("interaction.command.whitelist.delete.embed.title")
            description =
                translatable("interaction.command.whitelist.delete.embed.description", deletedUser, deletedUserUuid)
            timestamp = ZonedDateTime.now()
            color = EmbedColors.WHITELIST_DELETE
            footer {
                name = translatable(
                    "interaction.command.whitelist.delete.embed.footer",
                    executor.name
                )
                iconUrl = executor.avatarUrl
            }
        }
    }

    fun buildMemberAddedMessage(
        member: Member,
        executor: User
    ) = MessageCreate {
        content = member.asMention
        embed {
            title = translatable("interaction.command.ticket.member.embed.title")
            description = translatable("interaction.command.ticket.member.embed.description")
            timestamp = ZonedDateTime.now()
            color = EmbedColors.ADD_TICKET_MEMBER
            footer {
                name = translatable(
                    "interaction.command.ticket.member.embed.footer",
                    executor.name
                )
                iconUrl = executor.avatarUrl
            }
        }
    }
}
