package dev.slne.discord.ticket.message

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import dev.slne.discord.DiscordBot
import dev.slne.discord.ticket.Ticket
import dev.slne.discord.ticket.message.attachment.TicketMessageAttachment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.requests.RestAction
import org.jetbrains.annotations.Contract
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.util.*
import java.util.function.Function

class TicketMessage {

    @JsonProperty("id")
    val id: Long = 0

    @JsonProperty("ticket_id")
    var ticketId: UUID? = null

    @JsonProperty("content")
    var jsonContent: String? = null

    @JsonProperty("message_id")
    var messageId: String? = null

    @JsonProperty("author_id")
    var authorId: String? = null

    @JsonProperty("author_name")
    var authorName: String? = null

    @JsonProperty("author_avatar_url")
    var authorAvatarUrl: String? = null

    @JsonProperty("message_created_at")
    var messageCreatedAt: ZonedDateTime? = null

    @JsonProperty("message_edited_at")
    var messageEditedAt: ZonedDateTime? = null

    @JsonProperty("message_deleted_at")
    var messageDeletedAt: ZonedDateTime? = null

    @JsonProperty("references_message_id")
    var referencesMessageId: String? = null

    @JsonProperty("attachments")
    var attachments = mutableListOf<TicketMessageAttachment>()

    @JsonProperty("bot_message")
    var botMessage = false

    suspend fun delete(): TicketMessage? =
        messageDeletedAt?.let {
            copy(this).run {
                messageDeletedAt = ZonedDateTime.now()

                // FIXME: 08.10.2024 02:49 test me
                create()
            }
        }

    suspend fun create(): TicketMessage {
        TODO("Fix this")
    }

    suspend fun update(updatedMessage: Message): TicketMessage =
        copy(this).run {
            jsonContent = updatedMessage.contentDisplay
            messageCreatedAt =
                Times.convertFromLocalDateTime(updatedMessage.timeCreated.toLocalDateTime())
            messageEditedAt =
                updatedMessage.timeEdited?.let { Times.convertFromLocalDateTime(it.toLocalDateTime()) }
            attachments = updatedMessage.attachments.map { attachment ->
                TicketMessageAttachment().apply {
                    name = attachment.fileName
                    url = attachment.url
                    extension = attachment.fileExtension
                    size = attachment.size
                    description = attachment.description
                }
            }.toMutableList()

            create()
        }

    @get:JsonIgnore
    val message: RestAction<Message>?
        get() = messageId?.let { ticket.thread?.retrieveMessageById(it) }

    @get:JsonIgnore
    val author: RestAction<User>?
        get() = authorId?.let { DiscordBot.jda.retrieveUserById(it) }

    @get:JsonIgnore
    val referencesMessage: RestAction<Message>?
        get() = referencesMessageId?.let { ticket.thread?.retrieveMessageById(it) }

    @get:JsonIgnore
    val ticket: Ticket
        get() = DiscordBot.ticketManager.getTicketById(ticketId)

    @JsonIgnore
    suspend fun content() =
        if (jsonContent != null) jsonContent else message?.let { withContext(Dispatchers.IO) { it.complete() }.contentDisplay }


    companion object {
        /**
         * Constructor for a ticket message
         *
         * @param ticket  the ticket
         * @param message The message to create the ticket message from
         */
        @JvmStatic
        fun fromTicketAndMessage(ticket: Ticket, message: Message): TicketMessage {
            return builder()
                .ticketId(ticket.ticketId)
                .messageId(message.id)
                .jsonContent(message.contentDisplay)
                .authorId(message.author.id)
                .authorName(message.author.name)
                .authorAvatarUrl(message.author.avatarUrl)
                .messageCreatedAt(getTimeAt(message.timeCreated))
                .messageEditedAt(getTimeAt(message.timeEdited))
                .referencesMessageId(
                    mapNullable<T?, R>(message.messageReference,
                        Function<T?, R> { obj: T? -> obj.getMessageId() })
                )
                .attachments(
                    message.attachments.stream().map<Any> { attachment: Message.Attachment ->
                        TicketMessageAttachment.builder()
                            .name(attachment.fileName)
                            .url(attachment.url)
                            .extension(attachment.fileExtension)
                            .size(attachment.size)
                            .description(attachment.description)
                            .build()
                    }
                        .toList())
                .botMessage(message.author.isBot)
                .build()
        }

        @Contract("null -> null")
        private fun getTimeAt(time: OffsetDateTime?): ZonedDateTime {
            return mapNullable<OffsetDateTime?, ZonedDateTime>(
                time
            ) { time1: OffsetDateTime? ->
                Times.convertFromLocalDateTime(
                    time1!!.toLocalDateTime()
                )
            }!!
        }

        private fun <T, R> mapNullable(value: T?, mapper: Function<T, R>): R? {
            return if (value != null) mapper.apply(value) else null
        }

        /**
         * Copy ticket message.
         *
         * @param clone the clone
         * @return the ticket message
         */
        fun copy(clone: TicketMessage): TicketMessage {
            val ticketMessage = TicketMessage()

            ticketMessage.ticketId = clone.ticketId
            ticketMessage.messageId = clone.messageId
            ticketMessage.jsonContent = clone.jsonContent
            ticketMessage.authorId = clone.authorId
            ticketMessage.authorName = clone.authorName
            ticketMessage.authorAvatarUrl = clone.authorAvatarUrl
            ticketMessage.messageCreatedAt = clone.messageCreatedAt
            ticketMessage.messageEditedAt = clone.messageEditedAt
            ticketMessage.messageDeletedAt = clone.messageDeletedAt
            ticketMessage.referencesMessageId = clone.referencesMessageId
            ticketMessage.attachments = ArrayList<TicketMessageAttachment>(clone.attachments)
            ticketMessage.botMessage = clone.botMessage

            return ticketMessage
        }

        /**
         * Returns a ticket message from a message id
         *
         * @param id the id
         * @return the ticket message
         */
        fun getByMessageId(id: Long): TicketMessage {
            return DiscordBot.getInstance().getTicketManager().getTickets().stream()
                .map(Ticket::messages)
                .filter { obj: Any? -> Objects.nonNull(obj) }
                .flatMap { obj: List<*> -> obj.stream() }
                .filter { message -> message.getId() === id }
                .findFirst().orElse(null)
        }
    }
}
