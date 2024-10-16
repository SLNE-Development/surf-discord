package dev.slne.discord.ticket.message

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.DiscordBot
import dev.slne.discord.message.toEuropeBerlin
import dev.slne.discord.persistence.service.ticket.TicketMessageService
import dev.slne.discord.persistence.service.ticket.TicketService
import dev.slne.discord.ticket.Ticket
import dev.slne.discord.ticket.message.attachment.TicketMessageAttachment
import dev.slne.discord.ticket.message.attachment.toTicketMessageAttachment
import jakarta.persistence.*
import net.dv8tion.jda.api.entities.Message
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.TimeZoneStorage
import org.hibernate.annotations.TimeZoneStorageType
import org.hibernate.type.SqlTypes
import java.time.ZonedDateTime

@Entity
@Table(name = "ticket_messages")
open class TicketMessage protected constructor() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JdbcTypeCode(SqlTypes.BIGINT)
    @Column(name = "id", nullable = false)
    open var id: Long? = null
        protected set

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "message_id", length = 20)
    open var messageId: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Lob
    @Column(name = "json_content")
    open var jsonContent: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "author_id", nullable = false, length = 20)
    open var authorId: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "author_name", nullable = false, length = 32)
    open var authorName: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "author_avatar_url")
    open var authorAvatarUrl: String? = null
        protected set

    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
    @Column(name = "message_created_at", nullable = false)
    open var messageCreatedAt: ZonedDateTime? = null
        protected set

    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
    @Column(name = "message_edited_at")
    open var messageEditedAt: ZonedDateTime? = null
        protected set

    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
    @Column(name = "message_deleted_at")
    open var messageDeletedAt: ZonedDateTime? = null
        protected set

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "references_message_id", length = 20)
    open var referencesMessageId: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Column(name = "bot_message", nullable = false)
    open var botMessage: Boolean? = null
        protected set

    @OneToMany(mappedBy = "ticketMessage")
    protected open var _attachments: MutableList<TicketMessageAttachment> = mutableListOf()

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    open var ticket: Ticket? = null

    val attachments: List<TicketMessageAttachment> get() = _attachments
    val message get() = messageId?.let { ticket?.thread?.retrieveMessageById(it) }
    val author get() = authorId?.let { DiscordBot.jda.retrieveUserById(it) }
    val referencesMessage get() = referencesMessageId?.let { ticket?.thread?.retrieveMessageById(it) }

    fun addAttachment(attachment: TicketMessageAttachment) {
        _attachments.add(attachment)
        attachment.ticketMessage = this
    }

    suspend fun content() =
        if (jsonContent != null) jsonContent else message?.await()?.contentDisplay

    suspend fun delete() = ticket?.let {
        messageDeletedAt = ZonedDateTime.now().toEuropeBerlin()

        TicketMessageService.deleteTicketMessage(it, this)
    }

    suspend fun create() = ticket?.let {
        messageCreatedAt = ZonedDateTime.now().toEuropeBerlin()

        TicketMessageService.createTicketMessage(it, this)
    }

    suspend fun update() = ticket?.let {
        messageEditedAt = ZonedDateTime.now().toEuropeBerlin()

        TicketMessageService.updateTicketMessage(it, this)
    }

    companion object {
        fun fromJda(message: Message) = TicketMessage().apply {
            this.messageId = message.id
            this.jsonContent = message.contentDisplay
            this.authorId = message.author.id
            this.authorName = message.author.name
            this.authorAvatarUrl = message.author.avatarUrl
            this.messageCreatedAt = message.timeCreated.toZonedDateTime()
            this.messageEditedAt = message.timeEdited?.toZonedDateTime()
            this.referencesMessageId = message.messageReference?.messageId
            this.botMessage = message.author.isBot

            message.attachments.map { it.toTicketMessageAttachment() }
                .forEach(::addAttachment)
        }

        fun getByMessageId(id: Long) = TicketService.tickets.asSequence()
            .flatMap { it.messages }
            .firstOrNull { it.id == id }
    }
}

fun Message.toTicketMessage() = TicketMessage.fromJda(this)
