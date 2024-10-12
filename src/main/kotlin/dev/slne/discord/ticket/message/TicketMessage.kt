package dev.slne.discord.ticket.message

import dev.slne.discord.DiscordBot
import dev.slne.discord.message.toEuropeBerlin
import dev.slne.discord.spring.service.ticket.TicketMessageService
import dev.slne.discord.spring.service.ticket.TicketService
import dev.slne.discord.ticket.Ticket
import dev.slne.discord.ticket.message.attachment.TicketMessageAttachment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.requests.RestAction
import java.time.ZonedDateTime
import java.util.*

class TicketMessage {

    val id: Long = 0
    var ticketId: UUID? = null
    var jsonContent: String? = null
    var messageId: String? = null
    var authorId: String? = null
    var authorName: String? = null
    var authorAvatarUrl: String? = null
    var messageCreatedAt: ZonedDateTime? = null
    var messageEditedAt: ZonedDateTime? = null
    var messageDeletedAt: ZonedDateTime? = null
    var referencesMessageId: String? = null
    var attachments = listOf<TicketMessageAttachment>()
    var botMessage = false

    suspend fun delete() = ticket?.let {
        messageDeletedAt = ZonedDateTime.now().toEuropeBerlin()
        
        TicketMessageService.deleteTicketMessage(it, this)
    }

    suspend fun create() = ticket?.let { TicketMessageService.createTicketMessage(it, this) }

    suspend fun update() = ticket?.let { TicketMessageService.updateTicketMessage(it, this) }

    val message: RestAction<Message>?
        get() = messageId?.let { ticket?.thread?.retrieveMessageById(it) }

    val author: RestAction<User>?
        get() = authorId?.let { DiscordBot.jda.retrieveUserById(it) }

    val referencesMessage: RestAction<Message>?
        get() = referencesMessageId?.let { ticket?.thread?.retrieveMessageById(it) }

    val ticket: Ticket?
        get() = TicketService.getTicketById(ticketId!!)

    suspend fun content() =
        if (jsonContent != null) jsonContent else message?.let { withContext(Dispatchers.IO) { it.complete() }.contentDisplay }

    companion object {
        fun fromTicketAndMessage(ticket: Ticket, message: Message) = TicketMessage().apply {
            this.ticketId = ticket.ticketId
            this.messageId = message.id
            this.jsonContent = message.contentDisplay
            this.authorId = message.author.id
            this.authorName = message.author.name
            this.authorAvatarUrl = message.author.avatarUrl
            this.messageCreatedAt = message.timeCreated.toZonedDateTime()
            this.messageEditedAt = message.timeEdited?.toZonedDateTime()
            this.referencesMessageId = message.messageReference?.messageId
            this.attachments = message.attachments.map {
                TicketMessageAttachment(
                    this,
                    name = it.fileName,
                    url = it.url,
                    extension = it.fileExtension,
                    size = it.size,
                    description = it.description
                )
            }
            this.botMessage = message.author.isBot
        }

        fun getByMessageId(id: Long) = TicketService.tickets
            .mapNotNull(Ticket::messages)
            .flatten()
            .firstOrNull { it.id == id }
    }
}
