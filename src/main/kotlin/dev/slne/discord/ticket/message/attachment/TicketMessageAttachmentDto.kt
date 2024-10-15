package dev.slne.discord.ticket.message.attachment

import dev.slne.discord.ticket.message.TicketMessageDto
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "ticket_message_attachments")
open class TicketMessageAttachmentDto {
    @Id
    @JdbcTypeCode(SqlTypes.BIGINT)
    @Column(name = "id", nullable = false)
    open var id: Long? = null
        protected set

    @Column(name = "attachment_id", nullable = false, length = 20)
    open var attachmentId: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Lob
    @Column(name = "filename", nullable = false)
    open var filename: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Lob
    @Column(name = "title")
    open var title: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "description", length = 1024)
    open var description: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "size", nullable = false)
    open var size: Int? = null
        protected set

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "content_type")
    open var contentType: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Lob
    @Column(name = "url", nullable = false)
    open var url: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Lob
    @Column(name = "proxy_url")
    open var proxyUrl: String? = null

    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "height", nullable = false)
    open var height: Int? = null
        protected set

    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "width", nullable = false)
    open var width: Int? = null
        protected set

    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Column(name = "ephemeral", nullable = false)
    open var ephemeral: Boolean? = null
        protected set

    @JdbcTypeCode(SqlTypes.FLOAT)
    @Column(name = "duration_secs", nullable = false)
    open var durationSecs: Float? = null
        protected set

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Lob
    @Column(name = "waveform")
    open var waveform: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "flags", nullable = false)
    open var flags: Int? = null
        protected set

    @ManyToOne
    @JoinColumn(name = "message_id")
    open var ticketMessageDto: TicketMessageDto? = null


}