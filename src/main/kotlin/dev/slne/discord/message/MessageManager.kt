package dev.slne.discord.message

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.MessageCreate
import dev.slne.discord.exception.command.CommandException
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.jda
import dev.slne.discord.message.EmbedColors.ERROR_COLOR
import dev.slne.discord.message.EmbedColors.WL_QUERY
import dev.slne.discord.persistence.external.Whitelist
import dev.slne.discord.persistence.service.whitelist.WhitelistRepository
import dev.slne.discord.ticket.Ticket
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.interactions.InteractionHook
import java.time.ZonedDateTime

object MessageManager {

    suspend fun sendTicketClosedMessages(ticket: Ticket): Message? =
        ticket.thread?.sendMessage(MessageCreate {
            embeds += EmbedManager.buildTicketClosedEmbed(ticket)
        })?.await()

    suspend fun printUserWlQuery(user: User, channel: ThreadChannel) {
        channel.sendTyping().await()
        val whitelists = WhitelistRepository.findWhitelists(null, user.id, null)

        try {
            printUserWlQuery(whitelists, user.name, channel, null)
        } catch (exception: CommandException) {
            channel.sendMessage("${exception.message}").await()
        }
    }

    suspend fun printUserWlQuery(
        whitelists: List<Whitelist>, name: String, channel: ThreadChannel, hook: InteractionHook?
    ) {
        if (whitelists.isEmpty()) {
            throw CommandExceptions.WHITELIST_QUERY_NO_ENTRIES.create(name)
        }

        printWlQuery(channel, "\"" + name + "\"", whitelists)

        hook?.deleteOriginal()?.await()
    }

    suspend fun printWlQuery(
        channel: ThreadChannel,
        title: String,
        whitelists: List<Whitelist>
    ) {
        channel.sendMessage(translatable("whitelist.query.start", title.replace("\"", "")))
            .await()

        for (whitelist in whitelists) {
            channel.sendMessageEmbeds(getWhitelistQueryEmbed(whitelist)).await()
        }
    }

    fun getErrorEmbed(title: String?, description: String?) = Embed {
        this.title = title
        this.description = description
        color = ERROR_COLOR
        timestamp = ZonedDateTime.now()
    }

    suspend fun getWhitelistQueryEmbed(whitelist: Whitelist) = Embed {
        title = translatable("whitelist.query.embed.title")
        footer {
            name = translatable("whitelist.query.embed.footer")
            iconUrl = jda.selfUser.avatarUrl
        }
        description = translatable("whitelist.query.embed.description")
        color = WL_QUERY
        timestamp = ZonedDateTime.now()

        val minecraftName = whitelist.minecraftName()
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

        if (twitchLink != null) {
            field {
                name = translatable("whitelist.query.embed.field.twitch-name")
                value = "[${twitchLink}](${whitelist.clickableTwitchLink})"
            }
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
            value =
                if (whitelist.blocked == true) translatable("common.yes") else translatable("common.no")
        }
    }

    suspend fun buildMemberAddedMessage(
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
                iconUrl = executor.getAvatarUrl()
            }
        }
    }
}
