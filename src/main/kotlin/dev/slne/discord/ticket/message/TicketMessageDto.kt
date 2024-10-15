package dev.slne.discord.ticket.message

import dev.slne.discord.ticket.TicketDto
import dev.slne.discord.ticket.message.attachment.TicketMessageAttachmentDto
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.TimeZoneStorage
import org.hibernate.annotations.TimeZoneStorageType
import org.hibernate.type.SqlTypes
import java.io.Serializable
import java.time.ZonedDateTime

@Entity
@Table(name = "ticket_messages")
open class TicketMessageDto : Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JdbcTypeCode(SqlTypes.BIGINT)
    @Column(name = "id", nullable = false)
    open var id: Long? = null
        protected set


    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "message_id", length = 20)
    open var messageId: String? = null

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
//    @Lob
    @Column(name = "json_content")
    open var jsonContent: String? = null


    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "author_id", nullable = false, length = 20)
    open var authorId: String? = null

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "author_name", nullable = false, length = 32)
    open var authorName: String? = null


    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "author_avatar_url")
    open var authorAvatarUrl: String? = null

    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
    @Column(name = "message_created_at", nullable = false)
    open var messageCreatedAt: ZonedDateTime? = null

    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
    @Column(name = "message_edited_at")
    open var messageEditedAt: ZonedDateTime? = null

    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
    @Column(name = "message_deleted_at")
    open var messageDeletedAt: ZonedDateTime? = null

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "references_message_id", length = 20)
    open var referencesMessageId: String? = null

    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Column(name = "bot_message", nullable = false)
    open var botMessage: Boolean? = null
        protected set

    @OneToMany(mappedBy = "ticketMessageDto")
    open var tests: MutableList<TicketMessageAttachmentDto> = mutableListOf()

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    open var ticketDto: TicketDto? = null
}