package dev.slne.discord.message

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.MessageCreate
import dev.slne.discord.DiscordBot.jda
import dev.slne.discord.exception.command.CommandException
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.message.EmbedColors.ERROR_COLOR
import dev.slne.discord.message.EmbedColors.WL_QUERY
import dev.slne.discord.persistence.feign.dto.WhitelistDTO
import dev.slne.discord.persistence.service.whitelist.WhitelistService
import dev.slne.discord.ticket.Ticket
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.interactions.InteractionHook
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import java.time.ZonedDateTime

object MessageManager {

    private val logger = ComponentLogger.logger()

    suspend fun sendTicketClosedMessages(ticket: Ticket): Message =
        ticket.thread?.sendMessage(MessageCreate {
            embeds += EmbedManager.buildTicketClosedEmbed(ticket)
        })!!.await()

    suspend fun printUserWlQuery(user: User, channel: ThreadChannel) {
        channel.sendTyping().await()
        val whitelists = WhitelistService.checkWhitelists(null, user.id, null)

        try {
            printUserWlQuery(whitelists, user.name, channel, null)
        } catch (exception: CommandException) {
            channel.sendMessage("${exception.message}").await()
        }
    }

    suspend fun printUserWlQuery(
        whitelists: List<WhitelistDTO>, name: String, channel: ThreadChannel, hook: InteractionHook?
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
        whitelists: List<WhitelistDTO>
    ) {
        channel.sendMessage(RawMessages.get("whitelist.query.start", title.replace("\"", "")))
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

    suspend fun getWhitelistQueryEmbed(whitelist: WhitelistDTO) = Embed {
        title = RawMessages.get("whitelist.query.embed.title")
        footer {
            name = RawMessages.get("whitelist.query.embed.footer")
            iconUrl = jda.selfUser.avatarUrl
        }
        description = RawMessages.get("whitelist.query.embed.description")
        color = WL_QUERY
        timestamp = ZonedDateTime.now()

        val minecraftName = whitelist.minecraftName
        val twitchLink = whitelist.twitchLink
        val uuid = whitelist.uuid
        val discordUser = whitelist.discordUser?.await()
        val addedBy = whitelist.addedBy?.await()

        if (minecraftName != null) {
            field {
                name = RawMessages.get("whitelist.query.embed.field.minecraft-name")
                value = minecraftName
            }
        }

        if (twitchLink != null) {
            field {
                name = RawMessages.get("whitelist.query.embed.field.twitch-name")
                value = twitchLink
            }
        }

        if (discordUser != null) {
            field {
                name = RawMessages.get("whitelist.query.embed.field.discord-user")
                value = discordUser.asMention
            }
        }

        if (addedBy != null) {
            field {
                name = RawMessages.get("whitelist.query.embed.field.added-by")
                value = addedBy.asMention
            }
        }

        field {
            name = RawMessages.get("whitelist.query.embed.field.uuid")
            value = uuid.toString()
        }

        field {
            name = RawMessages.get("whitelist.query.embed.field.blocked")
            value = whitelist.blocked.toString()
        }
    }
}
