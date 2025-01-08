package dev.slne.discord.ticket.message

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.extensions.ticket
import dev.slne.discord.getBean
import dev.slne.discord.ticket.Ticket
import dev.slne.discord.ticket.message.attachment.TicketMessageAttachment
import jakarta.persistence.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.TimeZoneStorage
import org.hibernate.annotations.TimeZoneStorageType
import org.hibernate.proxy.HibernateProxy
import org.hibernate.type.SqlTypes
import java.time.ZonedDateTime

@Entity
@Table(name = "ticket_messages")
data class TicketMessage(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JdbcTypeCode(SqlTypes.BIGINT)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "message_id", length = 20)
    val messageId: String? = null,

    @Lob
    @Column(name = "json_content")
    var jsonContent: String? = null,

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "author_id", nullable = false, length = 20)
    val authorId: String,

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "author_name", nullable = false, length = 32)
    val authorName: String,

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "author_avatar_url")
    val authorAvatarUrl: String? = null,

    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
    @Column(name = "message_created_at", nullable = false)
    var messageCreatedAt: ZonedDateTime,


    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
    @Column(name = "message_edited_at")
    var messageEditedAt: ZonedDateTime? = null,

    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
    @Column(name = "message_deleted_at")
    var messageDeletedAt: ZonedDateTime? = null,

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "references_message_id", length = 20)
    val referencesMessageId: String? = null,

    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Column(name = "bot_message", nullable = false)
    val botMessage: Boolean,

    @OneToMany(
        mappedBy = "ticketMessage",
        cascade = [CascadeType.ALL],
        fetch = FetchType.EAGER
    )
    private val _attachments: MutableList<TicketMessageAttachment> = mutableListOf(),

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    val ticket: Ticket
) {

    constructor(message: Message) : this(
        messageId = message.id,
        jsonContent = message.contentDisplay,
        authorId = message.author.id,
        authorName = message.author.name,
        authorAvatarUrl = message.author.avatarUrl,
        messageCreatedAt = message.timeCreated.toZonedDateTime(),
        messageEditedAt = message.timeEdited?.toZonedDateTime(),
        referencesMessageId = message.messageReference?.messageId,
        botMessage = message.author.isBot,
        ticket = message.channel.ticket
    )

    val attachments: List<TicketMessageAttachment> get() = _attachments
    val message get() = messageId?.let { ticket.thread?.retrieveMessageById(it) }
    val author get() = getBean<JDA>().retrieveUserById(authorId)
    val referencedMessage get() = referencesMessageId?.let { ticket.thread?.retrieveMessageById(it) }

    fun addAttachment(attachment: TicketMessageAttachment) {
        _attachments.add(attachment)
        attachment.ticketMessage = this
    }

    suspend fun content() =
        if (jsonContent != null) jsonContent else message?.await()?.contentDisplay

    suspend fun copyAndDelete(): TicketMessage {
        val copy = copy()

        copy.messageDeletedAt = ZonedDateTime.now()

        return copy
    }

    suspend fun copyAndUpdate(message: Message) = copy().apply {
        jsonContent = message.contentDisplay
        messageCreatedAt = message.timeCreated.toZonedDateTime()
        messageEditedAt = message.timeEdited?.toZonedDateTime()
    }

    private fun copy() = TicketMessage(
        authorId = authorId,
        authorName = authorName,
        authorAvatarUrl = authorAvatarUrl,
        messageCreatedAt = messageCreatedAt,
        messageEditedAt = messageEditedAt,
        messageDeletedAt = messageDeletedAt,
        referencesMessageId = referencesMessageId,
        botMessage = botMessage,
        ticket = ticket,
        messageId = messageId,
        jsonContent = jsonContent,
        _attachments = _attachments.toMutableList()
    )

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as TicketMessage

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    override fun toString(): String {
        return "TicketMessage(messageDeletedAt=$messageDeletedAt, messageEditedAt=$messageEditedAt, _attachments=$_attachments, botMessage=$botMessage, referencesMessageId=$referencesMessageId, messageCreatedAt=$messageCreatedAt, authorAvatarUrl=$authorAvatarUrl, authorName='$authorName', authorId='$authorId', jsonContent=$jsonContent, messageId=$messageId, id=$id)"
    }

}
